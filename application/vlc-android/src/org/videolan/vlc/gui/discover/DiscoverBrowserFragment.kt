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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.videolan.vlc.R
import org.videolan.vlc.gui.BaseFragment

class DiscoverBrowserFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {
    override fun getTitle() = getString(R.string.discover)
    private lateinit var pagerAdapter: DiscoverAdapter
    private lateinit var layoutOnPageChangeListener: TabLayout.TabLayoutOnPageChangeListener
    override val hasTabs = true
    private var tabLayout: TabLayout? = null
    private lateinit var viewPager: ViewPager

    private val tcl = TabLayout.TabLayoutOnPageChangeListener(tabLayout)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discover_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = requireActivity().findViewById(R.id.sliding_tabs)
        viewPager = view.findViewById(R.id.pager)
        pagerAdapter = DiscoverAdapter(childFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.setOnTouchListener(swipeFilter)
    }

    override fun onStart() {
        setupTabLayout()
        super.onStart()
    }

    override fun onStop() {
        unSetTabLayout()
        super.onStop()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) { }

    private fun setupTabLayout() {
        if (tabLayout == null || !::viewPager.isInitialized) return
        tabLayout?.setupWithViewPager(viewPager)
        if (!::layoutOnPageChangeListener.isInitialized) layoutOnPageChangeListener = TabLayout.TabLayoutOnPageChangeListener(tabLayout)
        viewPager.addOnPageChangeListener(layoutOnPageChangeListener)
        tabLayout?.addOnTabSelectedListener(this)
        viewPager.addOnPageChangeListener(this)
    }

    private fun unSetTabLayout() {
        if (tabLayout != null || !::viewPager.isInitialized) return
        viewPager.removeOnPageChangeListener(layoutOnPageChangeListener)
        tabLayout?.removeOnTabSelectedListener(this)
        viewPager.removeOnPageChangeListener(this)
    }

    inner class DiscoverAdapter(val fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> DiscoverFeedFragment.newInstance()
                1 -> DiscoverServiceFragment.newInstance()
                else -> throw IllegalStateException("Invalid discover fragment index")
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when(position) {
                0 -> requireActivity().getString(R.string.discover_feed)
                else -> requireActivity().getString(R.string.discover_podcast)
            }
        }
    }

    override fun onRefresh() {}

    override fun onTabSelected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        tcl.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {
        tcl.onPageScrollStateChanged(state)
    }
}