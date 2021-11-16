package com.drunkenboys.calendarun.ui.managecalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.databinding.FragmentManageCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.HorizontalInsetDividerDecoration
import com.drunkenboys.calendarun.util.extensions.stateCollect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageCalendarFragment : BaseFragment<FragmentManageCalendarBinding>(R.layout.fragment_manage_calendar) {

    private val manageCalendarAdapter = ManageCalendarAdapter(::onCalendarClickListener)

    private val manageCalendarViewModel by viewModels<ManageCalendarViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageCalendarViewModel.fetchCalendarList()
        setupToolbar()
        setupAdapter()
        setupFabClickListener()
        collectCalendarList()
    }

    private fun onCalendarClickListener(calendar: Calendar) {
        val action = ManageCalendarFragmentDirections.toEditCalendar(calendar.id)
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
            val action = ManageCalendarFragmentDirections.toSaveCalendar(0L)
            navController.navigate(action)
        }
    }

    private fun collectCalendarList() {
        stateCollect(manageCalendarViewModel.calendarList) { calendarList ->
            manageCalendarAdapter.submitList(calendarList)
        }
    }
}
