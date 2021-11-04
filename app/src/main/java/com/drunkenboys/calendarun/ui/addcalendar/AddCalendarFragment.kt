package com.drunkenboys.calendarun.ui.addcalendar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentAddCalendarBinding
import com.drunkenboys.calendarun.showDatePickerDialog
import com.drunkenboys.calendarun.toStringDateFormat
import com.drunkenboys.calendarun.ui.addcalendar.adapter.AddCalendarRecyclerViewAdapter
import com.drunkenboys.calendarun.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCalendarFragment : BaseFragment<FragmentAddCalendarBinding>(R.layout.fragment_add_calendar) {

    private val recyclerViewAdapter by lazy { AddCalendarRecyclerViewAdapter() }
    private val addCalendarViewModel by viewModels<AddCalendarViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataBinding()
        setRecyclerViewAdapter()
        setTextDatePickerClickListener()
        setCheckPointListObserver()
        setCalendarNameChangeListener()
    }

    private fun setDataBinding() {
        binding.addCalendarViewModel = addCalendarViewModel
    }

    private fun setRecyclerViewAdapter() {
        binding.rAddCalendarCheckPointList.adapter = recyclerViewAdapter
    }

    private fun setCalendarNameChangeListener() {
        binding.etAddCalendarCalendarName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(name: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addCalendarViewModel.setCalendarName(name.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}
        })
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

    private fun setCheckPointListObserver() {
        addCalendarViewModel.checkPointList.observe(viewLifecycleOwner, { checkPointList ->
            val checkPointModelList = mutableListOf<CheckPointModel>()
            checkPointList.forEach { checkPoint ->
                val checkPointModel = CheckPointModel(
                    checkPoint.name,
                    toStringDateFormat(checkPoint.endDate)
                )
                checkPointModelList.add(checkPointModel)
            }
            recyclerViewAdapter.submitList(checkPointModelList)
        })
    }
}
