package com.drunkenboys.ckscalendar.yearcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.FakeFactory
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import kotlinx.coroutines.launch
import java.time.LocalDate

@ExperimentalAnimationApi
@ExperimentalFoundationApi
class YearCalendarView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutYearCalendarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_year_calendar,
        this,
        true
    )

    private val controller = YearCalendarController()

    private val header by lazy { YearCalendarHeader() }

    init {
        val yearList =  mutableListOf<List<CalendarSet>>()
        (INIT_YEAR..LAST_YEAR).forEach { year ->
            yearList.add(FakeFactory.createFakeCalendarSetList(year))
        }

        binding.composeYearCalendarViewDayOfWeek.setContent {
            header.WeekHeader()
        }

        binding.composeYearCalendarViewYearCalendar.setContent {
            CalendarLazyColumn(yearList)
        }
    }

    //FIXME: 네이밍 수정해야할지도
    @SuppressLint("CoroutineCreationDuringComposition")
    @ExperimentalFoundationApi
    @Composable
    private fun CalendarLazyColumn(yearList: List<List<CalendarSet>>) {
        // RecyclerView의 상태를 관찰
        val listState = rememberLazyListState()

        // RecyclerView와 유사
        LazyColumn(state = listState) {
            yearList.forEach { year ->
                // 항상 떠있는 헤더
                stickyHeader {
                    header.YearHeader(year)
                }

                items(year) { month ->
                    controller.calendarSetToCalendarDates(month).forEach { week ->
                        val weekIds = week.map { day -> day.date.toString() }

                        ConstraintLayout(controller.dayOfWeekConstraints(weekIds), Modifier.fillMaxWidth()) {
                            if (controller.isFirstWeek(week, month.id)) Text(text = "${month.id}월")
                                week.forEach { day ->

                                // TODO: 디자인 설정
                                if (day.dayType == DayType.PADDING) {
                                    PaddingText(day = day)
                                } else {
                                    DayText(day = day)
                                }
                            }

                            // TODO: 스케줄 탐색
                        }
                    }
                }
            }
        }

        // 뷰가 호출되면 오늘 날짜가 보이게 스크롤
        rememberCoroutineScope().launch {
            listState.scrollToItem(index = LAST_YEAR - 1) // preload
            listState.scrollToItem(index = getTodayItemIndex())
        }
    }

    // FIXME: 스케줄 추가하면서 패딩 조정
    @Composable
    private fun DayText(day: CalendarDate) {
        Text(
            text = "${day.date.dayOfMonth}",
            color = when (day.dayType) {
                DayType.HOLIDAY -> Color.Red
                DayType.SATURDAY -> Color.Blue
                DayType.SUNDAY -> Color.Red
                else -> Color.Black
            },
            modifier = Modifier.layoutId(day.date.toString()).padding(top = 30.dp),
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    private fun PaddingText(day: CalendarDate) {
        Text(
            text = "${day.date.dayOfMonth}",
            modifier = Modifier.layoutId(day.date.toString()).alpha(0f),
            textAlign = TextAlign.Center,
        )
    }

    private fun getTodayItemIndex(): Int {
        val today = LocalDate.now()

        // 월 달력 12개 + 년 헤더 1개
        return (today.year - INIT_YEAR) * 13 + today.monthValue - 1
    }

    companion object {
        const val TAG = "YEAR_CALENDAR"
        const val INIT_YEAR = 0
        const val LAST_YEAR = 10000
    }
}