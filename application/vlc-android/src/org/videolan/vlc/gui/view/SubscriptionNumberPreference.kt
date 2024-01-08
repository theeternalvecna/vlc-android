/*
 * ************************************************************************
 *  SubscriptionNumberPreference.kt
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
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import org.videolan.vlc.R

/**
 * Custom preference letting the user choose a number with the following values
 * -1 is follow parent
 * 0 is none
 * 1 - 100 is displayed as a number of media
 *
 */
class SubscriptionNumberPreference(context: Context, attrs: AttributeSet?) : DialogPreference(context, attrs) {

    override fun getSummary(): CharSequence {
        return when(getPersistedInt()) {
            -1 -> context.getString(R.string.follow_parent)
            0 -> context.getString(R.string.none)
            else -> context.resources.getQuantityString(R.plurals.media_quantity, getPersistedInt(), getPersistedInt())
        }
    }

    fun getPersistedInt() = super.getPersistedInt(FALLBACK_DEFAULT_VALUE)

    fun doPersistInt(value: Int) {
        super.persistInt(value)
        notifyChanged()
    }

    /**
     * Saves the text to the current data storage.
     *
     * @param text The text to save
     */
    fun setValue(value:Int) {
        val wasBlocking = shouldDisableDependents()
        persistInt(value)
        val isBlocking = shouldDisableDependents()
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking)
        }
        notifyChanged()
    }

    override fun onSetInitialValue(restore: Boolean, defaultValue: Any?) {
        onSetInitialValue(defaultValue)
        setValue(if (restore) getPersistedInt(FALLBACK_DEFAULT_VALUE) else defaultValue as Int)
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any? {
        return a?.getInteger(index, FALLBACK_DEFAULT_VALUE)
    }

    companion object {
        const val FALLBACK_DEFAULT_VALUE = -1
        const val MIN_VALUE = 0
        const val MAX_VALUE = 100
    }
}