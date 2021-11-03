package com.drunkenboys.calendarun.ui.saveschedule

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.ui.saveschedule.model.ScheduleNotificationType
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

@AndroidEntryPoint
class SaveScheduleFragment : BaseFragment<FragmentSaveScheduleBinding>(R.layout.fragment_save_schedule) {

    private val viewModel: SaveScheduleViewModel by viewModels()

    private val navController by lazy { findNavController() }
    private val args: SaveScheduleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        init()
        viewModel.init(args.scheduleId, args.calendarId, args.calendarName, args.behaviorType)
        observeNotification()
        observeTagColor()
    }

    private fun init() {
        initToolbar()
        initTimePicker()
        initTvNotification()
    }

    private fun initToolbar() = with(binding) {
        when (args.behaviorType) {
            BehaviorType.INSERT -> toolbarSaveSchedule.title = "일정 추가"
            BehaviorType.UPDATE -> {
                toolbarSaveSchedule.title = "일정 수정"
                toolbarSaveSchedule.inflateMenu(R.menu.menu_save_schedule_toolbar)
            }
        }
        toolbarSaveSchedule.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_delete_schedule) {
                DeleteScheduleDialog().show(parentFragmentManager, DeleteScheduleDialog::class.simpleName)
                true
            } else
                false
        }

        val appBarConfig = AppBarConfiguration(navController.graph)
        toolbarSaveSchedule.setupWithNavController(navController, appBarConfig)
    }

    private fun initTimePicker() {
        binding.tvSaveScheduleScheduleStartInput.setOnClickListener { selectTime(viewModel.startDate) }
        binding.tvSaveScheduleScheduleEndInput.setOnClickListener { selectTime(viewModel.endDate) }
    }

    private fun selectTime(liveData: MutableLiveData<Date>) {
        lifecycleScope.launch {
            val calendar = Calendar.getInstance()

            calendar.timeInMillis = pickDateInMillis() ?: return@launch
            val (hour, minute) = pickTime() ?: return@launch
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            liveData.value = calendar.time
        }
    }

    private suspend fun pickDateInMillis() = suspendCancellableCoroutine<Long?> { cont ->
        // TODO: 2021-11-03 picker 생성을 util 패키지로 분리 고려
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.pick_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.apply {
            addOnPositiveButtonClickListener { timeInMillis -> cont.resume(timeInMillis) }
            addOnCancelListener { if (cont.isActive) cont.resume(null) }
            addOnDismissListener { if (cont.isActive) cont.resume(null) }
            addOnNegativeButtonClickListener { if (cont.isActive) cont.resume(null) }
        }

        datePicker.show(parentFragmentManager, this@SaveScheduleFragment::class.simpleName)
    }

    private suspend fun pickTime() = suspendCancellableCoroutine<Pair<Int, Int>?> { cont ->
        // TODO: 2021-11-03 picker 생성을 util 패키지로 분리 고려
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(R.string.pick_time)
            .build()
        timePicker.apply {
            addOnPositiveButtonClickListener { cont.resume(timePicker.hour to timePicker.minute) }
            addOnCancelListener { if (cont.isActive) cont.resume(null) }
            addOnDismissListener { if (cont.isActive) cont.resume(null) }
            addOnNegativeButtonClickListener { if (cont.isActive) cont.resume(null) }
        }

        timePicker.show(parentFragmentManager, this@SaveScheduleFragment::class.simpleName)
    }

    private fun initTvNotification() {
        val dropDownAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.notification_type, R.layout.item_drop_down_list)
        val listPopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle).apply {
            anchorView = binding.tvSaveScheduleNotification
            setAdapter(dropDownAdapter)
            setOnItemClickListener { _, _, position, _ ->
                viewModel.notification.value = ScheduleNotificationType.values()[position]
                dismiss()
            }
        }

        binding.tvSaveScheduleNotification.setOnClickListener {
            listPopupWindow.show()
        }
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
