package com.drunkenboys.calendarun.ui.addcalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentAddCalendarBinding
import com.drunkenboys.calendarun.showDatePickerDialog
import com.drunkenboys.calendarun.toStringDateFormat
import com.drunkenboys.calendarun.ui.addcalendar.adapter.AddCalendarRecyclerViewAdapter
import com.drunkenboys.calendarun.ui.base.BaseFragment

class AddCalendarFragment : BaseFragment<FragmentAddCalendarBinding>(R.layout.fragment_add_calendar) {

    private val recyclerViewAdapter by lazy { AddCalendarRecyclerViewAdapter() }
    private val addCalendarViewModel by viewModels<AddCalendarViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRecyclerViewAdapter()
        setTextDatePickerClickListener()
        setCalendarObserver()
        setCheckPointListObserver()
    }

    private fun setRecyclerViewAdapter() {
        binding.rAddCalendarCheckPointList.adapter = recyclerViewAdapter
    }

    private fun setTextDatePickerClickListener() {
        binding.tvAddCalendarStartDatePicker.setOnClickListener {
            showDatePickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                addCalendarViewModel.setCalendarStartDate(getString(R.string.ui_date_format, year, month, dayOfMonth))
            }
        }
        binding.tvAddCalendarEndDatePicker.setOnClickListener {
            showDatePickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                addCalendarViewModel.setCalendarEndDate(getString(R.string.ui_date_format, year, month, dayOfMonth))
            }
        }
    }

    private fun setCalendarObserver() {
        addCalendarViewModel.calendar.observe(viewLifecycleOwner, { calendar ->
            with(binding) {
                tvAddCalendarCalendarName.text = calendar.name
                tvAddCalendarStartDatePicker.text = toStringDateFormat(calendar.startDate)
                tvAddCalendarEndDatePicker.text = toStringDateFormat(calendar.endDate)
            }
        })
    }

    private fun setCheckPointListObserver() {
        addCalendarViewModel.checkPointList.observe(viewLifecycleOwner, { checkPointList ->
            recyclerViewAdapter.submitList(checkPointList)
        })
    }
}
