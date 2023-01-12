package org.videolan.vlc.gui.discover

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.*
import org.videolan.tools.MultiSelectHelper
import org.videolan.tools.SUBSCRIPTION_CARD_MODE
import org.videolan.tools.Settings
import org.videolan.tools.dp
import org.videolan.vlc.R
import org.videolan.vlc.databinding.SubscriptionGridBinding
import org.videolan.vlc.gui.dialogs.SavePlaylistDialog
import org.videolan.vlc.gui.helpers.ItemOffsetDecoration
import org.videolan.vlc.gui.helpers.UiTools
import org.videolan.vlc.gui.helpers.UiTools.addToPlaylist
import org.videolan.vlc.gui.helpers.UiTools.createShortcut
import org.videolan.vlc.gui.view.EmptyLoadingState
import org.videolan.vlc.media.MediaUtils
import org.videolan.vlc.util.Permissions
import org.videolan.vlc.util.launchWhenStarted
import org.videolan.vlc.viewmodels.PlaylistModel
import org.videolan.vlc.viewmodels.subscription.DiscoverFeedViewModel
import org.videolan.vlc.viewmodels.subscription.getViewModel

private const val TAG = "VLC/DiscFeedFr"

class DiscoverFeedFragment : DiscoverFragment<DiscoverFeedViewModel>() {

    private lateinit var feedAdapter: DiscoverAdapter
    private lateinit var binding: SubscriptionGridBinding
    private var gridItemDecoration: RecyclerView.ItemDecoration? = null
    private lateinit var settings: SharedPreferences
    override fun hasFAB() = false

    companion object {
        fun newInstance() = DiscoverFeedFragment()
    }

