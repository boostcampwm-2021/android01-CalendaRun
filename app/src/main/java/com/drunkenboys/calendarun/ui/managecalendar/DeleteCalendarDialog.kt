package com.drunkenboys.calendarun.ui.managecalendar

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.drunkenboys.calendarun.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCalendarDialog : DialogFragment() {

    private val manageCalendarViewModel: ManageCalendarViewModel
            by navGraphViewModels(R.id.manageCalendarFragment) { defaultViewModelProviderFactory }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.deleteCalendar_title))
        .setMessage(getString(R.string.deleteCalendar_message))
        .setPositiveButton(getString(R.string.delete)) { _, _ ->
            manageCalendarViewModel.emitDeleteCalendarEvent()
            findNavController().navigateUp()
        }
        .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        .create()
}
