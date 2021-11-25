package com.drunkenboys.calendarun.ui.maincalendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.KEY_CALENDAR_ID
import com.drunkenboys.calendarun.MainActivity
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.databinding.DrawerHeaderBinding
import com.drunkenboys.calendarun.databinding.FragmentMainCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.extensions.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainCalendarFragment : BaseFragment<FragmentMainCalendarBinding>(R.layout.fragment_main_calendar) {

    private val mainCalendarViewModel by viewModels<MainCalendarViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
        setupToolbar()

        launchAndRepeatWithViewLifecycle {
            launch { collectCalendarId() }
            launch { collectCalendarList() }
            launch { collectCalendarSet() }
            launch { collectScheduleList() }
            launch { collectFabClickEvent() }
            launch { collectDaySecondClickListener() }
            launch { collectCalendarDesignObject() }
            launch { collectLicenseClickEvent() }
        }
    }

    private fun setupDataBinding() {
        binding.mainCalendarViewModel = mainCalendarViewModel
        val drawerHeaderBinding = DrawerHeaderBinding.bind(binding.navView.getHeaderView(FIRST_HEADER))
        drawerHeaderBinding.viewModel = mainCalendarViewModel
        drawerHeaderBinding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupToolbar() {
        binding.toolbarMainCalendar.setupWithNavController(navController, binding.layoutDrawer)
        setupOnMenuItemClickListener()
    }

    private fun setupOnMenuItemClickListener() {
        binding.toolbarMainCalendar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_main_calendar_calendar -> mainCalendarViewModel.toggleCalendar()
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

    private suspend fun collectCalendarId() {
        mainCalendarViewModel.calendarId.collect { calendarId ->
            val pref = requireContext().getSharedPreferences(MainActivity.CALENDAR_ID_PREF, Context.MODE_PRIVATE)
            pref.edit()
                .putLong(KEY_CALENDAR_ID, calendarId)
                .apply()
        }
    }

    private suspend fun collectCalendarList() {
        mainCalendarViewModel.calendarList.collect { calendarList ->
            setupNavigationView(calendarList)
        }
    }

    private fun setupNavigationView(calendarList: List<Calendar>) {
        addCalendarMenu(calendarList)
        inflateDrawerMenu()
    }

    private fun addCalendarMenu(calendarList: List<Calendar>) {
        val menu = binding.navView.menu
        menu.clear()
        calendarList.forEach { calendar ->
            menu.add(0, calendar.id.toInt(), 0, calendar.name)
                .setIcon(R.drawable.ic_favorite_24)
                .setCheckable(true)
                .setOnMenuItemClickListener {
                    mainCalendarViewModel.setCalendarId(calendar.id)
                    binding.layoutDrawer.close()
                    LoadingDialog().show(childFragmentManager, this::class.simpleName)
                    true
                }
        }
        val currentCalendarId = mainCalendarViewModel.calendarId.value
        menu.findItem(currentCalendarId.toInt())?.isChecked = true
    }

    private fun inflateDrawerMenu() {
        binding.navView.inflateMenu(R.menu.menu_main_calendar_nav_drawer)
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_mainCalendar_addCalendar -> navigateToSaveCalendar()
                R.id.menu_mainCalendar_calendarManage -> navigateToManageCalendar()
                R.id.menu_mainCalendar_themeSetting -> navigateToThemeFragment()
                R.id.menu_mainCalendar_license -> mainCalendarViewModel.emitLicenseClickEvent()
            }
            binding.layoutDrawer.close()
            true
        }
    }

    private fun navigateToSaveCalendar() {
        val action = MainCalendarFragmentDirections.toSaveCalendar()
        navController.navigate(action)
    }

    private fun navigateToManageCalendar() {
        val action = MainCalendarFragmentDirections.toManageCalendar()
        navController.navigate(action)
    }

    private fun navigateToThemeFragment() {
        val action = MainCalendarFragmentDirections.toThemeFragment()
        navController.navigate(action)
    }

    private suspend fun collectCalendarSet() {
        mainCalendarViewModel.calendarSetList.collect { calendarSetList ->
            if (calendarSetList.isEmpty()) {
                binding.calendarMonth.setupDefaultCalendarSet()
                binding.calendarYear.setupDefaultCalendarSet()
            } else {
                binding.calendarMonth.setCalendarSetList(calendarSetList)
                binding.calendarYear.setCalendarSetList(calendarSetList)
            }
        }
    }

    private suspend fun collectScheduleList() {
        mainCalendarViewModel.scheduleList.collect { scheduleList ->
            binding.calendarMonth.setSchedules(scheduleList)
            binding.calendarYear.setSchedules(scheduleList)
        }
    }

    private suspend fun collectFabClickEvent() {
        mainCalendarViewModel.fabClickEvent.collect { calendarId ->
            val action = MainCalendarFragmentDirections.toSaveSchedule(calendarId, 0)
            navController.navigate(action)
        }
    }

    private suspend fun collectDaySecondClickListener() {
        mainCalendarViewModel.daySecondClickEvent
            .throttleFirst(DEFAULT_TOUCH_THROTTLE_PERIOD)
            .collect { date ->
                val action = MainCalendarFragmentDirections.toDayScheduleDialog(mainCalendarViewModel.calendarId.value, date.toString())
                navController.navigateSafe(action)
            }
    }

    private suspend fun collectCalendarDesignObject() {
        mainCalendarViewModel.calendarDesignObject.collect { designObj ->
            designObj ?: return@collect
            binding.calendarMonth.setDesign(designObj)
            binding.calendarYear.setTheme(designObj)
        }
    }

    private suspend fun collectLicenseClickEvent() {
        mainCalendarViewModel.licenseClickEvent
            .throttleFirst(DEFAULT_TOUCH_THROTTLE_PERIOD)
            .collect {
                startActivity(Intent(this@MainCalendarFragment.context, OssLicensesMenuActivity::class.java))
            }
    }

    companion object {

        private const val FIRST_HEADER = 0
        private const val DEFAULT_TOUCH_THROTTLE_PERIOD = 500L
    }
}
