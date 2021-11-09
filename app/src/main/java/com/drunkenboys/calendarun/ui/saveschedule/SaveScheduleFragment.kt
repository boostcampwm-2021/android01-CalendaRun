package com.drunkenboys.calendarun.ui.saveschedule

import android.app.AlarmManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.getSystemService
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
import com.drunkenboys.calendarun.receiver.ScheduleAlarmReceiver
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.util.notificationDate
import com.drunkenboys.calendarun.util.pickDateInMillis
import com.drunkenboys.calendarun.util.pickTime
import com.drunkenboys.calendarun.util.startAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SaveScheduleFragment : BaseFragment<FragmentSaveScheduleBinding>(R.layout.fragment_save_schedule) {

    private val saveScheduleViewModel: SaveScheduleViewModel
            by navGraphViewModels(R.id.saveScheduleFragment) { defaultViewModelProviderFactory }

    private val navController by lazy { findNavController() }
    private val args: SaveScheduleFragmentArgs by navArgs()

    private var notificationPopupWidow: ListPopupWindow? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = saveScheduleViewModel

        initToolbar()
        initNotificationPopupWindow()
        observePickDateTimeEvent()
        observePickNotificationTypeEvent()
        observeNotification()
        observeTagColor()
        observeIsPickTagColorPopupVisible()
        observeSaveScheduleEvent()
        observeDeleteScheduleEvent()

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

    private fun initNotificationPopupWindow() {
        val dropDownAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.saveSchedule_notificationType,
            R.layout.item_drop_down_list
        )
        notificationPopupWidow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle).apply {
            anchorView = binding.tvSaveScheduleNotification
            setAdapter(dropDownAdapter)
            setOnItemClickListener { _, _, position, _ ->
                saveScheduleViewModel.notificationType.value = Schedule.NotificationType.values()[position]
                dismiss()
            }
        }
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

                saveScheduleViewModel.updateDate(calendar.time, dateType)
            }
        }
    }

    private fun observePickNotificationTypeEvent() {
        saveScheduleViewModel.pickNotificationTypeEvent.observe(viewLifecycleOwner) {
            notificationPopupWidow?.show()
        }
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

    private fun togglePickTagColorPopup(isVisible: Boolean) = with(binding.layoutSaveSchedulePickTagColorPopup) {
        if (isVisible) {
            root.isVisible = true
            root.startAnimation(R.anim.show_scale_up)
        } else {
            root.startAnimation(R.anim.hide_scale_down) {
                root.isVisible = false
            }
        }
    }

    private fun observeSaveScheduleEvent() {
        saveScheduleViewModel.saveScheduleEvent.observe(viewLifecycleOwner) { schedule ->
            saveNotification(schedule)
            navController.navigateUp()
        }
    }

    private fun saveNotification(schedule: Schedule) {
        val alarmManager = requireContext().getSystemService<AlarmManager>() ?: return

        val triggerAtMillis = schedule.notificationDate()
        val today = Calendar.getInstance()
        if (today.timeInMillis > triggerAtMillis) return

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            ScheduleAlarmReceiver.createPendingIntent(requireContext(), schedule)
        )
    }

    private fun observeDeleteScheduleEvent() {
        saveScheduleViewModel.deleteScheduleEvent.observe(viewLifecycleOwner) { schedule ->
            deleteNotification(schedule)
            navController.navigateUp()
        }
    }

    private fun deleteNotification(schedule: Schedule) {
        val alarmManager = requireContext().getSystemService<AlarmManager>() ?: return

        alarmManager.cancel(ScheduleAlarmReceiver.createPendingIntent(requireContext(), schedule))
    }
}
