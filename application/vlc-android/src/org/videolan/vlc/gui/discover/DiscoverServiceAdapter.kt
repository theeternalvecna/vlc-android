/*
 * ************************************************************************
 *  DiscoverServiceAdapter.kt
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

package org.videolan.vlc.gui.discover

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import org.videolan.libvlc.util.AndroidUtil
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.*
import org.videolan.tools.MultiSelectAdapter
import org.videolan.tools.MultiSelectHelper
import org.videolan.vlc.BR
import org.videolan.vlc.R
import org.videolan.vlc.gui.helpers.*
import org.videolan.vlc.gui.video.*
import org.videolan.vlc.gui.view.FastScroller

class DiscoverServiceAdapter : PagedListAdapter<MediaLibraryItem, DiscoverServiceAdapter.ViewHolder>(DiscoverServiceCallback), FastScroller.SeparatedAdapter,
        MultiSelectAdapter<MediaLibraryItem>, IEventsSource<DiscoverFragment.DiscoverAction> by EventsSource() {

    var isListMode = true
    val multiSelectHelper = MultiSelectHelper(this, UPDATE_SELECTION)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.binding.setVariable(BR.scaleType, ImageView.ScaleType.CENTER_CROP)
        fillView(holder, item)
        holder.binding.setVariable(BR.media, item)
        holder.selectView(multiSelectHelper.isSelected(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position)
        else {
            val item = getItem(position) as Subscription
            for (data in payloads) {
                when (data as Int) {
                    UPDATE_NB_MEDIA -> holder.binding.setVariable(BR.time, holder.itemView.context.resources.getQuantityString(R.plurals.media_quantity, item.nbMedia, item.nbMedia))
                    UPDATE_SELECTION -> holder.selectView(multiSelectHelper.isSelected(position))
                }
            }
        }
    }

    private fun isPositionValid(position: Int) =  position in 0 until itemCount

    override fun getItem(position: Int) = if (isPositionValid(position)) super.getItem(position) else null

    override fun getItemViewType(position: Int): Int {
        return if (isListMode) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, if (viewType == 0) R.layout.subscription_list_item else R.layout.subscription_grid_item, parent, false)
        return ViewHolder(binding)
    }

    override fun hasSections() = true


    private fun fillView(holder: ViewHolder, item: MediaLibraryItem) {
        when (item) {
            is Subscription -> {
                holder.title.text = item.title
                if (!isListMode) holder.binding.setVariable(BR.resolution, null)
                holder.binding.setVariable(BR.seen, 0L)
                holder.binding.setVariable(BR.max, 0)
                holder.binding.setVariable(BR.time, holder.itemView.context.resources.getQuantityString(R.plurals.media_quantity, item.nbMedia, item.nbMedia))
                holder.binding.setVariable(BR.isNetwork, false)
                holder.binding.setVariable(BR.isPresent, true)
            }
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    inner class ViewHolder(binding: ViewDataBinding) : SelectorViewHolder<ViewDataBinding>(binding) {
        val title : TextView = itemView.findViewById(R.id.ml_item_title)
        val more : ImageView = itemView.findViewById(R.id.item_more)

        init {
            binding.setVariable(BR.holder, this)
            binding.setVariable(BR.cover, UiTools.getDefaultVideoDrawable(itemView.context))
            if (AndroidUtil.isMarshMallowOrLater)
                itemView.setOnContextClickListener { v ->
                    onMoreClick(v)
                    true
                }
        }

        fun onImageClick(@Suppress("UNUSED_PARAMETER") v: View) {
            val position = layoutPosition
            if (isPositionValid(position)) getItem(position)?.let { eventsChannel.trySend(DiscoverFragment.DiscoverImageClick(layoutPosition, it)) }
        }

        fun onClick(@Suppress("UNUSED_PARAMETER") v: View) {
            val position = layoutPosition
            if (isPositionValid(position)) getItem(position)?.let { eventsChannel.trySend(DiscoverFragment.DiscoverClick(layoutPosition, it)) }
        }

        fun onMoreClick(@Suppress("UNUSED_PARAMETER") v: View) {
            val position = layoutPosition
            if (isPositionValid(position)) getItem(position)?.let { eventsChannel.trySend(DiscoverFragment.DiscoverCtxClick(layoutPosition, it)) }
        }

        fun onLongClick(@Suppress("UNUSED_PARAMETER") v: View): Boolean {
            val position = layoutPosition
            return isPositionValid(position) && getItem(position)?.let { eventsChannel.trySend(DiscoverFragment.DiscoverLongClick(layoutPosition, it)).isSuccess } == true
        }

        override fun selectView(selected: Boolean) {
            binding.setVariable(BR.selected, selected)
            more.visibility = if (multiSelectHelper.inActionMode) View.INVISIBLE else View.VISIBLE
        }

        override fun isSelected() = multiSelectHelper.isSelected(layoutPosition)
    }
}

private object DiscoverServiceCallback : DiffUtil.ItemCallback<MediaLibraryItem>() {
    override fun areItemsTheSame(oldItem: MediaLibraryItem, newItem: MediaLibraryItem) = oldItem === newItem || oldItem.itemType == newItem.itemType && oldItem.equals(newItem)

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: MediaLibraryItem, newItem: MediaLibraryItem): Boolean {
        return if (oldItem is Subscription && newItem is Subscription) {
            oldItem === newItem || (oldItem.title == newItem.title
                    && oldItem.nbMedia == newItem.nbMedia)
        }
        else false
    }

    override fun getChangePayload(oldItem: MediaLibraryItem, newItem: MediaLibraryItem) = when {
        (oldItem is Subscription && newItem is Subscription) && oldItem.nbMedia != newItem.nbMedia -> UPDATE_NB_MEDIA
        oldItem.artworkMrl != newItem.artworkMrl -> UPDATE_THUMB
        else -> UPDATE_SEEN
    }
}