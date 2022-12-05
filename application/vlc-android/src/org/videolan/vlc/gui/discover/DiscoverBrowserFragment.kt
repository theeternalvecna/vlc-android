/*
 * ************************************************************************
 *  DiscoverBrowserFragment.kt
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

import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.videolan.medialibrary.interfaces.media.DiscoverService
import org.videolan.medialibrary.media.DiscoverServiceImpl
import org.videolan.vlc.R
import org.videolan.vlc.gui.BaseFragment
import org.videolan.vlc.gui.dialogs.PodcastAddDialog
import org.videolan.vlc.util.findCurrentFragment
import org.videolan.vlc.util.findFragmentAt

class DiscoverBrowserFragment : BaseFragment(), TabLayout.OnTabSelectedListener {
    override fun getTitle() = getString(R.string.discover)
    private lateinit var pagerAdapter: DiscoverAdapter
    override val hasTabs = true
    private var tabLayout: TabLayout? = null
    private lateinit var viewPager: ViewPager2
    override fun hasFAB() = true


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discover_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = requireActivity().findViewById(R.id.sliding_tabs)
        viewPager = view.findViewById(R.id.pager)
        //placeholder service
        pagerAdapter = DiscoverAdapter(requireActivity(), DiscoverServiceImpl(DiscoverService.Type.PODCAST, 0, 0, 0))
        viewPager.adapter = pagerAdapter
    }

    override fun onStart() {
        setupTabLayout()
        super.onStart()
        fabPlay?.setImageResource(R.drawable.ic_fab_add)
        fabPlay?.contentDescription = getString(R.string.add)
    }

    override fun onStop() {
        unSetTabLayout()
        (viewPager.findCurrentFragment(parentFragmentManager) as? BaseFragment)?.stopActionMode()
        super.onStop()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) {}

    private fun setupTabLayout() {
        if (tabLayout == null || !::viewPager.isInitialized) return
        tabLayout?.let {
            TabLayoutMediator(it, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.discover_feed)
                    else -> getString(R.string.discover_podcast)
                }
            }.attach()
        }
        tabLayout?.addOnTabSelectedListener(this)
    }

    private fun unSetTabLayout() {
        if (tabLayout != null || !::viewPager.isInitialized) return
        tabLayout?.removeOnTabSelectedListener(this)
    }

    inner class DiscoverAdapter(fa: FragmentActivity, val service: DiscoverService) : FragmentStateAdapter(fa) {

        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DiscoverFeedFragment.newInstance()
                1 -> DiscoverServiceFragment.newInstance(service)
                else -> throw IllegalStateException("Invalid discover fragment index")
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.position?.let {
            (viewPager.findFragmentAt(parentFragmentManager, it) as? BaseFragment)?.stopActionMode()
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onFabPlayClick(view: View) {
        PodcastAddDialog.newInstance().show(requireActivity().supportFragmentManager, "PodcastAddDialog")
    }
}