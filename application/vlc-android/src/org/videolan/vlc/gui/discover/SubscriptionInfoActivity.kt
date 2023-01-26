/*
 * ************************************************************************
 *  SubscriptionInfoActivity.kt
 * *************************************************************************
 * Copyright © 2023 VLC authors and VideoLAN
 * Author: Nicolas POMEPUY
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 * **************************************************************************
 *
 *
 */

package org.videolan.vlc.gui.discover

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.medialibrary.interfaces.media.VideoGroup
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.*
import org.videolan.resources.util.parcelable
import org.videolan.tools.MultiSelectHelper
import org.videolan.tools.dp
import org.videolan.tools.setGone
import org.videolan.tools.setVisible
import org.videolan.vlc.R
import org.videolan.vlc.databinding.SubscriptionInfoActivityBinding
import org.videolan.vlc.gui.ContentActivity
import org.videolan.vlc.gui.browser.KEY_MEDIA
import org.videolan.vlc.gui.dialogs.CtxActionReceiver
import org.videolan.vlc.gui.dialogs.SavePlaylistDialog
import org.videolan.vlc.gui.dialogs.showContext
import org.videolan.vlc.gui.helpers.AudioUtil
import org.videolan.vlc.gui.helpers.UiTools
import org.videolan.vlc.gui.helpers.UiTools.addToPlaylist
import org.videolan.vlc.gui.helpers.UiTools.createShortcut
import org.videolan.vlc.gui.helpers.linkify
import org.videolan.vlc.interfaces.Filterable
import org.videolan.vlc.media.MediaUtils
import org.videolan.vlc.util.launchWhenStarted
import org.videolan.vlc.viewmodels.mobile.PlaylistViewModel
import org.videolan.vlc.viewmodels.mobile.getViewModel

class SubscriptionInfoActivity: ContentActivity(), CtxActionReceiver, ActionMode.Callback, Filterable {
    private lateinit var multiSelectHelper: MultiSelectHelper<MediaLibraryItem>
    private lateinit var feedAdapter: DiscoverAdapter
    private lateinit var binding: SubscriptionInfoActivityBinding
    private lateinit var viewModel: PlaylistViewModel
    lateinit var media:Subscription
    var actionMode: ActionMode? = null

