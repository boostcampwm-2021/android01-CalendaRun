package com.drunkenboys.calendarun.ui.dayschedule

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.DialogDayScheduleBinding
import com.drunkenboys.calendarun.util.extensions.sharedCollect
import com.drunkenboys.calendarun.util.extensions.stateCollect
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class DayScheduleDialog : DialogFragment() {

    private var _binding: DialogDayScheduleBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException()

    private val dayScheduleViewModel by viewModels<DayScheduleViewModel>()

    private val dayScheduleAdapter = DayScheduleAdapter()

    private val navController by lazy { findNavController() }
    private val args by navArgs<DayScheduleDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        _binding = DialogDayScheduleBinding.inflate(layoutInflater)
        binding.viewModel = dayScheduleViewModel
        binding.lifecycleOwner = this
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setupRvDaySchedule()
        setupImgAddSchedule()
        collectListItem()
        collectScheduleClickEvent()

        dayScheduleViewModel.fetchScheduleList(LocalDate.parse(args.localDate))

        return binding.root
    }

    private fun setupRvDaySchedule() {
        binding.rvDaySchedule.adapter = dayScheduleAdapter
    }

    private fun setupImgAddSchedule() {
        binding.imgDayScheduleAddSchedule.setOnClickListener {
            val action = DayScheduleDialogDirections.toSaveSchedule(args.calendarId, 0, localDate = args.localDate)
            navController.navigate(action)
        }
    }

    private fun collectListItem() {
        stateCollect(dayScheduleViewModel.listItem) { listItem ->
            dayScheduleAdapter.submitList(listItem)
        }
    }

    private fun collectScheduleClickEvent() {
        sharedCollect(dayScheduleViewModel.scheduleClickEvent) { schedule ->
            val action = DayScheduleDialogDirections.toSaveSchedule(schedule.calendarId, schedule.id)
            navController.navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()

        val displayMetrics = resources.displayMetrics
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_white_radius_24dp)
        val (width, height) = (displayMetrics.widthPixels * 0.9).toInt() to (displayMetrics.heightPixels * 0.7).toInt()
        dialog?.window?.setLayout(width, height)
        binding.root.layoutParams.height = height
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
