package com.drunkenboys.calendarun.ui.theme

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentThemeBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.extensions.stateCollect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeFragment : BaseFragment<FragmentThemeBinding>(R.layout.fragment_theme) {

    private val themeViewModel by viewModels<ThemeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = themeViewModel

        collectTextAlign()
    }

    private fun collectTextAlign() {
        stateCollect(themeViewModel.textAlign) { align ->
            when (align) {
                Gravity.START -> binding.tvThemeTextAlignContent.setText(R.string.start)
                Gravity.CENTER_VERTICAL -> binding.tvThemeTextAlignContent.setText(R.string.center)
                Gravity.END -> binding.tvThemeTextAlignContent.setText(R.string.end)
            }
        }
    }
}
