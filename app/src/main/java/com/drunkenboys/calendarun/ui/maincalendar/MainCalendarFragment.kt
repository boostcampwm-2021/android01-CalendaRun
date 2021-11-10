package com.drunkenboys.calendarun.ui.maincalendar

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
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
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

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
        setNavigationOnClickListener()
        setOnMenuItemClickListener()
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

    // TODO: 임시 데이터
    @RequiresApi(Build.VERSION_CODES.O)
    fun createFakeSchedule(): List<CalendarScheduleObject> {
        val today = LocalDate.now()

        return listOf(
            CalendarScheduleObject(
                id = 0,
                color = ScheduleColorType.YELLOW.color,
                text = "옛스케줄옛옛",
                startDate = today.minusDays(20),
                endDate = today.minusDays(5)
            ),
            CalendarScheduleObject(
                id = 1,
                color = ScheduleColorType.YELLOW.color,
                text = "뒷스케줄뒷뒷",
                startDate = today.plusDays(2),
                endDate = today.plusDays(7)
            ),
            CalendarScheduleObject(
                id = 2,
                color = ScheduleColorType.BLUE.color,
                text = "앞스케줄앞앞",
                startDate = today.minusDays(7),
                endDate = today.minusDays(2)
            ),
            CalendarScheduleObject(
                id = 3,
                color = ScheduleColorType.MAGENTA.color,
                text = "깍두기깍두기",
                startDate = today.minusDays(5),
                endDate = today.plusDays(5)
            ),
            CalendarScheduleObject(
                id = 4,
                color = ScheduleColorType.CYAN.color,
                text = "긴스케줄긴긴",
                startDate = today.minusDays(9),
                endDate = today.plusDays(9)
            ),
        )
    }

    private fun setDataBinding() {
        binding.mainCalendarViewModel = mainCalendarViewModel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.calendarMonth.setSchedules(createFakeSchedule())
        }
    }

    private fun setupToolbar() {
        binding.toolbarMainCalendar.setupWithNavController(navController, binding.layoutDrawer)
    }

    private fun setNavigationOnClickListener() = with(binding) {
        toolbarMainCalendar.setNavigationOnClickListener {
            layoutDrawer.openDrawer(GravityCompat.START)
            root.findViewById<TextView>(R.id.tv_drawerHeader_title).text =
                this@MainCalendarFragment.mainCalendarViewModel.calendar.value?.name
        }
    }

    private fun setOnMenuItemClickListener() {
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
            binding.calendarYear.setSchedules(scheduleList)
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
                    mainCalendarViewModel.setCalendar(calendarList[item.order])
                    binding.layoutDrawer.closeDrawer(GravityCompat.START)
                    // TODO: item에 맞는 캘린더 뷰로 변경
                }
            }
            true
        }
    }

    private fun navigateToAddSchedule() {
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
