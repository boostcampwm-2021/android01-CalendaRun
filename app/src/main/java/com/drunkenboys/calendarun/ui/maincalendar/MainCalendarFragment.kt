package com.drunkenboys.calendarun.ui.maincalendar

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.databinding.DrawerHeaderBinding
import com.drunkenboys.calendarun.databinding.FragmentMainCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.extensions.navigateSafe
import com.drunkenboys.calendarun.util.extensions.sharedCollect
import com.drunkenboys.calendarun.util.extensions.stateCollect
import com.drunkenboys.calendarun.util.extensions.throttleFirst
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCalendarFragment : BaseFragment<FragmentMainCalendarBinding>(R.layout.fragment_main_calendar) {

    private val mainCalendarViewModel by viewModels<MainCalendarViewModel>()
    private var isMonthCalendar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendarView()
        setupDataBinding()
        collectScheduleList()
        collectCalendarSet()
        collectCalendarList()
        setupToolbar()
        setupMonthCalendar()
        collectFabClickEvent()
        collectDaySecondClickListener()
        mainCalendarViewModel.fetchCalendarList()
        collectCalendarDesignObject()
    }

    private fun setupCalendarView() {
        // TODO: CalendarView 내부로 전환
        binding.calendarMonth.isVisible = isMonthCalendar
        binding.calendarYear.isVisible = !isMonthCalendar
    }

    private fun setupDataBinding() {
        binding.mainCalendarViewModel = mainCalendarViewModel
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
                    mainCalendarViewModel.setCalendar(calendar)
                    binding.layoutDrawer.closeDrawer(GravityCompat.START)
                    true
                }
        }

        menu.add(getString(R.string.calendar_add))
            .setIcon(R.drawable.ic_add)
            .setOnMenuItemClickListener {
                navigateToSaveCalendar()
                true
            }

        val manageMenu = menu.addSubMenu(getString(R.string.drawer_calendar_manage))
        manageMenu.add(getString(R.string.drawer_calendar_manage))
            .setIcon(R.drawable.ic_calendar_today)
            .setOnMenuItemClickListener {
                navigateToManageCalendar()
                true
            }

        manageMenu.add(getString(R.string.drawer_theme_setting))
            .setIcon(R.drawable.ic_palette)
            .setOnMenuItemClickListener {
                navigateToThemeFragment()
                true
            }
    }

    private fun navigateToSaveCalendar() {
        val action = MainCalendarFragmentDirections.toSaveCalendar()
        navController.navigate(action)
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
    }

    private fun navigateToManageCalendar() {
        val action = MainCalendarFragmentDirections.toManageCalendar()
        navController.navigate(action)
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
    }

    private fun navigateToThemeFragment() {
        val action = MainCalendarFragmentDirections.toThemeFragment()
        navController.navigate(action)
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
    }

    private fun collectScheduleList() {
        stateCollect(mainCalendarViewModel.scheduleList) { scheduleList ->
            binding.calendarMonth.setSchedules(scheduleList)
            binding.calendarYear.setSchedules(scheduleList)
        }
    }

    private fun collectCalendarSet() {
        stateCollect(mainCalendarViewModel.calendarSetList) { calendarSetList ->
            if (calendarSetList.isEmpty()) {
                binding.calendarMonth.setupDefaultCalendarSet()
            } else {
                binding.calendarMonth.setCalendarSetList(calendarSetList)
            }
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

    private fun setupToolbar() {
        binding.toolbarMainCalendar.setupWithNavController(navController, binding.layoutDrawer)
        setupOnMenuItemClickListener()
        setupAddDrawerListener()
    }

    private fun setupAddDrawerListener() = with(binding) {
        val drawerHeaderBinding = DrawerHeaderBinding.bind(navView.getHeaderView(FIRST_HEADER))
        layoutDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val menuItemOrder = this@MainCalendarFragment.mainCalendarViewModel.menuItemOrder.value
                if (binding.navView.menu.isEmpty()) return
                binding.navView.menu[menuItemOrder].isChecked = true
                drawerHeaderBinding.tvDrawerHeaderTitle.text = binding.navView.menu[menuItemOrder].title
            }

            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
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
        val action = MainCalendarFragmentDirections.toSearchSchedule()
        navController.navigate(action)
    }

    private fun setupMonthCalendar() {
        // TODO: 2021-11-16 이벤트를 발행하는 부분을 xml로 이동시킬 수 있으면 좋을듯
        binding.calendarMonth.setOnDaySecondClickListener { date, _ ->
            mainCalendarViewModel.emitDaySecondClickEvent(date)
        }
    }

    private fun collectFabClickEvent() {
        sharedCollect(mainCalendarViewModel.fabClickEvent) { calendarId ->
            val action = MainCalendarFragmentDirections.toSaveSchedule(calendarId, 0)
            navController.navigate(action)
        }
    }

    private fun collectDaySecondClickListener() {
        // TODO: 2021-11-16 현재 flow 확장함수가 StateFlow와 SharedFlow만 받기 때문에 라이프사이클 처리를 명시해주고 있는데 개선이 필요해보임.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainCalendarViewModel.daySecondClickEvent
                    .throttleFirst(DEFAULT_TOUCH_THROTTLE_PERIOD)
                    .collect { date ->
                        val calendarId = mainCalendarViewModel.calendar.value?.id ?: throw IllegalStateException("calendarId is null")
                        val action = MainCalendarFragmentDirections.toDayScheduleDialog(calendarId, date.toString())
                        navController.navigateSafe(action)
                    }
            }
        }
    }

    private fun collectCalendarDesignObject() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainCalendarViewModel.fetchCalendarDesignObject()
                    .collect {
                        binding.calendarMonth.setDesign(it)
                        binding.calendarYear.setTheme(it)
                    }
            }
        }
    }

    companion object {

        private const val FIRST_HEADER = 0
        private const val DEFAULT_TOUCH_THROTTLE_PERIOD = 500L
    }
}
