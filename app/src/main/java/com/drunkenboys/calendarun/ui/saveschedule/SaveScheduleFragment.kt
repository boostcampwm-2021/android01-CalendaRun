package com.drunkenboys.calendarun.ui.saveschedule

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.ScheduleNotificationType
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SaveScheduleFragment : BaseFragment<FragmentSaveScheduleBinding>(R.layout.fragment_save_schedule) {

    private val viewModel: SaveScheduleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        initViews()
        observeNotification()
        observeTagColor()
    }

    private fun initViews() {
        binding.tvSaveScheduleScheduleStartInput.setOnClickListener { selectTime(viewModel.startDate) }
        binding.tvSaveScheduleScheduleEndInput.setOnClickListener { selectTime(viewModel.endDate) }
    }

    private fun selectTime(target: MutableLiveData<Date>) {
        val calendar = Calendar.getInstance()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("요일 선택")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val select = Date(it)
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            Log.d("SaveScheduleFragment", "select day: ${df.format(select)}")
            calendar.time = select
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("시간 선택")
                .build()
            timePicker.addOnPositiveButtonClickListener {
                Log.d("SaveScheduleFragment", "hour: ${timePicker.hour}")
                Log.d("SaveScheduleFragment", "minute: ${timePicker.minute}")
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)

                target.value = calendar.time
            }
            timePicker.show(parentFragmentManager, this@SaveScheduleFragment::class.simpleName)
        }
        datePicker.show(parentFragmentManager, this@SaveScheduleFragment::class.simpleName)
    }

    private fun observeNotification() {
        viewModel.notification.observe(viewLifecycleOwner) { type ->
            binding.tvSaveScheduleNotification.text = when (type) {
                ScheduleNotificationType.NONE -> getString(R.string.notification_none)
                ScheduleNotificationType.TEN_MINUTES_AGO -> getString(R.string.notification_ten_minutes_ago)
                ScheduleNotificationType.A_HOUR_AGO -> getString(R.string.notification_a_hour_ago)
                ScheduleNotificationType.A_DAY_AGO -> getString(R.string.notification_a_day_ago)
                null -> ""
            }
        }
    }

    private fun observeTagColor() {
        viewModel.tagColor.observe(viewLifecycleOwner) { colorRes ->
            binding.viewSaveScheduleTagColor.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
        }
    }
}
