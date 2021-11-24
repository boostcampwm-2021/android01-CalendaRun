package com.drunkenboys.calendarun.ui.maincalendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.databinding.DrawerHeaderBinding
import com.drunkenboys.calendarun.databinding.FragmentMainCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.extensions.*
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCalendarFragment : BaseFragment<FragmentMainCalendarBinding>(R.layout.fragment_main_calendar) {

    private val mainCalendarViewModel by viewModels<MainCalendarViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
        setupToolbar()

        launchAndRepeatWithViewLifecycle {
            launch { collectScheduleList() }
            launch { collectCalendarSet() }
            launch { collectCalendarList() }
            launch { collectFabClickEvent() }
            launch { collectDaySecondClickListener() }
            launch { collectCalendarDesignObject() }
            launch { collectLicenseClickEvent() }
        }
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
//                    mainCalendarViewModel.setSelectedCalendarIndex(index)
//                    mainCalendarViewModel.setCalendar(calendar)
                    mainCalendarViewModel.setCalendarId(calendar.id)
                    binding.layoutDrawer.closeDrawer(GravityCompat.START)
                    LoadingDialog().show(childFragmentManager, this::class.simpleName)
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

        manageMenu.add(getString(R.string.drawer_license))
            .setIcon(R.drawable.ic_license)
            .setOnMenuItemClickListener {
                mainCalendarViewModel.emitLicenseClickEvent()
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

    private suspend fun collectScheduleList() {
        mainCalendarViewModel.scheduleList.collect { scheduleList ->
            binding.calendarMonth.setSchedules(scheduleList)
            binding.calendarYear.setSchedules(scheduleList)
        }
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

    private suspend fun collectCalendarList() {
        mainCalendarViewModel.calendarList.collect { calendarList ->
            setupNavigationView(calendarList)
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
                val index = this@MainCalendarFragment.mainCalendarViewModel.selectCalendarIndex.value
                if (binding.navView.menu.isEmpty()) return
                binding.navView.menu[index].isChecked = true
                drawerHeaderBinding.tvDrawerHeaderTitle.text = binding.navView.menu[index].title
                drawerHeaderBinding.tvDrawerHeaderSetting.setOnClickListener {
                    this@MainCalendarFragment.mainCalendarViewModel.emitLicenseClickEvent()
                }
            }

            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
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
                binding.layoutDrawer.closeDrawer(GravityCompat.START)
                startActivity(Intent(this@MainCalendarFragment.context, OssLicensesMenuActivity::class.java))
            }
    }

    companion object {

        private const val FIRST_HEADER = 0
        private const val DEFAULT_TOUCH_THROTTLE_PERIOD = 500L
    }
}
