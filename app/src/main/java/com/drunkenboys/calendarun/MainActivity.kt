package com.drunkenboys.calendarun

import android.os.Bundle
import com.drunkenboys.calendarun.databinding.ActivityMainBinding
import com.drunkenboys.calendarun.ui.base.BaseViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseViewActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
