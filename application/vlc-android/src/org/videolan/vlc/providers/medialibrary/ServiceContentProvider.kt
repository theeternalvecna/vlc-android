/*
 * ************************************************************************
 *  ServiceContentProvider.kt
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


package org.videolan.vlc.providers.medialibrary

import android.content.Context
import org.videolan.medialibrary.interfaces.media.DiscoverService
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.vlc.viewmodels.SortableModel

class ServiceContentProvider(val service : DiscoverService, context: Context, model: SortableModel) : MedialibraryProvider<Subscription>(context, model){

    override fun getTotalCount() = if (model.filterQuery == null)
        service.nbSubscriptions
    else
        service.searchSubscriptionsCount(model.filterQuery, sort, desc, true)

    override fun getPage(loadSize: Int, startposition: Int): Array<Subscription> {
        val list = if (model.filterQuery == null)
            service.getSubscriptions(sort, desc, true, false)
        else
            service.searchSubscriptions(model.filterQuery, sort, desc, true, false, loadSize, startposition)
        return list
    }

    override fun getAll(): Array<Subscription> = service.getSubscriptions(sort, desc, true, false)
}
