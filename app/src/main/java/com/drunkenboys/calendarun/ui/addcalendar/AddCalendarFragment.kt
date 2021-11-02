package com.drunkenboys.calendarun.ui.addcalendar

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentAddCalendarBinding
import com.drunkenboys.calendarun.ui.addcalendar.adapter.AddCalendarRecyclerViewAdapter
import com.drunkenboys.calendarun.ui.base.BaseFragment
import java.util.*

class AddCalendarFragment : BaseFragment<FragmentAddCalendarBinding>(R.layout.fragment_add_calendar) {

    private val recyclerViewAdapter by lazy { AddCalendarRecyclerViewAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerViewAdapter()
        setTextDatePickerClickListener()
    }

    private fun setRecyclerViewAdapter() {
        binding.rAddCalendarCheckPointList.adapter = recyclerViewAdapter
    }

    private fun setTextDatePickerClickListener() {
        val textDatePickerClickListener = View.OnClickListener { view -> showDatePickerDialog(view as TextView) }

        binding.tvAddCalendarStartDatePicker.setOnClickListener(textDatePickerClickListener)
        binding.tvAddCalendarEndDatePicker.setOnClickListener(textDatePickerClickListener)
    }

    private fun showDatePickerDialog(textView: TextView) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, pickYear, pickMonth, pickDayOfMonth ->
            // TODO ViewModel에 저장하는 방식으로 개선
            textView.text = getString(R.string.ui_date_format, pickYear, pickMonth, pickDayOfMonth)
        }

        DatePickerDialog(requireContext(), dateSetListener, year, month, dayOfMonth).show()
    }
}
