package com.drunkenboys.calendarun.ui.savecalendar

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.savecalendar.model.SliceItem
import com.drunkenboys.calendarun.util.extensions.launchAndRepeatWithViewLifecycle
import com.drunkenboys.calendarun.util.extensions.pickRangeDateInMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class SaveCalendarFragment : BaseFragment<FragmentSaveCalendarBinding>(R.layout.fragment_save_calendar) {

    private val saveCalendarViewModel by viewModels<SaveCalendarViewModel>()
    private val saveCalendarAdapter by lazy { SaveCalendarAdapter(viewLifecycleOwner, ::onSliceClick) }

    private val args: SaveCalendarFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupDataBinding()
        setupRecyclerView()
        setupToolbarMenuOnItemClickListener()

        launchAndRepeatWithViewLifecycle {
            launch { collectSliceItemList() }
            launch { collectSaveCalendarEvent() }
            launch { collectUseDefaultCalendar() }
            launch { collectBlankTitleEvent() }
        }
    }

    private fun onSliceClick(sliceItem: SliceItem) {
        lifecycleScope.launch {
            val dateInMillis = pickRangeDateInMillis() ?: return@launch

            val startTime = LocalDate.ofEpochDay(dateInMillis.first / 1000 / 60 / 60 / 24)
            val endTime = LocalDate.ofEpochDay(dateInMillis.second / 1000 / 60 / 60 / 24)

            sliceItem.startDate.emit(startTime)
            sliceItem.endDate.emit(endTime)
        }
    }

    private fun setupToolbar() = with(binding) {
        toolbarSaveCalendar.setupWithNavController(navController)

        if (args.calendarId == 0L) {
            toolbarSaveCalendar.title = getString(R.string.calendar_add)
        } else {
            toolbarSaveCalendar.title = getString(R.string.calendar_edit)
        }
    }

    private fun setupDataBinding() {
        binding.saveCalendarViewModel = saveCalendarViewModel
    }

    private fun setupRecyclerView() {
        binding.rvSaveCalendarSliceList.adapter = saveCalendarAdapter
    }

    private fun setupToolbarMenuOnItemClickListener() {
        binding.toolbarSaveCalendar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_delete_slice) {
                val currentSliceItemList = saveCalendarAdapter.currentList
                saveCalendarViewModel.deleteSliceItem(currentSliceItemList)
                true
            } else {
                false
            }
        }
    }

    private suspend fun collectSliceItemList() {
        saveCalendarViewModel.sliceItemList.collect { list ->
            saveCalendarAdapter.submitList(list.sortedWith(compareBy(nullsLast()) { it.startDate.value }))
            delay(300)
            binding.svSaveCalendar.smoothScrollTo(0, binding.tvSaveCalendarSaveCalendar.bottom)
        }
    }

    private suspend fun collectUseDefaultCalendar() {
        // TODO: data Binding 으로 이동
        saveCalendarViewModel.useDefaultCalendar.collect { checked ->
            with(binding) {
                rvSaveCalendarSliceList.isVisible = !checked
                tvSaveCalendarAddSliceView.isVisible = !checked
                toolbarSaveCalendar.menu.findItem(R.id.menu_delete_slice).isVisible = !checked
                tvSaveCalendarSliceCaption.setTextColor(
                    if (checked) {
                        ContextCompat.getColor(requireContext(), R.color.light_grey)
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.background_black)
                    }
                )
            }
        }
    }

    private suspend fun collectSaveCalendarEvent() {
        saveCalendarViewModel.saveCalendarEvent.collect { isSaved ->
            if (isSaved) {
                navController.navigateUp()
            }
        }
    }

    private suspend fun collectBlankTitleEvent() {
        saveCalendarViewModel.blankTitleEvent.collect {
            binding.etSaveCalendarCalendarName.isError = true
        }
    }
}
