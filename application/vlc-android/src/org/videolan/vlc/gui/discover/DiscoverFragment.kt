/*
 * ************************************************************************
 *  DiscoverFragment.kt
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

import android.content.Intent
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.medialibrary.interfaces.media.VideoGroup
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.CTX_DELETE
import org.videolan.resources.CTX_FEED_FLAGS
import org.videolan.resources.CTX_MARK_AS_PLAYED
import org.videolan.resources.CTX_MARK_AS_UNPLAYED
import org.videolan.tools.MultiSelectHelper
import org.videolan.vlc.gui.browser.KEY_MEDIA
import org.videolan.vlc.gui.browser.MediaBrowserFragment
import org.videolan.vlc.gui.dialogs.CtxActionReceiver
import org.videolan.vlc.gui.dialogs.showContext
import org.videolan.vlc.gui.helpers.UiTools
import org.videolan.vlc.viewmodels.MedialibraryViewModel

abstract class DiscoverFragment<T : MedialibraryViewModel>: MediaBrowserFragment<T>(), SwipeRefreshLayout.OnRefreshListener, CtxActionReceiver {
    lateinit var multiSelectHelper: MultiSelectHelper<MediaLibraryItem>


    override fun getTitle() = ""
    abstract fun getRootView():View
    abstract fun scrollToTop()

    override fun onRefresh() {
       viewModel.refresh()
    }

    fun DiscoverAction.process() {
        when (this) {
            is DiscoverClick -> {
                onClick(position, item)
            }
            is DiscoverLongClick -> {
                if ((item is VideoGroup && item.presentCount == 0)) UiTools.snackerMissing(requireActivity()) else onLongClick(position)
            }
            is DiscoverCtxClick -> {
                when (item) {
                    is Subscription -> {
                        var flags = CTX_DELETE
                        showContext(requireActivity(), this@DiscoverFragment, position, item, flags)
                    }
                    is MediaWrapper -> {
                        var flags = CTX_FEED_FLAGS
                        flags = if (item.seen > 0) flags or CTX_MARK_AS_UNPLAYED else  flags or CTX_MARK_AS_PLAYED
                        showContext(requireActivity(), this@DiscoverFragment, position, item, flags)
                    }
                }
            }
            is DiscoverImageClick -> {
                if (actionMode != null) {
                    onClick(position, item)
                } else {
                    onLongClick(position)
                }
            }
            is DiscoverPlayClick -> {
                (this@DiscoverFragment as DiscoverFeedFragment).play(position, item as MediaWrapper)
            }
            is DiscoverAddPlayQueueClick -> {
                if (item is MediaWrapper) {
                    (this@DiscoverFragment as DiscoverFeedFragment).appendQueue(position, item)
                }
            }
        }
    }

    private fun onClick(position: Int, item: MediaLibraryItem) {
        if (actionMode != null) {
            multiSelectHelper.toggleSelection(position)
            invalidateActionMode()
        } else when (item) {
            is Subscription -> {
                //todo open the subscription screen
            }
            is MediaWrapper -> {
                val i = Intent(requireActivity(), SubscriptionInfoActivity::class.java)
                i.putExtra(KEY_MEDIA, item)
                startActivity(i)
            }
        }
    }

    private fun onLongClick(position: Int) {
        if (actionMode == null && inSearchMode()) UiTools.setKeyboardVisibility(getRootView(), false)
        multiSelectHelper.toggleSelection(position, true)
        if (actionMode == null) startActionMode() else invalidateActionMode()
    }

    sealed class DiscoverAction
    class DiscoverClick(val position: Int, val item: MediaLibraryItem) : DiscoverAction()
    class DiscoverLongClick(val position: Int, val item: MediaLibraryItem) : DiscoverAction()
    class DiscoverCtxClick(val position: Int, val item: MediaLibraryItem) : DiscoverAction()
    class DiscoverImageClick(val position: Int, val item: MediaLibraryItem) : DiscoverAction()
    class DiscoverPlayClick(val position: Int, val item: MediaLibraryItem) : DiscoverAction()
    class DiscoverAddPlayQueueClick(val position: Int, val item: MediaLibraryItem) : DiscoverAction()
}