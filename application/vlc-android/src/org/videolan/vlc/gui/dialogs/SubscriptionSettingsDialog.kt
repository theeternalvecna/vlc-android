/*
 * ************************************************************************
 *  SubscriptionSettingsDialog.kt
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

package org.videolan.vlc.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.vlc.databinding.DialogSubscriptionSettingsBinding
import org.videolan.vlc.gui.preferences.PreferencesSubscriptions

class SubscriptionSettingsDialog : VLCBottomSheetDialogFragment() {

    override fun getDefaultState(): Int = STATE_EXPANDED

    override fun needToManageOrientation(): Boolean = true

    private lateinit var subscription:Subscription
    private lateinit var binding: DialogSubscriptionSettingsBinding


    override fun initialFocusedView() = binding.container

    override fun onCreate(savedInstanceState: Bundle?) {
        subscription = requireArguments().getParcelable(SUBSCRIPTION_KEY) ?: throw java.lang.IllegalStateException("Invalid subscription")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogSubscriptionSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
                .beginTransaction()
                .replace(binding.fragmentPlaceholder.id, PreferencesSubscriptions.newInstance(subscription), "PreferencesSubscriptions")
                .commit()
    }

    companion object {

        private const val SUBSCRIPTION_KEY = "subscription_key"

        fun newInstance(subscription:Subscription): SubscriptionSettingsDialog {
            return SubscriptionSettingsDialog().apply { arguments = bundleOf(SUBSCRIPTION_KEY to subscription) }
        }
    }
}
