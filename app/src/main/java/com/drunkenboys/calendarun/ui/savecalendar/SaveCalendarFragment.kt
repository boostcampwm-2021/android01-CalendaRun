package com.drunkenboys.calendarun.ui.savecalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.savecalendar.model.CheckPointItem
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.util.showDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveCalendarFragment : BaseFragment<FragmentSaveCalendarBinding>(R.layout.fragment_save_calendar) {

    private val saveCalendarViewModel by viewModels<SaveCalendarViewModel>()
    private val saveCalendarAdapter by lazy { SaveCalendarRecyclerViewAdapter(viewLifecycleOwner) }
    private val navController by lazy { findNavController() }

    private val args: SaveCalendarFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setDataBinding()
        setRecyclerViewAdapter()
        setAddCheckPointViewClickListener()
        setPickDateTimeEventObserver()
        setSaveCalendarEventObserver()
    }

    private fun setupToolbar() = with(binding) {
        toolbarSaveCalendar.setupWithNavController(navController)
        when (args.behaviorType) {
            BehaviorType.INSERT -> toolbarSaveCalendar.title = "달력 추가"
            BehaviorType.UPDATE -> toolbarSaveCalendar.title = "달력 수정"
        }
    }

    private fun setDataBinding() {
        binding.saveCalendarViewModel = saveCalendarViewModel
    }

    private fun setRecyclerViewAdapter() {
        binding.rSaveCalendarCheckPointList.adapter = saveCalendarAdapter
    }

    private fun setAddCheckPointViewClickListener() {
        binding.tvSaveCalendarAddCheckPointView.setOnClickListener {
            val newList = emptyList<CheckPointItem>().toMutableList()
            newList.add(CheckPointItem())
            newList.addAll(saveCalendarAdapter.currentList)
            saveCalendarAdapter.submitList(newList)
        }
    }

    private fun setPickDateTimeEventObserver() {
        saveCalendarViewModel.pickStartDateEvent.observe(viewLifecycleOwner) {
            showDatePickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                saveCalendarViewModel.setCalendarStartDate(getString(R.string.ui_date_format, year, month, dayOfMonth))
            }
        }
        saveCalendarViewModel.pickEndDateEvent.observe(viewLifecycleOwner) {
            showDatePickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                saveCalendarViewModel.setCalendarEndDate(getString(R.string.ui_date_format, year, month, dayOfMonth))
            }
        }

    }

    private fun setSaveCalendarEventObserver() {
        saveCalendarViewModel.saveCalendarEvent.observe(viewLifecycleOwner) {
            val checkPointItemList = saveCalendarAdapter.currentList
            saveCalendarViewModel.setCheckPointItemList(checkPointItemList)
            saveCalendarViewModel.saveCalendar()
            navController.navigateUp()
        }
    }
}
