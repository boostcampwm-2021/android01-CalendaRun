package com.drunkenboys.calendarun.ui.maincalendar

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentMainCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCalendarFragment : BaseFragment<FragmentMainCalendarBinding>(R.layout.fragment_main_calendar) {

    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarMainCalendar.setupWithNavController(navController)
        binding.toolbarMainCalendar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_main_calendar_search -> navigateToSearchSchedule()
                R.id.menu_main_calendar_calendar -> {
                    // TODO: CalendarView 내부로 전환
                    binding.calendarMonth.isVisible = binding.calendarMonth.isVisible.not()
                    binding.calendarYear.isVisible = binding.calendarYear.isVisible.not()
                }
                else -> return@setOnMenuItemClickListener false
            }
            true
        }

        // TODO: 2021-11-04 뷰모델 추가 시 이벤트 방식으로 변경 및 argument 설정
        binding.fabMainCalenderAddSchedule.setOnClickListener {
            val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSaveScheduleFragment()
            navController.navigate(action)
        }
    }

    private fun navigateToSearchSchedule() {
        val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSearchScheduleFragment()
        navController.navigate(action)
    }
}
