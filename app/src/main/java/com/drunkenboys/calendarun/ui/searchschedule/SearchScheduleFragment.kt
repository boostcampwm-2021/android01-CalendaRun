package com.drunkenboys.calendarun.ui.searchschedule

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSearchScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchScheduleFragment : BaseFragment<FragmentSearchScheduleBinding>(R.layout.fragment_search_schedule) {

    private val searchScheduleViewModel: SearchScheduleViewModel by viewModels()

    private val searchScheduleAdapter = SearchScheduleAdapter()

    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        initRvSearchSchedule()
        observeListItem()

        searchScheduleViewModel.fetchScheduleList()
    }

    private fun setupToolbar() {
        binding.toolbarSearchSchedule.setupWithNavController(navController)
        showIMM()
    }

    private fun showIMM() {
        binding.etSearchScheduleToolbar.requestFocus()
        val imm = requireContext().getSystemService<InputMethodManager>()
        imm?.showSoftInput(binding.etSearchScheduleToolbar, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun initRvSearchSchedule() {
        binding.rvSearchSchedule.adapter = searchScheduleAdapter
        val itemDecoration = HorizontalInsetDividerDecoration(
            context = requireContext(),
            orientation = RecyclerView.VERTICAL,
            leftInset = 16f,
            rightInset = 16f
        )
        binding.rvSearchSchedule.addItemDecoration(itemDecoration)
    }

    private fun observeListItem() {
        searchScheduleViewModel.listItem.observe(viewLifecycleOwner) { listItem ->
            searchScheduleAdapter.submitList(listItem)
        }
    }
}
