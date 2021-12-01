package com.drunkenboys.calendarun.ui.appinfo

import android.os.Bundle
import android.view.View
import androidx.navigation.ui.setupWithNavController
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentAppInfoBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment

class AppInfoFragment : BaseFragment<FragmentAppInfoBinding>(R.layout.fragment_app_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbarAppInfo.setupWithNavController(navController)
    }
}
