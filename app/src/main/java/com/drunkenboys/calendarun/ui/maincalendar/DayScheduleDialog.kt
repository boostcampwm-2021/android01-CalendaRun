package com.drunkenboys.calendarun.ui.maincalendar

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.drunkenboys.calendarun.databinding.DialogDayScheduleBinding
import com.drunkenboys.calendarun.ui.searchschedule.SearchScheduleAdapter
import com.drunkenboys.calendarun.ui.searchschedule.SearchScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayScheduleDialog : DialogFragment() {

    private var _binding: DialogDayScheduleBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException()

    private val dayScheduleViewModel by viewModels<SearchScheduleViewModel>()

    private val dayScheduleAdapter = SearchScheduleAdapter()

    private val navController by lazy { findNavController() }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        _binding = DialogDayScheduleBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
