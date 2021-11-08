package com.drunkenboys.calendarun.ui.saveschedule

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.databinding.FragmentSaveScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.ui.saveschedule.model.DateType
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

    private val saveScheduleViewModel: SaveScheduleViewModel
            by navGraphViewModels(R.id.saveScheduleFragment) { defaultViewModelProviderFactory }

    private val navController by lazy { findNavController() }
    private val args: SaveScheduleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = saveScheduleViewModel

        initToolbar()
        observePickDateTimeEvent()
        observePickNotificationTypeEvent()
        observeNotification()
        observeTagColor()
        observeIsPickTagColorPopupVisible()

        saveScheduleViewModel.init(args.behaviorType)
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
                navController.navigate(SaveScheduleFragmentDirections.actionSaveScheduleFragmentToDeleteScheduleDialog())
                true
            } else {
                false
            }
        }

        val appBarConfig = AppBarConfiguration(navController.graph)
        toolbarSaveSchedule.setupWithNavController(navController, appBarConfig)
    }

    private fun observePickDateTimeEvent() {
        saveScheduleViewModel.pickDateTimeEvent.observe(viewLifecycleOwner) { dateType ->
            dateType ?: return@observe

            lifecycleScope.launch {
                val calendar = Calendar.getInstance()

                calendar.timeInMillis = pickDateInMillis() ?: return@launch
                val (hour, minute) = pickTime() ?: return@launch
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                when (dateType) {
                    DateType.START -> saveScheduleViewModel.startDate.value = calendar.time
                    DateType.END -> saveScheduleViewModel.endDate.value = calendar.time
                }
            }
        }
    }

    private fun observePickNotificationTypeEvent() {
        saveScheduleViewModel.pickNotificationTypeEvent.observe(viewLifecycleOwner) {
            val dropDownAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.saveSchedule_notificationType,
                R.layout.item_drop_down_list
            )
            val listPopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle).apply {
                anchorView = binding.tvSaveScheduleNotification
                setAdapter(dropDownAdapter)
                setOnItemClickListener { _, _, position, _ ->
                    saveScheduleViewModel.notificationType.value = Schedule.NotificationType.values()[position]
                    dismiss()
                }
            }

            binding.tvSaveScheduleNotification.setOnClickListener {
                listPopupWindow.show()
            }
        }
    }

    private suspend fun pickDateInMillis() = suspendCancellableCoroutine<Long?> { cont ->
        // TODO: 2021-11-03 picker 생성을 util 패키지로 분리 고려
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.saveSchedule_pickDate))
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
            .setTitleText(R.string.saveSchedule_pickTime)
            .build()
        timePicker.apply {
            addOnPositiveButtonClickListener { cont.resume(timePicker.hour to timePicker.minute) }
            addOnCancelListener { if (cont.isActive) cont.resume(null) }
            addOnDismissListener { if (cont.isActive) cont.resume(null) }
            addOnNegativeButtonClickListener { if (cont.isActive) cont.resume(null) }
        }

        timePicker.show(parentFragmentManager, this@SaveScheduleFragment::class.simpleName)
    }

    private fun observeNotification() {
        saveScheduleViewModel.notificationType.observe(viewLifecycleOwner) { type ->
            binding.tvSaveScheduleNotification.text = when (type) {
                Schedule.NotificationType.NONE -> getString(R.string.saveSchedule_notificationNone)
                Schedule.NotificationType.TEN_MINUTES_AGO -> getString(R.string.saveSchedule_notificationTenMinutesAgo)
                Schedule.NotificationType.A_HOUR_AGO -> getString(R.string.saveSchedule_notificationAHourAgo)
                Schedule.NotificationType.A_DAY_AGO -> getString(R.string.saveSchedule_notificationADayAgo)
                null -> ""
            }
        }
    }

    private fun observeTagColor() {
        saveScheduleViewModel.tagColor.observe(viewLifecycleOwner) { color ->
            color ?: return@observe
            binding.viewSaveScheduleTagColor.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun observeIsPickTagColorPopupVisible() {
        saveScheduleViewModel.isPickTagColorPopupVisible.observe(viewLifecycleOwner, ::togglePickTagColorPopup)
    }

    private fun togglePickTagColorPopup(isVisible: Boolean) {
        if (isVisible) {
            binding.layoutSaveSchedulePickTagColorPopup.root.isVisible = true
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.show_scale_up)
            binding.layoutSaveSchedulePickTagColorPopup.root.startAnimation(anim)
        } else {
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.hide_scale_down)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    binding.layoutSaveSchedulePickTagColorPopup.root.isVisible = false
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            binding.layoutSaveSchedulePickTagColorPopup.root.startAnimation(anim)
        }
    }
}
