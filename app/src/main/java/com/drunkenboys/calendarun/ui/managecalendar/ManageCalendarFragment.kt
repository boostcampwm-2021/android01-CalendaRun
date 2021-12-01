package com.drunkenboys.calendarun.ui.managecalendar

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentManageCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.maincalendar.MainCalendarViewModel
import com.drunkenboys.calendarun.ui.managecalendar.model.CalendarItem
import com.drunkenboys.calendarun.util.HorizontalInsetDividerDecoration
import com.drunkenboys.calendarun.util.extensions.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageCalendarFragment : BaseFragment<FragmentManageCalendarBinding>(R.layout.fragment_manage_calendar) {

    private val manageCalendarAdapter = ManageCalendarAdapter(::onCalendarItemClickListener)

    private val manageCalendarViewModel by navGraphViewModels<ManageCalendarViewModel>(R.id.manageCalendarFragment) { defaultViewModelProviderFactory }
    private val mainCalendarViewModel by navGraphViewModels<MainCalendarViewModel>(R.id.mainCalendarFragment) { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()
        setupFabClickListener()
        setupToolbarMenuItemOnClickListener()

        launchAndRepeatWithViewLifecycle {
            launch { collectCalendarItemList() }
            launch { collectDeleteCalendar() }
        }
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

    private suspend fun collectCalendarItemList() {
        manageCalendarViewModel.calendarItemList.collect { calendarItemList ->
            manageCalendarAdapter.submitList(calendarItemList)
            binding.rvManageCalendar.isVisible = calendarItemList.isNotEmpty()
            binding.tvEmpty.isVisible = calendarItemList.isEmpty()
        }
    }

    private suspend fun collectDeleteCalendar() {
        manageCalendarViewModel.deleteCalendarEvent.collect {
            val currentCalendarItemList = manageCalendarAdapter.currentList
            manageCalendarViewModel.deleteCalendarItem(currentCalendarItemList)

            // TODO: 2021-11-25 로직의 위치를 옮기고 싶네요
            val currentCalendarId = mainCalendarViewModel.calendarId.value
            val checkedList = currentCalendarItemList.filter { it.check }
            if (checkedList.any { it.id == currentCalendarId }) {
                mainCalendarViewModel.setCalendarId(1)
            }
        }
    }
}
