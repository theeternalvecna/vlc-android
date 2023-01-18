/*
 * ************************************************************************
 *  DiscoverFeedProvider.kt
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
import org.videolan.vlc.media.PlaylistManager
import org.videolan.vlc.media.SubscriptionEpisode
import org.videolan.vlc.viewmodels.SortableModel

class DiscoverFeedProvider(context: Context, model: SortableModel) : MedialibraryProvider<SubscriptionEpisode>(context, model){

    override fun canSortByFileNameName() = true


    override fun getTotalCount() = if (model.filterQuery == null)
        medialibrary.getSubscriptionMediaCount(true)
    else
        medialibrary.getSearchSubscriptionMediaCount(model.filterQuery)

    override fun getPage(loadSize: Int, startposition: Int): Array<SubscriptionEpisode> {
        val list =  if (model.filterQuery == null)
            medialibrary.getSubscriptionMedia(model.sort, model.desc, true, false, loadSize, startposition)
        else
            medialibrary.searchSubscriptionMedia(model.filterQuery, model.sort, model.desc, true, false, loadSize, startposition)
        return list.map {
            SubscriptionEpisode(it,  it.subscriptions.toList(), PlaylistManager.hasMedia(it.uri))
        }.toTypedArray()
    }

    override fun getAll(): Array<SubscriptionEpisode> = medialibrary.getSubscriptionMedia(model.sort, model.desc, true, false, 0, 0).map {
        SubscriptionEpisode(it,  it.subscriptions.toList(), PlaylistManager.hasMedia(it.uri))
    }.toTypedArray()

}
