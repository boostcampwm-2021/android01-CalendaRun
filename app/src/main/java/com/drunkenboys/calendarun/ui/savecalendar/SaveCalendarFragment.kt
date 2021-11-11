package com.drunkenboys.calendarun.ui.savecalendar

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSaveCalendarBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import com.drunkenboys.calendarun.util.extensions.sharedCollect
import com.drunkenboys.calendarun.util.extensions.stateCollect
import com.drunkenboys.calendarun.util.showDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveCalendarFragment : BaseFragment<FragmentSaveCalendarBinding>(R.layout.fragment_save_calendar) {

    private val saveCalendarViewModel by viewModels<SaveCalendarViewModel>()
    private val saveCalendarAdapter by lazy { SaveCalendarRecyclerViewAdapter(viewLifecycleOwner) }

    private val args: SaveCalendarFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setDataBinding()
        setRecyclerViewAdapter()
        setCheckPointItemListObserver()
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

    private fun setCheckPointItemListObserver() {
        stateCollect(saveCalendarViewModel.checkPointItemList) { list ->
            saveCalendarAdapter.submitList(list)
        }
    }

    private fun setPickDateTimeEventObserver() {
        sharedCollect(saveCalendarViewModel.pickStartDateEvent) {
            showDatePickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                saveCalendarViewModel.setCalendarStartDate(getString(R.string.ui_date_format, year, month + 1, dayOfMonth))
            }
        }
        sharedCollect(saveCalendarViewModel.pickEndDateEvent) {
            showDatePickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                saveCalendarViewModel.setCalendarEndDate(getString(R.string.ui_date_format, year, month + 1, dayOfMonth))
            }
        }
    }

    private fun setSaveCalendarEventObserver() {
        sharedCollect(saveCalendarViewModel.saveCalendarEvent) { isSaved ->
            if (isSaved) {
                navController.navigateUp()
            } else {
                Toast.makeText(context, "입력 값이 이상해요", Toast.LENGTH_LONG).show()
            }
        }
    }
}
