package com.drunkenboys.calendarun.ui.saveschedule

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveScheduleFragment : BaseFragment<FragmentSaveScheduleBinding>(R.layout.fragment_save_schedule) {

    private val viewModel: SaveScheduleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        observeTagColor()
    }

    private fun observeTagColor() {
        viewModel.tagColor.observe(viewLifecycleOwner) {
            binding.viewSaveScheduleTagColor.backgroundTintList = ContextCompat.getColorStateList(requireContext(), it)
        }
    }
}
