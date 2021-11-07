package com.drunkenboys.calendarun

import android.os.Bundle
import com.drunkenboys.calendarun.databinding.ActivityMainBinding
import com.drunkenboys.calendarun.ui.base.BaseViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseViewActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    /*private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.fcv_main_container) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)*/
    }
}
