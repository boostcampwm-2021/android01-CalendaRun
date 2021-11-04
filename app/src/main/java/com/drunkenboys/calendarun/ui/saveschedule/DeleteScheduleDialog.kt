package com.drunkenboys.calendarun.ui.saveschedule

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import com.drunkenboys.calendarun.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteScheduleDialog : DialogFragment() {

    private val viewModel: SaveScheduleViewModel by navGraphViewModels(R.id.saveScheduleFragment)

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog = AlertDialog.Builder(requireContext())
        .setTitle(R.string.deleteScheduleDialog_title)
        .setMessage(R.string.deleteScheduleDialog_message)
        .setPositiveButton(R.string.menuSaveScheduleToolbar_delete) { _, _ ->
            // TODO: 2021-11-04 일정 삭제 요청
        }
        .create()
}
