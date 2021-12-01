package com.drunkenboys.calendarun.ui.theme

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentThemeBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.extensions.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ThemeFragment : BaseFragment<FragmentThemeBinding>(R.layout.fragment_theme) {

    private val themeViewModel by navGraphViewModels<ThemeViewModel>(R.id.themeFragment) { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = themeViewModel

        setupToolbar()

        launchAndRepeatWithViewLifecycle {
            launch { collectTextAlign() }
        }
    }

    private fun setupToolbar() {
        binding.toolbarTheme.setupWithNavController(navController)
        binding.toolbarTheme.setOnMenuItemClickListener { menu ->
            if (menu.itemId == R.id.menu_theme_reset) {
                navigateToThemeResetDialog()
                true
            } else {
                false
            }
        }
    }

    private fun navigateToThemeResetDialog() {
        val action = ThemeFragmentDirections.toThemeResetDialog()
        navController.navigate(action)
    }

    private suspend fun collectTextAlign() {
        themeViewModel.textAlign.collect { align ->
            when (align) {
                Gravity.START -> binding.tvThemeTextAlignContent.setText(R.string.start)
                Gravity.CENTER -> binding.tvThemeTextAlignContent.setText(R.string.center)
                Gravity.END -> binding.tvThemeTextAlignContent.setText(R.string.end)
            }
        }
    }
}
