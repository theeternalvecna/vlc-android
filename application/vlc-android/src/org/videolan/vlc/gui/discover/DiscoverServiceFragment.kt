package org.videolan.vlc.gui.discover

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.onEach
import org.videolan.medialibrary.interfaces.media.DiscoverService
import org.videolan.medialibrary.interfaces.media.Subscription
import org.videolan.medialibrary.media.MediaLibraryItem
import org.videolan.resources.*
import org.videolan.resources.util.parcelable
import org.videolan.tools.MultiSelectHelper
import org.videolan.tools.SUBSCRIPTION_CARD_MODE
import org.videolan.tools.Settings
import org.videolan.tools.dp
import org.videolan.vlc.R
import org.videolan.vlc.databinding.SubscriptionGridBinding
import org.videolan.vlc.gui.helpers.ItemOffsetDecoration
import org.videolan.vlc.gui.helpers.UiTools
import org.videolan.vlc.gui.video.*
import org.videolan.vlc.gui.view.EmptyLoadingState
import org.videolan.vlc.interfaces.Filterable
import org.videolan.vlc.util.Permissions
import org.videolan.vlc.util.launchWhenStarted
import org.videolan.vlc.viewmodels.subscription.ServiceContentViewModel
import org.videolan.vlc.viewmodels.subscription.getViewModel

private const val TAG = "VLC/DiscoverServiceFragment"

class DiscoverServiceFragment : DiscoverFragment<ServiceContentViewModel>(), Filterable {
    private lateinit var subscriptionListAdapter: DiscoverAdapter
    private lateinit var binding: SubscriptionGridBinding
    private lateinit var settings: SharedPreferences
    private var gridItemDecoration: RecyclerView.ItemDecoration? = null

    override fun hasFAB() = false
    override val hasTabs = true
    override val isMainNavigationPoint = false

    companion object {
        const val KEY_SERVICE = "key_service"
        fun newInstance(service: DiscoverService) = DiscoverServiceFragment().apply { arguments = bundleOf(KEY_SERVICE to service) }
    }

    override fun getRootView() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!::settings.isInitialized) settings = Settings.getInstance(requireContext())
        val discoverService = arguments?.parcelable(KEY_SERVICE) as? DiscoverService ?: throw IllegalStateException("No service provided")
        viewModel = getViewModel(discoverService)
        subscriptionListAdapter = DiscoverAdapter().apply { stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY }
        multiSelectHelper = subscriptionListAdapter.multiSelectHelper
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SubscriptionGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.videoGrid.adapter = subscriptionListAdapter
        swipeRefreshLayout.setOnRefreshListener(this)
        viewModel.provider.pagedList.observe(requireActivity()) {
            @Suppress("UNCHECKED_CAST")
            (it as? PagedList<MediaLibraryItem>)?.let { pagedList -> subscriptionListAdapter.submitList(pagedList) }
            updateEmptyView()
            restoreMultiSelectHelper()
        }
        viewModel.provider.loading.observe(requireActivity()) { loading ->
            if (isResumed) setRefreshing(loading) { refresh ->
                if (!refresh) {
                    menu?.let { UiTools.updateSortTitles(it, viewModel.provider) }
                    restoreMultiSelectHelper()
                }
            }
        }
        subscriptionListAdapter.events.onEach { it.process() }.launchWhenStarted(lifecycleScope)
    }

    override fun onStart() {
        super.onStart()
        updateViewMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        gridItemDecoration = null
    }


    override fun scrollToTop() {
        binding.videoGrid.smoothScrollToPosition(0)
    }

    private fun updateViewMode() {
        if (view == null || activity == null) {
            Log.w(TAG, "Unable to setup the view")
            return
        }
        val res = resources
        if (gridItemDecoration == null) gridItemDecoration = ItemOffsetDecoration(resources, R.dimen.left_right_1610_margin, R.dimen.top_bottom_1610_margin)
        val listMode = !settings.getBoolean(SUBSCRIPTION_CARD_MODE, true)

        // Select between grid or list
        binding.videoGrid.removeItemDecoration(gridItemDecoration!!)
        if (!listMode) {
            val thumbnailWidth = res.getDimensionPixelSize(R.dimen.grid_card_thumb_width)
            val margin = binding.videoGrid.paddingStart + binding.videoGrid.paddingEnd
            val columnWidth = binding.videoGrid.getPerfectColumnWidth(thumbnailWidth, margin) - res.getDimensionPixelSize(R.dimen.left_right_1610_margin) * 2
            binding.videoGrid.columnWidth = columnWidth
            binding.videoGrid.addItemDecoration(gridItemDecoration!!)
            binding.videoGrid.setPadding(4.dp, 4.dp, 4.dp, 4.dp)
        } else {
            binding.videoGrid.setPadding(0, 0, 0, 0)
        }
        binding.videoGrid.setNumColumns(if (listMode) 1 else -1)
        subscriptionListAdapter.isListMode = listMode
    }

    private fun updateEmptyView() {
        if (!::binding.isInitialized) return
        if (!isAdded) return
        val empty = viewModel.isEmpty() && subscriptionListAdapter.currentList.isNullOrEmpty()
        val working = viewModel.provider.loading.value != false
        binding.emptyLoading.emptyText = viewModel.filterQuery?.let {  getString(R.string.empty_search, it) } ?: getString(R.string.nomedia)
        binding.emptyLoading.state = when {
            !Permissions.canReadStorage(AppContextProvider.appContext) && empty -> EmptyLoadingState.MISSING_PERMISSION
            empty && working -> EmptyLoadingState.LOADING
            empty && !working && viewModel.filterQuery == null -> EmptyLoadingState.EMPTY
            empty && !working && viewModel.filterQuery != null -> EmptyLoadingState.EMPTY_SEARCH
            else -> EmptyLoadingState.NONE
        }
        binding.empty = empty && !working
    }



    override fun getTitle() = ""


    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        val count = multiSelectHelper.getSelectionCount()
        if (count == 0) {
            stopActionMode()
            return false
        }
        return super.onPrepareActionMode(mode, menu)
    }
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.action_mode_discover_service, menu)
        multiSelectHelper.toggleActionMode(true, subscriptionListAdapter.itemCount)
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
        val item = menuItem ?: return false
        val list = multiSelectHelper.getSelection().map { it as Subscription }

        when (item.itemId) {
            R.id.action_subscription_delete -> {
                removeItems(list)
            }
            else -> return false
        }
        stopActionMode()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        setFabPlayVisibility(true)
        multiSelectHelper.clearSelection()
        multiSelectHelper.toggleActionMode(false, subscriptionListAdapter.itemCount)
    }

    override fun getMultiHelper() = if (::subscriptionListAdapter.isInitialized) subscriptionListAdapter.multiSelectHelper as? MultiSelectHelper<ServiceContentViewModel> else null

    override fun onRefresh() {
        viewModel.refresh()
    }

    override fun onCtxAction(position: Int, option: Long) {
        if (position >= subscriptionListAdapter.itemCount) return
        val activity = activity ?: return
        when (val media = subscriptionListAdapter.getItem(position)) {
            is Subscription -> when (option) {
                CTX_DELETE -> removeItem(media)
            }
        }
    }
}