    override val displayTitle = true

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.subscription_info_activity)
        media = if (savedInstanceState != null)
            savedInstanceState.parcelable<Parcelable>(KEY_MEDIA) as Subscription
        else
            intent.parcelable<Parcelable>(KEY_MEDIA) as Subscription

        binding.media = media
        viewModel = getViewModel(media)
        val toolbar = findViewById<MaterialToolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = ""

        feedAdapter = DiscoverAdapter(true).apply { stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY }

        binding.episodeList.layoutManager = LinearLayoutManager(this)
        binding.episodeList.adapter = feedAdapter

        viewModel.tracksProvider.pagedList.observe(this) { tracks ->
            @Suppress("UNCHECKED_CAST")
            (tracks as? PagedList<MediaLibraryItem>)?.let { feedAdapter.submitList(it) }
            menu.let { UiTools.updateSortTitles(it, viewModel.tracksProvider) }
        }

        binding.summaryMore.setOnClickListener {
            binding.summary.maxLines = Int.MAX_VALUE
            binding.summaryMore.setGone()
            binding.summaryLess.setVisible()
        }
        binding.summaryLess.setOnClickListener {
            binding.summary.maxLines = 3
            binding.summaryLess.setGone()
            binding.summaryMore.setVisible()
        }

        binding.summary.setOnLineCountChangedListener {lineCount ->
            if (lineCount <= binding.summary.maxLines)  binding.summaryMore.setGone()
        }
        binding.summary.linkify(media.summary)
        binding.summary.setLinkTextColor(ContextCompat.getColor(this, R.color.orange500))

        showArtwork(media.artworkMrl)

        feedAdapter.events.onEach { it.process() }.launchWhenStarted(lifecycleScope)
        multiSelectHelper = feedAdapter.multiSelectHelper

        fragmentContainer = binding.songs
        initAudioPlayerContainerActivity()
    }

    private fun showArtwork(artworkMrl: String) {
        lifecycleScope.launch {
            val cover = withContext(Dispatchers.IO) {
                val width = if (binding.coverCard.width > 0) binding.coverCard.width else 48.dp
                AudioUtil.readCoverBitmap(Uri.decode(artworkMrl), width)
            }
            if (cover != null) {
                val palette = Palette.from(cover).generate()

                val paletteColor = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        palette.getDarkVibrantColor(Color.BLACK)
                    } // Night mode is not active, we're using the light theme
                    else -> {
                        palette.getLightVibrantColor(Color.WHITE)
                    } // Night mode is active, we're using dark theme
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val colorStateList = ColorStateList.valueOf(paletteColor)
                    binding.summary.setLinkTextColor(colorStateList)
                    binding.summarySeparator.backgroundTintList = colorStateList
                    binding.websiteButton.imageTintList = colorStateList
                    binding.author.setTextColor(paletteColor)
                    feedAdapter.paletteColor = paletteColor
                    feedAdapter.notifyItemRangeChanged(0, feedAdapter.itemCount, UPDATE_PAYLOAD)
                }
            }
        }
    }

    fun DiscoverFragment.DiscoverAction.process() {
        when (this) {
            is DiscoverFragment.DiscoverClick -> {
                onClick(position, item)
            }
            is DiscoverFragment.DiscoverLongClick -> {
                if ((item is VideoGroup && item.presentCount == 0)) UiTools.snackerMissing(this@SubscriptionInfoActivity) else onLongClick(position)
            }
            is DiscoverFragment.DiscoverCtxClick -> {
                when (item) {
                    is Subscription -> {
                        val flags = CTX_DELETE
                        showContext(this@SubscriptionInfoActivity, this@SubscriptionInfoActivity, position, item, flags)
                    }
                    is MediaWrapper -> {
                        var flags = CTX_FEED_FLAGS
                        flags = if (item.seen > 0) flags or CTX_MARK_AS_UNPLAYED else  flags or CTX_MARK_AS_PLAYED
                        showContext(this@SubscriptionInfoActivity, this@SubscriptionInfoActivity, position, item, flags)
                    }
                }
            }
            is DiscoverFragment.DiscoverImageClick -> {
                if (actionMode != null) {
                    onClick(position, item)
                } else {
                    onLongClick(position)
                }
            }
            is DiscoverFragment.DiscoverPlayClick -> {
                play(position, item as MediaWrapper)
            }
            is DiscoverFragment.DiscoverAddPlayQueueClick -> {
                if (item is MediaWrapper) {
                    appendQueue(position, item)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.subscription_info_option, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ml_menu_subscription_filter -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun startActionMode() {
        actionMode = startSupportActionMode(this)
    }

    private fun stopActionMode() = actionMode?.let {
        it.finish()
        onDestroyActionMode(it)
    }

    private fun invalidateActionMode() {
        if (actionMode != null)
            actionMode!!.invalidate()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        feedAdapter.multiSelectHelper.toggleActionMode(true, feedAdapter.itemCount)
        mode.menuInflater?.inflate(R.menu.action_mode_discover_feed, menu)
        return true
    }


    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val list = multiSelectHelper.getSelection().map { it as MediaWrapper }

        when (item.itemId) {
            R.id.action_feed_play -> {
                MediaUtils.openList(this, list, 0, false)
            }
            else -> return false
        }
        stopActionMode()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        feedAdapter.multiSelectHelper.toggleActionMode(false, feedAdapter.itemCount)
        actionMode = null
        feedAdapter.multiSelectHelper.clearSelection()
    }


    fun play(position: Int, item: MediaWrapper) {
        viewModel.play(item, position)
        feedAdapter.notifyItemChanged(position)
    }

    private fun appendQueue(position: Int, item: MediaWrapper) {
        viewModel.appendMedia(item, position)
        feedAdapter.notifyItemChanged(position)
    }

    private fun onClick(position: Int, item: MediaLibraryItem) {
        if (actionMode != null) {
            multiSelectHelper.toggleSelection(position)
            invalidateActionMode()
        } else when (item) {
            is Subscription -> {
                val i = Intent(this, SubscriptionInfoActivity::class.java)
                i.putExtra(KEY_MEDIA, item)
                startActivity(i)
            }
            is MediaWrapper -> {
                val i = Intent(this, SubscriptionEpisodeInfoActivity::class.java)
                i.putExtra(KEY_MEDIA, item)
                startActivity(i)
            }
        }
    }

    private fun onLongClick(position: Int) {
//        if (actionMode == null && inSearchMode()) UiTools.setKeyboardVisibility(getRootView(), false)
        multiSelectHelper.toggleSelection(position, true)
        if (actionMode == null) startActionMode() else invalidateActionMode()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_MEDIA, media)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        setTabLayoutVisibility(false)
    }

    override fun onCtxAction(position: Int, option: Long) {
        if (position >= feedAdapter.itemCount) return
        when (val media = feedAdapter.getItem(position)) {
            is MediaWrapper -> when (option) {
                CTX_PLAY -> MediaUtils.playTracks(this, media, 0, false)
                CTX_APPEND -> MediaUtils.appendMedia(this, media)
                CTX_PLAY_NEXT -> MediaUtils.insertNext(this, media)
                CTX_ADD_TO_PLAYLIST -> addToPlaylist(media.tracks, SavePlaylistDialog.KEY_NEW_TRACKS)
                CTX_ADD_SHORTCUT -> lifecycleScope.launch { createShortcut(media) }
                CTX_MARK_AS_PLAYED -> lifecycleScope.launch { viewModel.markAsPlayed(media) }
                CTX_MARK_AS_UNPLAYED -> lifecycleScope.launch { viewModel.markAsUnplayed(media) }
            }
        }
    }

    override fun getFilterQuery() = viewModel.filterQuery

    override fun enableSearchOption() = true

    override fun filter(query: String) {
        viewModel.filter(query)
    }

    override fun restoreList() = viewModel.restore()

    override fun setSearchVisibility(visible: Boolean) { }

    override fun allowedToExpand() = true
}