    override fun getRootView() = binding.root


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!::settings.isInitialized) settings = Settings.getInstance(requireContext())
        viewModel = getViewModel()

        feedAdapter = DiscoverAdapter().apply { stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY }
        multiSelectHelper = feedAdapter.multiSelectHelper
        val playlistModel = PlaylistModel.get(this)
        playlistModel.progress.observe(this) { progress ->
            playlistModel.currentMediaWrapper?.uri?.let { uri ->
                feedAdapter.setProgress(uri, progress.time)
                val item = feedAdapter.currentList?.toTypedArray()?.first { it is MediaWrapper && it.uri == uri }
                feedAdapter.notifyItemChanged(feedAdapter.currentList?.toTypedArray()?.indexOf(item)
                        ?: 0, UPDATE_PROGRESS)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SubscriptionGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.action_mode_discover_feed, menu)
        multiSelectHelper.toggleActionMode(true, feedAdapter.itemCount)
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
        val item = menuItem ?: return false
        val list = multiSelectHelper.getSelection().map { it as MediaWrapper }

        when (item.itemId) {
            R.id.action_feed_play -> {
                MediaUtils.openList(requireActivity(), list, 0, false)
            }
            else -> return false
        }
        stopActionMode()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        setFabPlayVisibility(true)
        multiSelectHelper.clearSelection()
        multiSelectHelper.toggleActionMode(false, feedAdapter.itemCount)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.videoGrid.adapter = feedAdapter
        swipeRefreshLayout.setOnRefreshListener(this)
        viewModel.provider.pagedList.observe(requireActivity()) {
            feedAdapter.clearRefreshes()
            @Suppress("UNCHECKED_CAST")
            (it as? PagedList<MediaLibraryItem>)?.let { pagedList -> feedAdapter.submitList(pagedList) }
            updateEmptyView()
            restoreMultiSelectHelper()
        }
        viewModel.provider.loading.observe(requireActivity()) { loading ->
            if (isResumed) setRefreshing(loading) { refresh ->
                if (!refresh) {
                    menu?.let { UiTools.updateSortTitles(it, viewModel.provider) }
                    restoreMultiSelectHelper()
                }
            }
        }
        feedAdapter.events.onEach { it.process() }.launchWhenStarted(lifecycleScope)

    }

    override fun onStart() {
        super.onStart()
        updateViewMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        gridItemDecoration = null
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.ml_menu_sortby).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun scrollToTop() {
        binding.videoGrid.smoothScrollToPosition(0)
    }

    private fun updateViewMode() {
        if (view == null || activity == null) {
            Log.w(TAG, "Unable to setup the view")
            return
        }
        val res = resources
        if (gridItemDecoration == null) gridItemDecoration = ItemOffsetDecoration(resources, R.dimen.left_right_1610_margin, R.dimen.top_bottom_1610_margin)
        val listMode = !settings.getBoolean(SUBSCRIPTION_CARD_MODE, false)

        // Select between grid or list
        binding.videoGrid.removeItemDecoration(gridItemDecoration!!)
        if (!listMode) {
            val thumbnailWidth = res.getDimensionPixelSize(R.dimen.grid_card_thumb_width)
            val margin = binding.videoGrid.paddingStart + binding.videoGrid.paddingEnd
            val columnWidth = binding.videoGrid.getPerfectColumnWidth(thumbnailWidth, margin) - res.getDimensionPixelSize(R.dimen.left_right_1610_margin) * 2
            binding.videoGrid.columnWidth = columnWidth
            binding.videoGrid.addItemDecoration(gridItemDecoration!!)
            binding.videoGrid.setPadding(4.dp, 4.dp, 4.dp, 4.dp)
        } else {
            binding.videoGrid.setPadding(0, 0, 0, 0)
        }
        binding.videoGrid.setNumColumns(if (listMode) 1 else -1)
        feedAdapter.isListMode = listMode
    }

    override fun getTitle() = ""

    private fun updateEmptyView() {
        if (!::binding.isInitialized) return
        if (!isAdded) return
        val empty = viewModel.isEmpty() && feedAdapter.currentList.isNullOrEmpty()
        val working = viewModel.provider.loading.value != false
        binding.emptyLoading.emptyText = viewModel.filterQuery?.let { getString(R.string.empty_search, it) }
                ?: getString(R.string.nomedia)
        binding.emptyLoading.state = when {
            !Permissions.canReadStorage(AppContextProvider.appContext) && empty -> EmptyLoadingState.MISSING_PERMISSION
            empty && working -> EmptyLoadingState.LOADING
            empty && !working && viewModel.filterQuery == null -> EmptyLoadingState.EMPTY
            empty && !working && viewModel.filterQuery != null -> EmptyLoadingState.EMPTY_SEARCH
            else -> EmptyLoadingState.NONE
        }
        binding.empty = empty && !working
    }

    override fun getMultiHelper() = if (::feedAdapter.isInitialized) feedAdapter.multiSelectHelper as? MultiSelectHelper<DiscoverFeedViewModel> else null

    override fun onRefresh() {
        viewModel.refresh()
    }

    override fun onCtxAction(position: Int, option: Long) {
        if (position >= feedAdapter.itemCount) return
        val activity = activity ?: return
        when (val media = feedAdapter.getItem(position)) {
            is MediaWrapper -> when (option) {
                CTX_PLAY -> MediaUtils.playTracks(requireActivity(), media, 0, false)
                CTX_APPEND -> MediaUtils.appendMedia(requireContext(), media)
                CTX_PLAY_NEXT -> MediaUtils.insertNext(requireActivity(), media)
                CTX_INFORMATION -> showInfoDialog(media)
                CTX_ADD_TO_PLAYLIST -> requireActivity().addToPlaylist(media.tracks, SavePlaylistDialog.KEY_NEW_TRACKS)
                CTX_ADD_SHORTCUT -> lifecycleScope.launch { requireActivity().createShortcut(media) }
                CTX_MARK_AS_PLAYED -> lifecycleScope.launch { viewModel.markAsPlayed(media) }
                CTX_MARK_AS_UNPLAYED -> lifecycleScope.launch { viewModel.markAsUnplayed(media) }
            }
        }
    }

    fun appendQueue(position: Int, item: MediaWrapper) {
        viewModel.appendMedia(item, position)
        feedAdapter.notifyItemChanged(position)
    }
    fun play(position: Int, item: MediaWrapper) {
        viewModel.play(item, position)
        feedAdapter.notifyItemChanged(position)
    }

}