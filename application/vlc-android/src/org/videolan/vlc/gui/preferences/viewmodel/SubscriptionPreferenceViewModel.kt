/*
 * ************************************************************************
 *  SubscriptionPreferenceViewModel.kt
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

package org.videolan.vlc.gui.preferences.viewmodel

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.vlc.gui.preferences.PreferencesSubscriptions

class SubscriptionPreferenceViewModel(context: Context, private val subscription: Subscription) : ViewModel() {
    val maxCacheMedia: MutableLiveData<Int?> = MutableLiveData(null)
    val newMediaNotification: MutableLiveData<Int?> = MutableLiveData(null)
    val maxCacheSize: MutableLiveData<Long?> = MutableLiveData(null)

    init {
        viewModelScope.launch { refresh() }
    }

    suspend fun refresh() = withContext(Dispatchers.IO) {
        maxCacheMedia.postValue(subscription.maxCacheMedia)
        newMediaNotification.postValue(subscription.newMediaNotification)
        maxCacheSize.postValue(subscription.maxCacheSize)
    }

    class Factory(val context: Context, val subscription: Subscription) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SubscriptionPreferenceViewModel(context.applicationContext, subscription) as T
        }
    }

    suspend fun setMaxCacheMedia(value: Int) = withContext(Dispatchers.IO) {
        subscription.maxCacheMedia = value
        refresh()
    }
    suspend fun setMaxCacheSize(value: Long) = withContext(Dispatchers.IO) {
        subscription.maxCacheSize = value
        refresh()
    }

    suspend fun setNewMediaNotification(value: Int) = withContext(Dispatchers.IO) {
        subscription.newMediaNotification = value
        refresh()
    }

}

internal fun PreferencesSubscriptions.getViewModel(subscription: Subscription) = ViewModelProvider(requireActivity(), SubscriptionPreferenceViewModel.Factory(requireContext(), subscription)).get(SubscriptionPreferenceViewModel::class.java)
