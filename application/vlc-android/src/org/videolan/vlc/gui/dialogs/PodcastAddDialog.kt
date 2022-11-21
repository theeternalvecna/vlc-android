/*
 * ************************************************************************
 *  PodcastAddDialog.kt
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

package org.videolan.vlc.gui.dialogs

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.videolan.medialibrary.interfaces.media.MlService
import org.videolan.resources.util.getFromMl
import org.videolan.tools.isValidUrl
import org.videolan.tools.setVisible
import org.videolan.vlc.R
import org.videolan.vlc.databinding.DialogAddPodcastBinding
import org.videolan.vlc.gui.helpers.UiTools


/**
 * Dialog allowing to add podcast entries
 */
class PodcastAddDialog : VLCBottomSheetDialogFragment() {

    private lateinit var binding: DialogAddPodcastBinding

    companion object {

        fun newInstance(): PodcastAddDialog {
            return PodcastAddDialog()
        }
    }

    override fun getDefaultState(): Int {
        return STATE_EXPANDED
    }

    override fun needToManageOrientation(): Boolean {
        return false
    }

    override fun initialFocusedView(): View = binding.title

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAddPodcastBinding.inflate(layoutInflater, container, false)
        binding.titleString = getString(R.string.add_to, getString(R.string.podcasts))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addPodcastButton.setOnClickListener {
            binding.podcastUrlText.editText?.text?.toString().let {
                if (!it.isValidUrl()) {
                    binding.podcastUrlText.error = getString(R.string.invalid_url)
                    return@let
                }
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val added = requireActivity().getFromMl {
                            getService(MlService.Type.PODCAST)!!.addSubscription(it)
                        }
                        withContext(Dispatchers.Main) {
                            if (!added) {
                                binding.podcastUrlText.error = getString(R.string.podcast_added_error)
                            } else {
                                UiTools.snacker(requireActivity(), R.string.podcast_added)
                                dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            try {
                val clipBoardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val text = clipBoardManager?.primaryClip?.getItemAt(0)?.text?.toString()
                if (text.isValidUrl()) {
                    binding.clipboardIndicator.setVisible()
                    binding.podcastUrlText.editText?.setText(text)
                }
            } catch (e: Exception) {
            }
        }
    }
}





