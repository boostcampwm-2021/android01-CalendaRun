package com.drunkenboys.calendarun.ui.maincalendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.forEachIndexed
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
        setupDataBinding()
        setupToolbar()
        setupFab()
        setupNavigationOnClickListener()
        setupOnMenuItemClickListener()
        setupCalendarListObserver()
        setupCheckPointListObserver()
        setupScheduleListObserver()
        setupOnDaySecondClickListener()
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

    private fun setupNavigationOnClickListener() = with(binding) {
        toolbarMainCalendar.setNavigationOnClickListener {
            val menuItemOrder = this@MainCalendarFragment.mainCalendarViewModel.menuItemOrder.value ?: DEFAULT_ORDER
            layoutDrawer.openDrawer(GravityCompat.START)
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

    private fun setupCalendarListObserver() {
        mainCalendarViewModel.calendarList.observe(viewLifecycleOwner) { calendarList ->
            setupNavigationView(calendarList)
            val calendar = mainCalendarViewModel.calendarList.value?.get(0) ?: return@observe
            mainCalendarViewModel.setCalendar(calendar)
        }
    }

    private fun setupNavigationView(calendarList: List<Calendar>) {
        val menu = binding.navView.menu
        menu.clear()

        calendarList.forEach { calendar ->
            menu.add(calendar.name)
                .setIcon(R.drawable.ic_favorite_24).isCheckable = true
        }
        menu.add(getString(R.string.drawer_calendar_add))
            .setIcon(R.drawable.ic_add)

        binding.navView.setNavigationItemSelectedListener { item ->
            menu.forEachIndexed { index, menuItem ->
                if (menuItem == item) {
                    if (item == menu[calendarList.size]) {
                        navigateToSaveSchedule()
                        return@forEachIndexed
                    }
                    mainCalendarViewModel.setMenuItemOrder(index)
                    // TODO: 2021-11-11 item에 맞는 캘린더 뷰로 변경
                    mainCalendarViewModel.setCalendar(calendarList[index])
                }
            }

            binding.layoutDrawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupCheckPointListObserver() {
        mainCalendarViewModel.checkPointList.observe(viewLifecycleOwner) { checkPointList ->
            // TODO: 2021-11-10 CheckPoint 날짜 읽어서 뷰 나누기
        }
    }

    private fun setupScheduleListObserver() {
        mainCalendarViewModel.scheduleList.observe(viewLifecycleOwner) { scheduleList ->
            binding.calendarMonth.setSchedules(scheduleList)
            binding.calendarYear.setSchedules(scheduleList)
        }
    }

    private fun navigateToSaveSchedule() {
        val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToSaveCalendarFragment()
        navController.navigate(action)
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
    }

    private fun setupOnDaySecondClickListener() {
        binding.calendarMonth.setOnDaySecondClickListener { date, _ ->
            val action = MainCalendarFragmentDirections.actionMainCalendarFragmentToDayScheduleDialog(date.toString())
            navController.navigate(action)
        }
    }

    companion object {
        private const val DEFAULT_ORDER = 0
    }
}
