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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            fetchHoliday()

            val pref = getSharedPreferences(APP_FIRST_RUN_PREF, Context.MODE_PRIVATE)
            pref.edit().putBoolean(IS_FIRST_RUN, false).apply()
        }
    }

    private fun setupNavHostFragment() {
        val navHost = NavHostFragment.create(R.navigation.nav_main, MainCalendarFragmentArgs(calendarId).toBundle())
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_main_container, navHost)
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    // 터치 영역에 따라 키보드를 숨기기 위해 구현
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // Activity에서 터치 이벤트가 발생할 때 현재 포커스를 가진 뷰를 저장
        if (ev.action == MotionEvent.ACTION_UP)
            prevFocus = currentFocus
        val result = super.dispatchTouchEvent(ev)
        // dispatchTouchEvent 호출 후 singleTapUp 제스처 탐지
        mDetector.onTouchEvent(ev)
        return result
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // ACTION_UP 이벤트에서 포커스를 가진 뷰가 EditText일 때 터치 영역을 확인하여 키보드를 토글
            if (e.action == MotionEvent.ACTION_UP && prevFocus is EditText) {
                val prevFocus = prevFocus ?: return false
                // 포커를 가진 EditText의 터치 영역 계산
                val hitRect = Rect()
                prevFocus.getGlobalVisibleRect(hitRect)

                // 터치 이벤트가 EditText의 터치 영역에 속할 때 키보드를 숨길지 결정
                if (!hitRect.contains(e.x.toInt(), e.y.toInt())) {
                    if (currentFocus is EditText && currentFocus != prevFocus) {
                        // 터치한 영역의 뷰가 다른 EditText일 때는 키보드를 가리지 않는다.
                        return false
                    } else {
                        // 터치한 영역이 EditText의 터치 영역 밖이면서 다른 EditText가 아닐 때 키보드 hide
                        getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(prevFocus.windowToken, 0)
                        prevFocus.clearFocus()
                    }
                }
            }
            return super.onSingleTapUp(e)
        }
    }

    private fun fetchHoliday() {
        val monthList = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        CoroutineScope(Dispatchers.IO).launch {
            // 현재 API에서 2004년부터 2023년까지의 공휴일 정보를 제공해줌
            for (year in 2004 until 2024) {
                for (month in monthList) {
                    try {
                        holidayRepository.fetchHolidayListOnMonth(
                            year.toString(),
                            month
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
                            val item = holidayRepository.fetchHolidayOnMonth(year.toString(), month).response.body.items.item
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
