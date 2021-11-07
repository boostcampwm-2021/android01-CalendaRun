package com.drunkenboys.calendarun.ui.savecalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveCalendarBinding
import com.drunkenboys.calendarun.showDatePickerDialog
import com.drunkenboys.calendarun.toStringDateFormat
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.savecalendar.adapter.SaveCalendarRecyclerViewAdapter
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveCalendarFragment : BaseFragment<FragmentSaveCalendarBinding>(R.layout.fragment_save_calendar) {

    private val saveCalendarViewModel by viewModels<SaveCalendarViewModel>()
    private val saveCalendarAdapter by lazy { SaveCalendarRecyclerViewAdapter() }

    private val args: SaveCalendarFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        setDataBinding()
        setRecyclerViewAdapter()
        setAddCheckPointViewClickListener()
        setCheckPointListObserver()
        setPickDateTimeEventObserver()
    }

    private fun initToolbar() = with(binding) {
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
        binding.btnSaveCalendarAddCheckPointView.setOnClickListener {
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

    private fun setCheckPointListObserver() {
        saveCalendarViewModel.checkPointList.observe(viewLifecycleOwner, { checkPointList ->
            val checkPointModelList = mutableListOf<CheckPointItem>()
            checkPointList.forEach { checkPoint ->
                val checkPointModel = CheckPointItem(
                    MutableLiveData(checkPoint.name),
                    MutableLiveData(toStringDateFormat(checkPoint.endDate))
                )
                checkPointModelList.add(checkPointModel)
            }
            saveCalendarAdapter.submitList(checkPointModelList)
        })
    }
}
