package com.drunkenboys.calendarun.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSettingBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.extensions.launchAndRepeatWithViewLifecycle
import com.drunkenboys.calendarun.util.extensions.throttleFirst
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val settingViewModel by viewModels<SettingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBinding()

        launchAndRepeatWithViewLifecycle {
            launch { collectManageCalendarClickEvent() }
            launch { collectThemeClickEvent() }
            launch { collectInfoClickEvent() }
            launch { collectLicenseClickEvent() }
        }
    }

    private fun setupToolbar() {
        binding.toolbarSetting.setupWithNavController(navController)
    }

    private fun setupBinding() {
        binding.viewModel = settingViewModel
    }

    private suspend fun collectManageCalendarClickEvent() {
        settingViewModel.manageCalendarClickEvent
            .throttleFirst(DEFAULT_TOUCH_THROTTLE_PERIOD)
            .collectLatest {
                val action = SettingFragmentDirections.toManageCalendar()
                navController.navigate(action)
            }
    }

    private suspend fun collectThemeClickEvent() {
        settingViewModel.themeClickEvent
            .throttleFirst(DEFAULT_TOUCH_THROTTLE_PERIOD)
            .collectLatest {
                val action = SettingFragmentDirections.toThemeFragment()
                navController.navigate(action)
            }
    }

    private suspend fun collectInfoClickEvent() {
        settingViewModel.infoClickEvent
            .throttleFirst(DEFAULT_TOUCH_THROTTLE_PERIOD)
            .collectLatest {

            }
    }

    private suspend fun collectLicenseClickEvent() {
        settingViewModel.licenseClickEvent
            .throttleFirst(DEFAULT_TOUCH_THROTTLE_PERIOD)
            .collectLatest {
                startActivity(Intent(this@SettingFragment.context, OssLicensesMenuActivity::class.java))
            }
    }

    companion object {

        private const val DEFAULT_TOUCH_THROTTLE_PERIOD = 500L
    }
}
