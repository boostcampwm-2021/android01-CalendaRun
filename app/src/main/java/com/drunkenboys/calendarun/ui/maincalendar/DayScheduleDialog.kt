package com.drunkenboys.calendarun.ui.maincalendar

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.drunkenboys.calendarun.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayScheduleDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(requireContext())
        .setView(R.layout.dialog_day_schedule)
        .create()
}
