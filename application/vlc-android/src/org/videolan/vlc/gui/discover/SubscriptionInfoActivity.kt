/*
 * ************************************************************************
 *  SubscriptionInfoActivity.kt
 * *************************************************************************
 * Copyright © 2022 VLC authors and VideoLAN
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

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.resources.util.parcelable
import org.videolan.vlc.R
import org.videolan.vlc.databinding.SubscriptionInfoActivityBinding
import org.videolan.vlc.gui.AudioPlayerContainerActivity
import org.videolan.vlc.gui.browser.KEY_MEDIA
import org.videolan.vlc.gui.helpers.AudioUtil
import org.videolan.vlc.gui.helpers.UiTools
import org.videolan.vlc.gui.helpers.setAlpha
import org.videolan.vlc.media.MediaUtils
import org.videolan.vlc.media.PlaylistManager
import org.videolan.vlc.media.SubscriptionEpisode
import org.videolan.vlc.util.getScreenWidth
import org.videolan.vlc.viewmodels.subscription.DiscoverSubInfoViewModel
import org.videolan.vlc.viewmodels.subscription.getViewModel

class SubscriptionInfoActivity : AudioPlayerContainerActivity() {
    private lateinit var viewModel: DiscoverSubInfoViewModel
    private lateinit var binding: SubscriptionInfoActivityBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.subscription_info_activity)
        val media = if (savedInstanceState != null)
            savedInstanceState.parcelable<Parcelable>(KEY_MEDIA) as MediaWrapper
        else
            intent.parcelable<Parcelable>(KEY_MEDIA) as MediaWrapper

        val subscriptionEpisode = SubscriptionEpisode(media, listOf(), PlaylistManager.hasMedia(media.uri))
        binding.media = subscriptionEpisode
        viewModel = getViewModel(media)
        viewModel.subscriptionEpisode.observe(this) {
            binding.media = it
            showArtwork(this, it.artworkMrl)
        }
        lifecycleScope.launch { viewModel.loadEpisodeSubs() }

        binding.progress = subscriptionEpisode.getProgress()


        fragmentContainer = binding.songs
        binding.summary.movementMethod = LinkMovementMethod.getInstance()
        initAudioPlayerContainerActivity()

        binding.playButton.setOnClickListener {
            MediaUtils.playTracks(this, media, 0, false)
        }
    }

    private fun showArtwork(context: SubscriptionInfoActivity, artworkMrl: String) {
        lifecycleScope.launch {
            val cover = withContext(Dispatchers.IO) {
                val width = if (binding.backgroundView.width > 0) binding.backgroundView.width else context.getScreenWidth()
                AudioUtil.readCoverBitmap(Uri.decode(artworkMrl), width)
            }
            if (cover != null) {
                binding.cover = BitmapDrawable(this@SubscriptionInfoActivity.resources, cover)
                binding.appbar.setExpanded(true, true)
                val radius = 45f
                val palette = Palette.from(cover).generate()
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val paletteBackgroundColor = when (currentNightMode) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        palette.darkMutedSwatch?.rgb?.setAlpha(0.75F)
                                ?: UiTools.getColorFromAttribute(context, R.attr.audio_player_background_tint)
                    } // Night mode is not active, we're using the light theme
                    else -> {
                        palette.lightMutedSwatch?.rgb?.setAlpha(0.75F)
                                ?: UiTools.getColorFromAttribute(context, R.attr.audio_player_background_tint)
                    } // Night mode is active, we're using dark theme
                }
                UiTools.blurView(binding.backgroundView, cover, radius, paletteBackgroundColor)

                binding.songs.setBackgroundColor(paletteBackgroundColor.setAlpha(0.75F))
                val paletteColor = when (currentNightMode) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        palette.getDarkVibrantColor(Color.BLACK)
                    } // Night mode is not active, we're using the light theme
                    else -> {
                        palette.getLightVibrantColor(Color.WHITE)
                    } // Night mode is active, we're using dark theme
                }
                val paletteMutedColor = when (currentNightMode) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        palette.getDarkMutedColor(Color.BLACK)
                    } // Night mode is not active, we're using the light theme
                    else -> {
                        palette.getLightMutedColor(Color.WHITE)
                    } // Night mode is active, we're using dark theme
                }
                binding.summary.setLinkTextColor(ColorStateList.valueOf(paletteColor))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) arrayOf(binding.playButton, binding.downloadButton, binding.playqueueButton).forEach {

                    val colorStateList = ColorStateList.valueOf(paletteColor)
                    it.backgroundTintList = colorStateList
                    it.setColor(paletteColor, paletteMutedColor)
                }
            }
        }
    }

    override fun isTransparent() = true

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_MEDIA, viewModel.media)
        super.onSaveInstanceState(outState)
    }
}