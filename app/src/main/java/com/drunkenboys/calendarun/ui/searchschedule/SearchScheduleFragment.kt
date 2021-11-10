package com.drunkenboys.calendarun.ui.searchschedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSearchScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchScheduleFragment : BaseFragment<FragmentSearchScheduleBinding>(R.layout.fragment_search_schedule) {

    private val searchScheduleViewModel: SearchScheduleViewModel by viewModels()

    private val searchScheduleAdapter = SearchScheduleAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = searchScheduleViewModel

        initToolbar()
        initRvSearchSchedule()
        observeListItem()
        observeScheduleClickEvent()

        searchScheduleViewModel.fetchScheduleList()
    }

    private fun initToolbar() {
        binding.toolbarSearchSchedule.setupWithNavController(navController)
    }

    private fun initRvSearchSchedule() {
        binding.rvSearchSchedule.adapter = searchScheduleAdapter
        val itemDecoration = HorizontalInsetDividerDecoration(
            context = requireContext(),
            orientation = RecyclerView.VERTICAL,
            leftInset = 16f,
            rightInset = 16f,
            ignoreLast = true
        )
        binding.rvSearchSchedule.addItemDecoration(itemDecoration)
    }

    private fun observeListItem() {
        searchScheduleViewModel.listItem.observe(viewLifecycleOwner) { listItem ->
            searchScheduleAdapter.submitList(listItem)
        }
    }

    private fun observeScheduleClickEvent() {
        searchScheduleViewModel.scheduleClickEvent.observe(viewLifecycleOwner) {
            val action = SearchScheduleFragmentDirections.actionSearchScheduleFragmentToSaveScheduleFragment(BehaviorType.UPDATE)
            navController.navigate(action)
        }
    }
}
