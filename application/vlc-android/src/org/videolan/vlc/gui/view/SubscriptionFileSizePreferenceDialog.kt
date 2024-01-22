/*
 * ************************************************************************
 *  SubscriptionFileSizePreferenceDialog.kt
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
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceDialogFragmentCompat
import org.videolan.vlc.R
import kotlin.math.log10
import kotlin.math.pow

class SubscriptionFileSizePreferenceDialog : PreferenceDialogFragmentCompat() {
    private lateinit var number: EditText
    private lateinit var unit: Spinner
    private lateinit var followParentSwitch: SwitchCompat

    override fun onCreateDialogView(context: Context?): View {
        val container = layoutInflater.inflate(R.layout.pref_subscription_file_size, null)
        followParentSwitch = container.findViewById(R.id.follow_parent_switch)
        number = container.findViewById(R.id.number)
        unit = container.findViewById(R.id.unit)
        return container
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        val size = (preference as SubscriptionFileSizePreference).getPersistedLong()

        followParentSwitch.setOnCheckedChangeListener { _, isChecked ->
            number.isEnabled = !isChecked
            unit.isEnabled = !isChecked
            unit.setSelection(0)
            number.setText("")
        }
        followParentSwitch.isChecked = size < 0L

        val spinnerArrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, FileSizeUnit.values())
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unit.adapter = spinnerArrayAdapter

        if (size > 0) when ((log10(size.toDouble()) / log10(1000.0)).toInt()) {
            in 0..2 -> {
                unit.setSelection(0)
                number.setText((size.toDouble() / FileSizeUnit.MB.multiplier).toString())
            }
            else -> {
                unit.setSelection(1)
                number.setText((size.toDouble() / FileSizeUnit.GB.multiplier).toString())
            }
        }

    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            try {
                val newValue: Long = if(followParentSwitch.isChecked) -1L else (number.text.toString().replace(",", ".").toDouble() * (unit.selectedItem as FileSizeUnit).multiplier).toLong()
                if (preference.callChangeListener(newValue)) {
                    (preference as SubscriptionFileSizePreference).doPersistLong(newValue)
                    preference.summary
                }
            } catch (e: NumberFormatException) {
                //the user used an invalid value -> don't save anything
            }
        }
    }

    companion object {
        fun newInstance(key: String): SubscriptionFileSizePreferenceDialog {
            val fragment = SubscriptionFileSizePreferenceDialog()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle

            return fragment
        }
    }
}

/**
 * Enum describing a file size unit
 *
 * @property title the title to be displayed
 * @property multiplier the multiplier allowing to go from a byte to this unit
 */
enum class FileSizeUnit(val title: String, val multiplier: Long) {
    MB("MB", 1000.0.pow(2).toLong()),
    GB("GB", 1000.0.pow(3).toLong());
    override fun toString() = title

}