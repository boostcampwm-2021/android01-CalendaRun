package com.drunkenboys.calendarun

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.NavHostFragment
import com.drunkenboys.calendarun.data.holiday.entity.Holiday
import com.drunkenboys.calendarun.data.holiday.repository.HolidayRepository
import com.drunkenboys.calendarun.databinding.ActivityMainBinding
import com.drunkenboys.calendarun.ui.base.BaseViewActivity
import com.drunkenboys.calendarun.ui.maincalendar.MainCalendarFragmentArgs
import com.drunkenboys.calendarun.ui.maincalendar.MainCalendarFragmentDirections
import com.drunkenboys.calendarun.util.extensions.getNetworkConnectState
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseViewActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    @Inject
    lateinit var holidayRepository: HolidayRepository

    private lateinit var mDetector: GestureDetectorCompat

    private var prevFocus: View? = null

    private val calendarId by lazy {
        val pref = getSharedPreferences(CALENDAR_ID_PREF, Context.MODE_PRIVATE)
        pref.getLong(KEY_CALENDAR_ID, 1)
    }

    private val isFirstRun by lazy {
        val pref = getSharedPreferences(APP_FIRST_RUN_PREF, Context.MODE_PRIVATE)
        pref.getBoolean(IS_FIRST_RUN, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetector = GestureDetectorCompat(this, SingleTapListener())

        if (savedInstanceState == null) {
            setupNavHostFragment()
        }

        if (isFirstRun && getNetworkConnectState()) {
            val pref = getSharedPreferences(APP_FIRST_RUN_PREF, Context.MODE_PRIVATE)
            pref.edit().putBoolean(IS_FIRST_RUN, false).apply()

            fetchHoliday()
        }
    }

    private fun setupNavHostFragment() {
        val navHost = NavHostFragment.create(R.navigation.nav_main, MainCalendarFragmentArgs(calendarId).toBundle())
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_main_container, navHost)
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    // ?????? ????????? ?????? ???????????? ????????? ?????? ??????
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // Activity?????? ?????? ???????????? ????????? ??? ?????? ???????????? ?????? ?????? ??????
        if (ev.action == MotionEvent.ACTION_UP)
            prevFocus = currentFocus
        val result = super.dispatchTouchEvent(ev)
        // dispatchTouchEvent ?????? ??? singleTapUp ????????? ??????
        mDetector.onTouchEvent(ev)
        return result
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // ACTION_UP ??????????????? ???????????? ?????? ?????? EditText??? ??? ?????? ????????? ???????????? ???????????? ??????
            if (e.action == MotionEvent.ACTION_UP && prevFocus is EditText) {
                val prevFocus = prevFocus ?: return false
                // ????????? ?????? EditText??? ?????? ?????? ??????
                val hitRect = Rect()
                prevFocus.getGlobalVisibleRect(hitRect)

                // ?????? ???????????? EditText??? ?????? ????????? ?????? ??? ???????????? ????????? ??????
                if (!hitRect.contains(e.x.toInt(), e.y.toInt())) {
                    if (currentFocus is EditText && currentFocus != prevFocus) {
                        // ????????? ????????? ?????? ?????? EditText??? ?????? ???????????? ????????? ?????????.
                        return false
                    } else {
                        // ????????? ????????? EditText??? ?????? ?????? ???????????? ?????? EditText??? ?????? ??? ????????? hide
                        getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(prevFocus.windowToken, 0)
                        prevFocus.clearFocus()
                    }
                }
            }
            return super.onSingleTapUp(e)
        }
    }

    private fun fetchHoliday() {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        lifecycleScope.launch {
            // ?????? API?????? 2004????????? 2023???????????? ????????? ????????? ????????????
            for (year in 2004 until 2024) {
                for (pageNo in 1..3) {
                    launch {
                        try {
                            holidayRepository.fetchHolidayListOnYear(
                                year.toString(),
                                pageNo
                            ).response.body.items.item.forEach { item ->
                                holidayRepository.insertHoliday(
                                    Holiday(
                                        id = 0L,
                                        name = item.dateName,
                                        date = LocalDate.parse(item.localDate.toString(), formatter)
                                    )
                                )
                            }
                        } catch (e: JsonSyntaxException) {
                            try {
                                val item = holidayRepository.fetchHolidayOnYear(year.toString(), pageNo).response.body.items.item
                                holidayRepository.insertHoliday(
                                    Holiday(
                                        id = 0L,
                                        name = item.dateName,
                                        date = LocalDate.parse(item.localDate.toString(), formatter)
                                    )
                                )
                            } catch (e: JsonSyntaxException) {
                            }
                        }
                    }
                }
            }
        }

    }

    companion object {

        const val CALENDAR_ID_PREF = "calendar_id_pref"
        private const val APP_FIRST_RUN_PREF = "app_first_run_pref"
        private const val IS_FIRST_RUN = "is_first_run"

        fun createNavigationPendingIntent(context: Context, calendarId: Long, startDate: String) = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_main)
            .setDestination(R.id.dayScheduleDialog)
            .setArguments(MainCalendarFragmentDirections.toDayScheduleDialog(calendarId, startDate).arguments)
            .createPendingIntent()
    }
}
