package com.drunkenboys.ckscalendar.yearcalendar

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.FakeFactory
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import java.time.DayOfWeek
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

    private var onDateClickListener: OnDayClickListener? = null
    private var onDateSecondClickListener: OnDaySecondClickListener? = null

    init {
        val scheduleList = FakeFactory.createFakeSchedule()
        val yearList =  mutableListOf<List<CalendarSet>>()
        (INIT_YEAR..LAST_YEAR).forEach { year ->
            yearList.add(FakeFactory.createFakeCalendarSetList(year))
        }

        binding.composeYearCalendarViewDayOfWeek.setContent {
            header.WeekHeader()
        }

        binding.composeYearCalendarViewYearCalendar.setContent {
            CalendarLazyColumn(yearList, scheduleList)
        }
    }

    @ExperimentalFoundationApi
    @Composable
    private fun CalendarLazyColumn(yearList: List<List<CalendarSet>>, scheduleList: List<CalendarScheduleObject>) {
        // RecyclerView의 상태를 관찰
        val listState = rememberLazyListState()

        val today = CalendarDate(LocalDate.now(), DayType.PADDING, true) // 초기화를 위한 dummy
        var clickedDay by remember { mutableStateOf<CalendarDate?>(today) }

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
                                    Column(modifier = Modifier
                                        .layoutId(day.date.toString())
                                        .border(
                                            BorderStroke(
                                                width = 2.dp,
                                                color = if (clickedDay?.date == day.date) colorResource(id = R.color.primary_color)
                                                else Color.Transparent))
                                        .clickable(onClick = {
                                            clickedDay = day
                                            if (clickedDay?.date != day.date) onDateClickListener
                                            else onDateSecondClickListener
                                        }),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        DayText(day = day)
                                        ScheduleText(day = day ,scheduleList)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 뷰가 호출되면 오늘 날짜가 보이게 스크롤
        LaunchedEffect(listState) {
            listState.scrollToItem(index = LAST_YEAR - 1) // preload
            listState.scrollToItem(index = getTodayItemIndex())
        }
    }

    // FIXME: 스케줄 추가하면서 패딩 조정
    @Composable
    private fun DayText(day: CalendarDate) {
        Text(
            text = "${day.date.dayOfMonth}",
            color = when (day.dayType) { // FIXME: month 와 통합
                DayType.HOLIDAY -> Color.Red
                DayType.SATURDAY -> Color.Blue
                DayType.SUNDAY -> Color.Red
                else -> Color.Black
            },
            modifier = Modifier
                .layoutId(day.date.toString()),
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    private fun PaddingText(day: CalendarDate) {
        Text(
            text = "${day.date.dayOfMonth}",
            modifier = Modifier
                .layoutId(day.date.toString())
                .alpha(0f),
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    private fun ScheduleText(day: CalendarDate, scheduleList: List<CalendarScheduleObject>) {

        // TODO: 보여지는 일정의 정렬
        val thumbnailList = scheduleList.filter { schedule ->
            day.date in schedule.startDate..schedule.endDate
        }.toMutableList()

        while (thumbnailList.size < 3) {
            thumbnailList.add(CalendarScheduleObject(
                -1, Color.Transparent.value.toInt(), "", day.date, day.date
            ))
        }

        while (thumbnailList.size > 3) {
            thumbnailList.removeLast()
        }

        thumbnailList.forEachIndexed { index, schedule ->
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(schedule.color))) {
                Text(
                    text = if (schedule.startDate == day.date || day.date.dayOfWeek == DayOfWeek.SUNDAY) schedule.text else "",
                    modifier = Modifier.padding(2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // TODO: font size
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }

    private fun getTodayItemIndex(): Int {
        val today = LocalDate.now()

        // 월 달력 12개 + 년 헤더 1개
        return (today.year - INIT_YEAR) * 13 + today.monthValue - 1
    }

    fun setOnDateClickListener(onDateClickListener: OnDayClickListener) {
        this.onDateClickListener = onDateClickListener
    }

    fun setOnDaySecondClickListener(onDateSecondClickListener: OnDaySecondClickListener) {
        this.onDateSecondClickListener = onDateSecondClickListener
    }

    companion object {
        const val TAG = "YEAR_CALENDAR"
        const val INIT_YEAR = 0
        const val LAST_YEAR = 10000
    }
}