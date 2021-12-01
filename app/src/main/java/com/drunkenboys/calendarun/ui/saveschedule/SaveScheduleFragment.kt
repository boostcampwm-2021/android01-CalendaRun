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
import com.drunkenboys.calendarun.ui.searchschedule.SearchScheduleFragment
import com.drunkenboys.calendarun.util.*
import com.drunkenboys.calendarun.util.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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

        launchAndRepeatWithViewLifecycle {
            launch { collectPickDateTimeEvent() }
            launch { collectPickNotificationTypeEvent() }
            launch { collectNotificationType() }
            launch { collectTagColor() }
            launch { collectIsPickTagColorPopupVisible() }
            launch { collectSaveScheduleEvent() }
            launch { collectDeleteScheduleEvent() }
            launch { collectBlankTitleEvent() }
        }
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

    private suspend fun collectPickDateTimeEvent() {
        saveScheduleViewModel.pickDateTimeEvent.collect { (dateType, localDateTime) ->
            val dateInMillis = pickDateInMillis(localDateTime.toLocalDate()) ?: return@collect

            val (hour, minute) = pickTime(localDateTime.toLocalTime()) ?: return@collect

            val dateTime = LocalDateTime.ofEpochSecond(dateInMillis / 1000, 0, ZoneOffset.UTC)
                .withHour(hour)
                .withMinute(minute)

            saveScheduleViewModel.updateDate(dateTime, dateType)
        }
    }

    private suspend fun collectPickNotificationTypeEvent() {
        saveScheduleViewModel.pickNotificationTypeEvent.collect {
            notificationPopupWidow?.show()
        }
    }

    private suspend fun collectNotificationType() {
        saveScheduleViewModel.notificationType.collect { type ->
            binding.tvSaveScheduleNotification.text = when (type) {
                Schedule.NotificationType.NONE -> getString(R.string.saveSchedule_notificationNone)
                Schedule.NotificationType.TEN_MINUTES_AGO -> getString(R.string.saveSchedule_notificationTenMinutesAgo)
                Schedule.NotificationType.A_HOUR_AGO -> getString(R.string.saveSchedule_notificationAHourAgo)
                Schedule.NotificationType.A_DAY_AGO -> getString(R.string.saveSchedule_notificationADayAgo)
            }
        }
    }

    private suspend fun collectTagColor() {
        saveScheduleViewModel.tagColor.collect { color ->
            binding.viewSaveScheduleTagColor.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private suspend fun collectIsPickTagColorPopupVisible() {
        saveScheduleViewModel.isPickTagColorPopupVisible.collect { isVisible ->
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

    private suspend fun collectSaveScheduleEvent() {
        saveScheduleViewModel.saveScheduleEvent.collect { (schedule, calendarName) ->
            saveNotification(schedule, calendarName)
            showToast(getString(R.string.toast_schedule_saved))
            navController.navigateUp()
        }
    }

    private fun saveNotification(schedule: Schedule, calendarName: String) {
        val alarmManager = requireContext().getSystemService<AlarmManager>() ?: return

        val triggerAtMillis = schedule.notificationDateTimeMillis()
        if (System.currentTimeMillis() > triggerAtMillis) return

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            ScheduleAlarmReceiver.createPendingIntent(requireContext(), schedule, calendarName)
        )
    }

    private suspend fun collectDeleteScheduleEvent() {
        saveScheduleViewModel.deleteScheduleEvent.collect { (schedule, calendarName) ->
            deleteNotification(schedule, calendarName)
            tryNotifyDeleteEvent(schedule.id)
            navController.navigateUp()
        }
    }

    private fun deleteNotification(schedule: Schedule, calendarName: String) {
        val alarmManager = requireContext().getSystemService<AlarmManager>() ?: return

        alarmManager.cancel(ScheduleAlarmReceiver.createPendingIntent(requireContext(), schedule, calendarName))
    }

    private fun tryNotifyDeleteEvent(id: Long) {
        if (navController.previousBackStackEntry?.destination?.id == R.id.searchScheduleFragment) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set(SearchScheduleFragment.DELETE_SCHEDULE_ID, id)
        }
    }

    private suspend fun collectBlankTitleEvent() {
        saveScheduleViewModel.blankTitleEvent.collect {
            binding.etSaveScheduleTitleInput.isError = true
        }
    }
}
