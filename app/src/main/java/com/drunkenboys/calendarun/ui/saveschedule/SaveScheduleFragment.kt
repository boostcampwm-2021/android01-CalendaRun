package com.drunkenboys.calendarun.ui.saveschedule

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.ScheduleNotificationType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveScheduleFragment : BaseFragment<FragmentSaveScheduleBinding>(R.layout.fragment_save_schedule) {

    private val viewModel: SaveScheduleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        observeNotification()
        observeTagColor()
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
