package com.drunkenboys.calendarun.ui.maincalendar

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.idstore.IdStore
import com.drunkenboys.calendarun.databinding.FragmentMainCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCalendarFragment : BaseFragment<FragmentMainCalendarBinding>(R.layout.fragment_main_calendar) {

    private val navController by lazy { findNavController() }
    private val mainCalendarViewModel by viewModels<MainCalendarViewModel>()
    private var isMonthCalendar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainCalendarViewModel.fetchCalendarList()
        setupCalendarView()
        setDataBinding()
        setupToolbar()
        setupFab()
        setCalendarObserver()
        setCalendarListObserver()
        setCheckPointListObserver()
        setScheduleListObserver()
        setOnDaySecondClickListener()
    }

    private fun setupCalendarView() {
        // TODO: CalendarView 내부로 전환
        binding.calendarMonth.isVisible = isMonthCalendar
        binding.calendarYear.isVisible = !isMonthCalendar
    }

    private fun setDataBinding() {
        binding.mainCalendarViewModel = mainCalendarViewModel
    }

    private fun setupToolbar() {
        binding.toolbarMainCalendar.setupWithNavController(navController, binding.layoutDrawer)
        binding.toolbarMainCalendar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_main_calendar_calendar -> {
                    isMonthCalendar = !isMonthCalendar
                    setupCalendarView()
                }
                R.id.menu_main_calendar_search -> navigateToSearchSchedule()
                else -> return@setOnMenuItemClickListener false
            }
            true
        }
    }

    private fun navigateToSearchSchedule() {
        val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSearchScheduleFragment()
        navController.navigate(action)
    }

    private fun setupFab() {
        // TODO: 2021-11-04 뷰모델 추가 시 이벤트 방식으로 변경
        binding.fabMainCalenderAddSchedule.setOnClickListener {
            // TODO: 2021-11-07 ID를 초기화하는 코드를 뷰모델로 이동해야 함
            IdStore.clearId(IdStore.KEY_SCHEDULE_ID)
            val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSaveScheduleFragment(BehaviorType.INSERT)
            navController.navigate(action)
        }
    }

    private fun setCalendarObserver() {
        mainCalendarViewModel.calendar.observe(viewLifecycleOwner) { calendar ->
            binding.toolbarMainCalendar.title = calendar.name
        }
    }

    private fun setCalendarListObserver() {
        mainCalendarViewModel.calendarList.observe(viewLifecycleOwner) { calendarList ->
            setupNavigationView(calendarList)
        }
    }

    private fun setCheckPointListObserver() {
        mainCalendarViewModel.checkPointList.observe(viewLifecycleOwner) { checkPointList ->
            // TODO: 2021-11-10 CheckPoint 날짜 읽어서 뷰 나누기
        }
    }

    private fun setScheduleListObserver() {
        mainCalendarViewModel.scheduleList.observe(viewLifecycleOwner) { scheduleList ->
            binding.calendarMonth.setSchedules(scheduleList)
        }
    }

    private fun setupNavigationView(calendarList: List<Calendar>) {
        val menu = binding.navView.menu
        menu.clear()

        calendarList.forEach { calendar ->
            menu.add(calendar.name)
                .setIcon(R.drawable.ic_favorite_24)
        }
        menu.add(getString(R.string.drawer_calendar_add))
            .setIcon(R.drawable.ic_add)

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item) {
                menu[calendarList.size] -> navigateToSaveCalendar()
                else -> {
                    val selectedCalendar = calendarList.find { calendar -> calendar.name == item.title } ?: throw IllegalStateException()
                    mainCalendarViewModel.setCalendar(selectedCalendar)
                    item.isChecked = true
                    binding.layoutDrawer.closeDrawer(GravityCompat.START)
                    // TODO: item에 맞는 캘린더 뷰로 변경
                }
            }
            true
        }
    }

    private fun navigateToSaveCalendar() {
        val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSaveCalendarFragment()
        navController.navigate(action)
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
    }

    private fun setOnDaySecondClickListener() {
        binding.calendarMonth.setOnDaySecondClickListener { date, _ ->
            val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToDayScheduleDialog(date.toString())
            navController.navigate(action)
        }
    }
}
