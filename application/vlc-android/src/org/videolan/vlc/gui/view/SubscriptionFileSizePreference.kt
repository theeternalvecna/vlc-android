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
import org.videolan.tools.readableSize
import org.videolan.vlc.R

/**
 * Custom preference letting the user choose a file size
 *
 */
class SubscriptionFileSizePreference(context: Context, attrs: AttributeSet?) : DialogPreference(context, attrs) {

    override fun getSummary()= when(getPersistedLong()) {
        in Long.MIN_VALUE until 0L -> context.getString(R.string.follow_parent)
        0L -> context.getString(R.string.none)
        else -> getPersistedLong().readableSize()
    }

    fun getPersistedLong() = super.getPersistedLong(FALLBACK_DEFAULT_VALUE)

    fun doPersistLong(value: Long) {
        super.persistLong(value)
        notifyChanged()
    }

    /**
     * Saves the value to the current data storage.
     *
     * @param value The value to save
     */
    fun setValue(value:Long) {
        val wasBlocking = shouldDisableDependents()
        persistLong(value)
        val isBlocking = shouldDisableDependents()
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking)
        }
        notifyChanged()
    }

    override fun onSetInitialValue(restore: Boolean, defaultValue: Any?) {
        onSetInitialValue(defaultValue)
        setValue(if (restore) getPersistedLong(FALLBACK_DEFAULT_VALUE) else defaultValue as Long)
    }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any? {
        return a?.getString(index)?.toLong() ?: (500L * 1024L * 1024L)
    }

    companion object {
        const val FALLBACK_DEFAULT_VALUE = 0L
    }
}