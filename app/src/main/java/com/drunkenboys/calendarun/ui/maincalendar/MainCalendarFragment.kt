package com.drunkenboys.calendarun.ui.maincalendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.idstore.IdStore
import com.drunkenboys.calendarun.databinding.FragmentMainCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.util.extensions.stateCollect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainCalendarFragment : BaseFragment<FragmentMainCalendarBinding>(R.layout.fragment_main_calendar) {

    private val mainCalendarViewModel by viewModels<MainCalendarViewModel>()
    private var isMonthCalendar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainCalendarViewModel.fetchCalendarList()
        setupCalendarView()
        setupDataBinding()
        setupToolbar()
        setupFab()
        setupMonthCalendar()
        collectCalendarList()
        collectCheckPointList()
        collectScheduleList()
    }

    private fun setupCalendarView() {
        // TODO: CalendarView 내부로 전환
        binding.calendarMonth.isVisible = isMonthCalendar
        binding.calendarYear.isVisible = !isMonthCalendar
    }

    private fun setupDataBinding() {
        binding.mainCalendarViewModel = mainCalendarViewModel
    }

    private fun setupToolbar() {
        binding.toolbarMainCalendar.setupWithNavController(navController, binding.layoutDrawer)
        setupOnMenuItemClickListener()
        setupNavigationOnClickListener()
    }

    private fun setupNavigationOnClickListener() = with(binding) {
        toolbarMainCalendar.setNavigationOnClickListener {
            val menuItemOrder = this@MainCalendarFragment.mainCalendarViewModel.menuItemOrder.value
            layoutDrawer.openDrawer(GravityCompat.START)
            if (binding.navView.menu.isEmpty()) return@setNavigationOnClickListener
            binding.navView.menu[menuItemOrder].isChecked = true
            binding.root.findViewById<TextView>(R.id.tv_drawerHeader_title).text = binding.navView.menu[menuItemOrder].title
        }
    }

    private fun setupOnMenuItemClickListener() {
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
        binding.fabMainCalenderAddSchedule.setOnClickListener {
            IdStore.clearId(IdStore.KEY_SCHEDULE_ID)
            val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSaveScheduleFragment(BehaviorType.INSERT)
            navController.navigate(action)
        }
    }

    private fun setupMonthCalendar() {
        binding.calendarMonth.setOnDaySecondClickListener { date, _ ->
            val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToDayScheduleDialog(date.toString())
            navController.navigate(action)
        }
    }

    private fun collectCalendarList() {
        stateCollect(mainCalendarViewModel.calendarList) { calendarList ->
            setupNavigationView(calendarList)

            val menuItemOrder = mainCalendarViewModel.menuItemOrder.value
            if (calendarList.isEmpty()) return@stateCollect
            val calendar = calendarList[menuItemOrder]
            mainCalendarViewModel.setCalendar(calendar)
        }
    }

    private fun setupNavigationView(calendarList: List<Calendar>) {
        val menu = binding.navView.menu
        menu.clear()

        calendarList.forEachIndexed { index, calendar ->
            menu.add(calendar.name)
                .setIcon(R.drawable.ic_favorite_24)
                .setCheckable(true)
                .setOnMenuItemClickListener {
                    mainCalendarViewModel.setMenuItemOrder(index)
                    // TODO: 2021-11-11 item에 맞는 캘린더 뷰로 변경
                    mainCalendarViewModel.setCalendar(calendar)
                    binding.layoutDrawer.closeDrawer(GravityCompat.START)
                    true
                }
        }

        menu.add(getString(R.string.drawer_calendar_add))
            .setIcon(R.drawable.ic_add)
            .setOnMenuItemClickListener {
                navigateToSaveCalendar()
                true
            }

        val manageMenu = menu.addSubMenu(getString(R.string.drawer_calendar_manage))
        manageMenu.add(getString(R.string.drawer_calendar_manage))
            .setIcon(R.drawable.ic_calendar_today)
            .setOnMenuItemClickListener {
                // TODO: 2021-11-11 달력 관리 화면으로 이동
                true
            }

        manageMenu.add(getString(R.string.drawer_theme_setting))
            .setIcon(R.drawable.ic_palette)
            .setOnMenuItemClickListener {
                // TODO: 2021-11-11 테마 설정 화면으로 이동동
                true
            }
    }

    private fun navigateToSaveCalendar() {
        val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSaveCalendarFragment()
        navController.navigate(action)
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
    }

    private fun collectCheckPointList() {
        stateCollect(mainCalendarViewModel.checkPointList) { checkPointList ->
            // TODO: 2021-11-10 CheckPoint 날짜 읽어서 뷰 나누기
        }
    }

    private fun collectScheduleList() {
        stateCollect(mainCalendarViewModel.scheduleList) { scheduleList ->
            binding.calendarMonth.setSchedules(scheduleList)
            binding.calendarYear.setSchedules(scheduleList)
        }
    }
}
