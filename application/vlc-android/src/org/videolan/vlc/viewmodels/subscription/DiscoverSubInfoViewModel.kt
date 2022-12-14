/*
 * ************************************************************************
 *  DiscoverSubInfoViewModel.kt
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.vlc.gui.discover.SubscriptionInfoActivity
import org.videolan.vlc.media.PlaylistManager
import org.videolan.vlc.media.SubscriptionEpisode

class DiscoverSubInfoViewModel(private val context: Context, val media: MediaWrapper) :  ViewModel()  {

    val subscriptionEpisode: MutableLiveData<SubscriptionEpisode> = MutableLiveData()

    suspend fun loadEpisodeSubs() {
        val subs = withContext(Dispatchers.IO) {
            media.subscriptions.toList()
        }
        subscriptionEpisode.value = SubscriptionEpisode(media, subs, PlaylistManager.hasMedia(media.uri))
    }



    class Factory(val context: Context, val media: MediaWrapper) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DiscoverSubInfoViewModel(context.applicationContext, media) as T
        }
    }
}

internal fun SubscriptionInfoActivity.getViewModel(media: MediaWrapper) = ViewModelProvider(this, DiscoverSubInfoViewModel.Factory(this, media))[DiscoverSubInfoViewModel::class.java]
