/*
 * ************************************************************************
 *  ServiceContentViewModel.kt
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
import org.videolan.medialibrary.interfaces.media.DiscoverService
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.vlc.gui.discover.DiscoverServiceFragment
import org.videolan.vlc.providers.medialibrary.MedialibraryProvider
import org.videolan.vlc.providers.medialibrary.ServiceContentProvider
import org.videolan.vlc.viewmodels.MedialibraryViewModel

class ServiceContentViewModel(context: Context, private val service: DiscoverService, var inCards:Boolean) : MedialibraryViewModel(context)  {
    val provider = ServiceContentProvider(service, context, this)
    override val providers: Array<MedialibraryProvider<out MediaLibraryItem>> = arrayOf(provider)

    init {
        watchSubscriptions()
    }

    /**
     * Refresh the whole service. Should be moved to a Worker later
     *
     */
    suspend fun updateService() {
        provider.loading.postValue(true)
        withContext(Dispatchers.IO) {
            service.refresh()
        }
        provider.loading.postValue(false)
    }

    class Factory(val context: Context, private val service: DiscoverService, private val inCards:Boolean) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ServiceContentViewModel(context.applicationContext, service, inCards) as T
        }
    }
}

internal fun DiscoverServiceFragment.getViewModel(service: DiscoverService, inCards:Boolean) = ViewModelProvider(requireActivity(), ServiceContentViewModel.Factory(requireContext(), service, inCards))[ServiceContentViewModel::class.java]
