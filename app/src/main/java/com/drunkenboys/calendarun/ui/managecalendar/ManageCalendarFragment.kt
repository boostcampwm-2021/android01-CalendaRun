package com.drunkenboys.calendarun.ui.managecalendar

import android.os.Bundle
import android.view.View
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentManageCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.managecalendar.model.CalendarItem
import com.drunkenboys.calendarun.util.HorizontalInsetDividerDecoration
import com.drunkenboys.calendarun.util.extensions.sharedCollect
import com.drunkenboys.calendarun.util.extensions.stateCollect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageCalendarFragment : BaseFragment<FragmentManageCalendarBinding>(R.layout.fragment_manage_calendar) {

    private val manageCalendarAdapter = ManageCalendarAdapter(::onCalendarItemClickListener)

    private val manageCalendarViewModel by navGraphViewModels<ManageCalendarViewModel>(R.id.manageCalendarFragment) { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageCalendarViewModel.fetchCalendarList()
        setupToolbar()
        setupAdapter()
        setupFabClickListener()
        setupToolbarMenuItemOnClickListener()
        collectCalendarItemList()
        collectDeleteCalendar()
    }

    private fun onCalendarItemClickListener(calendarItem: CalendarItem) {
        val action = ManageCalendarFragmentDirections.toEditCalendar(calendarItem.id)
        navController.navigate(action)
    }

    private fun setupToolbar() {
        binding.toolbarManageCalendar.setupWithNavController(navController)
        // TODO: 2021-11-15 메뉴 아이템 클릭 리스너 추가
    }

    private fun setupAdapter() {
        binding.rvManageCalendar.adapter = manageCalendarAdapter
        val itemDecoration = HorizontalInsetDividerDecoration(
            context = requireContext(),
            orientation = RecyclerView.VERTICAL,
            leftInset = 16f,
            rightInset = 16f,
            ignoreLast = true
        )
        binding.rvManageCalendar.addItemDecoration(itemDecoration)
    }

    private fun setupFabClickListener() {
        binding.fabManagerCalenderAddCalendar.setOnClickListener {
            val action = ManageCalendarFragmentDirections.toSaveCalendar()
            navController.navigate(action)
        }
    }

    private fun setupToolbarMenuItemOnClickListener() {
        binding.toolbarManageCalendar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_delete_schedule) {
                navController.navigate(ManageCalendarFragmentDirections.toDeleteCalendarDialog())
                true
            } else {
                false
            }
        }
    }

    private fun collectCalendarItemList() {
        stateCollect(manageCalendarViewModel.calendarItemList) { calendarItemList ->
            manageCalendarAdapter.submitList(calendarItemList)
        }
    }

    private fun collectDeleteCalendar() {
        sharedCollect(manageCalendarViewModel.deleteCalendarEvent) {
            val currentCalendarItemList = manageCalendarAdapter.currentList
            manageCalendarViewModel.deleteCalendarItem(currentCalendarItemList)
        }
    }
}
