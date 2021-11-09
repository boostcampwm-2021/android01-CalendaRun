package com.drunkenboys.calendarun

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.core.view.GestureDetectorCompat
import com.drunkenboys.calendarun.databinding.ActivityMainBinding
import com.drunkenboys.calendarun.receiver.ScheduleAlarmReceiver
import com.drunkenboys.calendarun.ui.base.BaseViewActivity
import com.drunkenboys.calendarun.util.PendingIntentExt
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseViewActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var mDetector: GestureDetectorCompat

    private var prevFocus: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetector = GestureDetectorCompat(this, SingleTapListener())
    }

    // TODO: 2021-11-09 알림 클릭 시 Fragment 내비게이션 구현

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

    companion object {

        private const val CODE_SCHEDULE_NOTIFICATION = 1000

        fun createNavigationPendingIntent(context: Context, calendarId: Long, scheduleId: Long): PendingIntent {
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(ScheduleAlarmReceiver.KEY_SCHEDULE_NOTIFICATION_CALENDAR_ID, calendarId)
                putExtra(ScheduleAlarmReceiver.KEY_SCHEDULE_NOTIFICATION_SCHEDULE_ID, scheduleId)
            }

            return PendingIntent.getActivity(
                context,
                CODE_SCHEDULE_NOTIFICATION,
                contentIntent,
                PendingIntentExt.FLAG_UPDATE_CURRENT
            )
        }
    }
}
