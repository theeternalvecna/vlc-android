/*
 * ************************************************************************
 *  TimeUtils.kt
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

/*
 * ************************************************************************
 *  TimeUtils.kt
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

package org.videolan.vlc.util

import android.content.Context
import android.text.format.DateUtils
import org.videolan.vlc.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtils {

    /**
     * Format a timestamp to a natural string (Today, yesterday, 2 weeks ago, ...)
     *
     * @param context the context to use to retrieve the strings
     * @param timestamp the timestamp to format in ms
     * @return a formated string
     */
    fun formatDateToNatural(context: Context, timestamp: Long): String {
        if (DateUtils.isToday(timestamp)) return context.getString(R.string.today)
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        val released = Calendar.getInstance(Locale.ENGLISH).apply { timeInMillis = timestamp }
        if (released.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && released.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) return context.getString(R.string.yesterday)
        if (System.currentTimeMillis() - timestamp < TimeUnit.DAYS.toMillis(7)) return SimpleDateFormat("EEEE", Locale.getDefault()).format(timestamp)
        val nbWeeks = (System.currentTimeMillis() - timestamp) / TimeUnit.DAYS.toMillis(7)
        if (nbWeeks < 4) return context.resources.getQuantityString(R.plurals.nb_weeks_ago, nbWeeks.toInt(), nbWeeks.toInt())
        val today = Calendar.getInstance()
        if (today.get(Calendar.YEAR) == released.get(Calendar.YEAR))
            return context.getString(R.string.in_month, SimpleDateFormat("MMMM", Locale.getDefault()).format(timestamp))
        return context.getString(R.string.in_year, SimpleDateFormat("yyyy", Locale.getDefault()).format(timestamp))
    }
}