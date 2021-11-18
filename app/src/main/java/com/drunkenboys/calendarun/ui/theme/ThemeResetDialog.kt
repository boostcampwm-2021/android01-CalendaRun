package com.drunkenboys.calendarun.ui.theme

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.drunkenboys.calendarun.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeResetDialog : DialogFragment() {

    private val themeViewModel: ThemeViewModel
            by navGraphViewModels(R.id.themeFragment) { defaultViewModelProviderFactory }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.resetTheme))
        .setMessage(getString(R.string.resetThemeMessage))
        .setPositiveButton(R.string.reset) { _, _ ->
            themeViewModel.resetTheme()
            findNavController().navigateUp()
        }
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .create()
}
