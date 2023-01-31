/*
 * ************************************************************************
 *  PreferencesSubscriptions.kt
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

package org.videolan.vlc.gui.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceScreen
import kotlinx.coroutines.launch
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.vlc.R
import org.videolan.vlc.gui.preferences.viewmodel.SubscriptionPreferenceViewModel
import org.videolan.vlc.gui.preferences.viewmodel.getViewModel
import org.videolan.vlc.gui.view.SubscriptionFileSizePreference
import org.videolan.vlc.gui.view.SubscriptionNumberPreference

class PreferencesSubscriptions : BasePreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var viewModel: SubscriptionPreferenceViewModel
    private lateinit var nbMediaCache: SubscriptionNumberPreference
    private lateinit var maxCacheSize: SubscriptionFileSizePreference
    private lateinit var subscription: Subscription

    override fun getXml() = R.xml.preferences_subscriptions

    override fun getTitleId() = R.string.subscription_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscription = requireArguments().getParcelable(SUBSCRIPTION_KEY)
                ?: throw java.lang.IllegalStateException("Invalid subscription")

        manageDisableState(true)

        nbMediaCache = findPreference("subscription_nb_cache_media")
                ?: throw java.lang.IllegalStateException()
        maxCacheSize = findPreference("subscription_max_cache_size")
                ?: throw java.lang.IllegalStateException()
        viewModel = getViewModel(subscription)

        viewModel.maxCacheMedia.observe(this) {value ->
            value?.let {
                nbMediaCache.setValue(it)
                manageDisableState(false)
            }
        }
        viewModel.maxCacheSize.observe(this) {value ->
            value?.let {
                maxCacheSize.setValue(it)
                manageDisableState(false)
            }
        }
        viewModel.newMediaNotification.observe(this) {value ->
            value?.let {
                findPreference<ListPreference>("subscription_enable_notification")?.value = when (it) {
                    0 -> "0"
                    in 1..Int.MAX_VALUE -> "1"
                    else -> "-1"
                }
                manageDisableState(false)
            }
        }
    }

    private fun manageDisableState(shouldDisable:Boolean) {
        findPreference<PreferenceScreen>("subscription_preferences")?.let { parent->
            for (i in 0 until parent.preferenceCount) {
                parent.getPreference(i).isEnabled = !shouldDisable
            }
        }
    }


    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "subscription_nb_cache_media" -> {
                lifecycleScope.launch { viewModel.setMaxCacheMedia(sharedPreferences.getInt(key, -1)) }
            }
            "subscription_enable_notification" -> {
                lifecycleScope.launch { viewModel.setNewMediaNotification((sharedPreferences.getString(key, "0") ?: "0").toInt()) }
            }
            "subscription_max_cache_size" -> {
                lifecycleScope.launch { viewModel.setMaxCacheSize(sharedPreferences.getLong(key, -1L)) }
            }
        }
    }


    companion object {

        private const val SUBSCRIPTION_KEY = "subscription_key"

        fun newInstance(subscription: Subscription): PreferencesSubscriptions {
            return PreferencesSubscriptions().apply { arguments = bundleOf(SUBSCRIPTION_KEY to subscription) }
        }
    }
}
