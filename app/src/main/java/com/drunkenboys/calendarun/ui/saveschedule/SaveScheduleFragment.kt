package com.drunkenboys.calendarun.ui.saveschedule

import android.app.AlarmManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.databinding.FragmentSaveScheduleBinding
import com.drunkenboys.calendarun.receiver.ScheduleAlarmReceiver
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.*
import com.drunkenboys.calendarun.util.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@AndroidEntryPoint
class SaveScheduleFragment : BaseFragment<FragmentSaveScheduleBinding>(R.layout.fragment_save_schedule) {

    private val saveScheduleViewModel: SaveScheduleViewModel
            by navGraphViewModels(R.id.saveScheduleFragment) { defaultViewModelProviderFactory }

    private val args: SaveScheduleFragmentArgs by navArgs()

    private var notificationPopupWidow: ListPopupWindow? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = saveScheduleViewModel

        setupToolbar()
        setupNotificationPopupWindow()
        collectPickDateTimeEvent()
        collectPickNotificationTypeEvent()
        collectNotificationType()
        collectTagColor()
        collectIsPickTagColorPopupVisible()
        collectSaveScheduleEvent()
        collectDeleteScheduleEvent()
        collectBlankTitleEvent()
    }

    private fun setupToolbar() = with(binding) {
        setupToolbarTitle()
        setupToolbarMenuItemClickListener()
        toolbarSaveSchedule.setupWithNavController(navController)
    }

    private fun setupToolbarTitle() = with(binding) {
        if (args.scheduleId == 0L) {
            toolbarSaveSchedule.title = getString(R.string.saveSchedule_toolbarTitle_insert)
        } else {
            toolbarSaveSchedule.title = getString(R.string.saveSchedule_toolbarTitle_update)
            toolbarSaveSchedule.inflateMenu(R.menu.menu_save_schedule_toolbar)
        }
    }

    private fun setupToolbarMenuItemClickListener() = with(binding) {
        toolbarSaveSchedule.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_delete_schedule) {
                navController.navigate(SaveScheduleFragmentDirections.toDeleteScheduleDialog())
                true
            } else {
                false
            }
        }
    }

    private fun setupNotificationPopupWindow() {
        val dropDownAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.saveSchedule_notificationType,
            R.layout.item_drop_down_list
        )
        notificationPopupWidow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle).apply {
            anchorView = binding.tvSaveScheduleNotification
            setAdapter(dropDownAdapter)
            isModal = true
            setOnItemClickListener { _, _, position, _ ->
                saveScheduleViewModel.updateNotificationType(Schedule.NotificationType.values()[position])
                dismiss()
            }
        }
    }

    private fun collectPickDateTimeEvent() {
        sharedCollect(saveScheduleViewModel.pickDateTimeEvent) { (dateType, localDateTime) ->
            val dateInMillis = pickDateInMillis(localDateTime.toLocalDate()) ?: return@sharedCollect

            val (hour, minute) = pickTime(localDateTime.toLocalTime()) ?: return@sharedCollect

            val dateTime = LocalDateTime.ofEpochSecond(dateInMillis / 1000, 0, ZoneOffset.UTC)
                .withHour(hour)
                .withMinute(minute)

            saveScheduleViewModel.updateDate(dateTime, dateType)
        }
    }

    private fun collectPickNotificationTypeEvent() {
        sharedCollect(saveScheduleViewModel.pickNotificationTypeEvent) {
            notificationPopupWidow?.show()
        }
    }

    private fun collectNotificationType() {
        stateCollect(saveScheduleViewModel.notificationType) { type ->
            binding.tvSaveScheduleNotification.text = when (type) {
                Schedule.NotificationType.NONE -> getString(R.string.saveSchedule_notificationNone)
                Schedule.NotificationType.TEN_MINUTES_AGO -> getString(R.string.saveSchedule_notificationTenMinutesAgo)
                Schedule.NotificationType.A_HOUR_AGO -> getString(R.string.saveSchedule_notificationAHourAgo)
                Schedule.NotificationType.A_DAY_AGO -> getString(R.string.saveSchedule_notificationADayAgo)
            }
        }
    }

    private fun collectTagColor() {
        stateCollect(saveScheduleViewModel.tagColor) { color ->
            binding.viewSaveScheduleTagColor.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun collectIsPickTagColorPopupVisible() {
        stateCollect(saveScheduleViewModel.isPickTagColorPopupVisible) { isVisible ->
            togglePickTagColorPopup(isVisible)
        }
    }

    private fun togglePickTagColorPopup(isVisible: Boolean) = with(binding.layoutSaveSchedulePickTagColorPopup) {
        if (isVisible) {
            root.isVisible = true
            root.isFocusable = true
            root.startAnimation(R.anim.show_scale_up)
        } else {
            root.isFocusable = false
            root.startAnimation(R.anim.hide_scale_down) {
                root.isVisible = false
            }
        }
    }

    private fun collectSaveScheduleEvent() {
        sharedCollect(saveScheduleViewModel.saveScheduleEvent) { schedule ->
            saveNotification(schedule)
            showToast(getString(R.string.toast_schedule_saved))
            navController.navigateUp()
        }
    }

    private fun saveNotification(schedule: Schedule) {
        val alarmManager = requireContext().getSystemService<AlarmManager>() ?: return

        val triggerAtMillis = schedule.notificationDateTimeMillis()
        if (System.currentTimeMillis() > triggerAtMillis) return

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            ScheduleAlarmReceiver.createPendingIntent(requireContext(), schedule)
        )
    }

    private fun collectDeleteScheduleEvent() {
        sharedCollect(saveScheduleViewModel.deleteScheduleEvent) { schedule ->
            deleteNotification(schedule)
            showToast(getString(R.string.toast_schedule_deleted))
            navController.navigateUp()
        }
    }

    private fun deleteNotification(schedule: Schedule) {
        val alarmManager = requireContext().getSystemService<AlarmManager>() ?: return

        alarmManager.cancel(ScheduleAlarmReceiver.createPendingIntent(requireContext(), schedule))
    }

    private fun collectBlankTitleEvent() {
        sharedCollect(saveScheduleViewModel.blankTitleEvent) {
            binding.etSaveScheduleTitleInput.isError = true
        }
    }
}
