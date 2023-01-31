/*
 * ************************************************************************
 *  SubscriptionNumberPreferenceDialog.kt
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

package org.videolan.vlc.gui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceDialogFragmentCompat
import org.videolan.vlc.R

class SubscriptionNumberPreferenceDialog : PreferenceDialogFragmentCompat() {
    private lateinit var numberPicker: NumberPicker
    private lateinit var followParentSwitch: SwitchCompat

    override fun onCreateDialogView(context: Context?): View {
        val container = layoutInflater.inflate(R.layout.pref_subscription_number, null)
        numberPicker = container.findViewById(R.id.number_picker)
        followParentSwitch = container.findViewById(R.id.follow_parent_switch)
        numberPicker.minValue = SubscriptionNumberPreference.MIN_VALUE
        numberPicker.maxValue = SubscriptionNumberPreference.MAX_VALUE



        return container
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        val currentValue = (preference as SubscriptionNumberPreference).getPersistedInt()
        followParentSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            numberPicker.isEnabled = !isChecked
        }
        when (currentValue) {
            -1 -> {
                followParentSwitch.isChecked = true
                numberPicker.value = 2
            }
            else -> {
                followParentSwitch.isChecked = false
                numberPicker.value = currentValue
            }
        }

    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            numberPicker.clearFocus()
            val newValue: Int = if (followParentSwitch.isChecked) -1 else numberPicker.value
            if (preference.callChangeListener(newValue)) {
                (preference as SubscriptionNumberPreference).doPersistInt(newValue)
                preference.summary
            }
        }
    }

    companion object {
        fun newInstance(key: String): SubscriptionNumberPreferenceDialog {
            val fragment = SubscriptionNumberPreferenceDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}