/*
 * ************************************************************************
 *  DiscoverFeedViewModel.kt
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

package org.videolan.vlc.viewmodels.subscription

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.vlc.gui.discover.DiscoverFeedFragment
import org.videolan.vlc.media.MediaUtils
import org.videolan.vlc.providers.medialibrary.DiscoverFeedProvider
import org.videolan.vlc.providers.medialibrary.MedialibraryProvider
import org.videolan.vlc.viewmodels.MedialibraryViewModel

class DiscoverFeedViewModel(context: Context) : MedialibraryViewModel(context)  {

    init {
        sort = Medialibrary.SORT_RELEASEDATE
        desc = true
    }
    suspend fun markAsPlayed(media: MediaWrapper) = withContext(Dispatchers.IO){
        if (media.seen == 0L) media.setPlayCount(1L)
    }

    suspend fun markAsUnplayed(media: MediaWrapper) = withContext(Dispatchers.IO) {
       media.setPlayCount(0L)
    }

    fun appendMedia(item: MediaWrapper, position: Int) {
        MediaUtils.appendMedia(context, item)
        provider.appendMedia(position)
    }

    fun play(item: MediaWrapper, position: Int) {
        MediaUtils.playTracks(context, item, 0, false)
        provider.appendMedia(position)
    }

    val provider = DiscoverFeedProvider(context, this)
    override val providers: Array<MedialibraryProvider<out MediaLibraryItem>> = arrayOf(provider)

    init {
        watchSubscriptions()
        watchMedia()
    }


    class Factory(val context: Context) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DiscoverFeedViewModel(context.applicationContext) as T
        }
    }
}

internal fun DiscoverFeedFragment.getViewModel() = ViewModelProvider(requireActivity(), DiscoverFeedViewModel.Factory(requireContext()))[DiscoverFeedViewModel::class.java]
