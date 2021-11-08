package com.drunkenboys.ckscalendar.yearcalendar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.compiler.plugins.kotlin.ComposeFqNames.key
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.accessibility.AccessibilityViewCommand
import androidx.databinding.DataBindingUtil
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.FakeFactory
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.TimeUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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

    init {
        val yearList =  mutableListOf<List<CalendarSet>>()
        (INIT_YEAR..LAST_YEAR).forEach { year ->
            yearList.add(FakeFactory.createFakeCalendarSetList(year))
        }

        binding.composeYearCalendarViewDayOfWeek.setContent {
            YearCalendar()
        }

        binding.composeYearCalendarViewYearCalendar.setContent {
            YearCalendarRecyclerView(yearList)
        }
    }

    @Composable
    private fun YearCalendar() {
        val weekIds = listOf(
            context.getString(R.string.sunday),
            context.getString(R.string.monday),
            context.getString(R.string.tuesday),
            context.getString(R.string.wednesday),
            context.getString(R.string.thursday),
            context.getString(R.string.friday),
            context.getString(R.string.saturday)
        )

        ConstraintLayout(dayOfWeekConstraints(weekIds)) {
            weekIds.forEach { dayId ->
                DayOfWeekTextView(dayOfWeek = dayId)
            }
        }
    }

    @Composable
    private fun DayOfWeekTextView(dayOfWeek: String) {
        Text(
            text = dayOfWeek,
            modifier = Modifier.layoutId(dayOfWeek)
        )
    }

    private fun dayOfWeekConstraints(weekIds: List<String>) = ConstraintSet {
        val sunday = createRefFor(weekIds[0])
        val monday = createRefFor(weekIds[1])
        val tuesday = createRefFor(weekIds[2])
        val wednesday = createRefFor(weekIds[3])
        val thursday = createRefFor(weekIds[4])
        val friday = createRefFor(weekIds[5])
        val saturday = createRefFor(weekIds[6])

        constrain(sunday) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(monday.start)
            width = Dimension.fillToConstraints
        }

        constrain(monday) {
            top.linkTo(parent.top)
            start.linkTo(sunday.end)
            end.linkTo(tuesday.start)
            width = Dimension.fillToConstraints
        }

        constrain(tuesday) {
            top.linkTo(parent.top)
            start.linkTo(monday.end)
            end.linkTo(wednesday.start)
            width = Dimension.fillToConstraints
        }

        constrain(wednesday) {
            top.linkTo(parent.top)
            start.linkTo(tuesday.end)
            end.linkTo(thursday.start)
            width = Dimension.fillToConstraints
        }

        constrain(thursday) {
            top.linkTo(parent.top)
            start.linkTo(wednesday.end)
            end.linkTo(friday.start)
            width = Dimension.fillToConstraints
        }

        constrain(friday) {
            top.linkTo(parent.top)
            start.linkTo(thursday.end)
            end.linkTo(saturday.start)
            width = Dimension.fillToConstraints
        }

        constrain(saturday) {
            top.linkTo(parent.top)
            start.linkTo(friday.end)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
    }

    //FIXME: 네이밍 수정해야할지도
    @SuppressLint("CoroutineCreationDuringComposition")
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @Composable
    private fun YearCalendarRecyclerView(yearList: List<List<CalendarSet>>) {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            yearList.forEach { year ->
                stickyHeader {
                    Text( //FIXME: background 투명하지 않게 설정
                        text = "${year[0].startDate.year}년",
                        modifier = Modifier
                            .background(color = Color.White)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                items(
                    items = year,
                    key = { month ->
                        month.startDate
                    }) { month ->
                    // 월 + 주
                    calendarSetToCalendarDates(month).forEach { week ->
                        val weekIds = week.map { day ->
                            day.date.toString()
                        }

                        ConstraintLayout(dayOfWeekConstraints(weekIds), Modifier.fillMaxWidth()) {
                            AnimatedVisibility(visible = isFirstWeek(week, month.id)) {
                                Text(text = "${month.id}월")
                            }

                            // 일주일 나열
                            // TODO: 디자인 설정
                            week.forEach { day ->
                                if (day.dayType != DayType.PADDING)
                                    Text(
                                        text = "${day.date.dayOfMonth}",
                                        color = when (day.dayType) {
                                            DayType.HOLIDAY -> Color.Red
                                            DayType.SATURDAY -> Color.Blue
                                            DayType.SUNDAY -> Color.Red
                                            else -> Color.Black
                                        },
                                        modifier = Modifier.layoutId(day.date.toString()),
                                        textAlign = TextAlign.Center,
                                    )
                                else
                                    Text(
                                        text = "${day.date.dayOfMonth}",
                                        modifier = Modifier
                                            .layoutId(day.date.toString())
                                            .alpha(0f),
                                        textAlign = TextAlign.Center,
                                    )
                                // TODO: 스케줄 탐색

                            }
                        }
                    }
                }
            }
        }

        rememberCoroutineScope().launch {
            listState.scrollToItem(index = initItemIndex())
        }
    }

    private fun initItemIndex(): Int {
        val today = LocalDate.now()

        // 월 달력 12개 + 년 헤더 1개
        return (today.year - INIT_YEAR) * 13 + today.monthValue - 1
    }

    private fun calendarSetToCalendarDates(month: CalendarSet): List<List<CalendarDate>> {
        // n주
        val weekList = mutableListOf<MutableList<CalendarDate>>()
        var oneDay = month.startDate
        var paddingPrev = month.startDate
        var paddingNext = month.endDate

        // 앞쪽 패딩
        if (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) weekList.add(mutableListOf())
        while (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) {
            paddingPrev = paddingPrev.minusDays(1)
            weekList.last().add(CalendarDate(paddingPrev, DayType.PADDING))
        }

        // n주일 추가
        repeat(month.startDate.lengthOfMonth()) {
            // 일요일에는 1주일 갱신
            if (oneDay.dayOfWeek == DayOfWeek.SUNDAY) weekList.add(mutableListOf())

            // 1주일 추가
            weekList.last().add(CalendarDate(oneDay, TimeUtils.parseDayWeekToDayType(oneDay.dayOfWeek)))

            oneDay = oneDay.plusDays(1L)
        }

        // 뒤쪽 패딩
        while (paddingNext.dayOfWeek != DayOfWeek.SATURDAY) {
            paddingNext = paddingNext.plusDays(1)
            weekList.last().add(CalendarDate(paddingNext, DayType.PADDING))
        }

        return weekList
    }

    private fun isFirstWeek(week: List<CalendarDate>, monthId: Int) = week.any { day ->
        day.date.dayOfMonth == 1 && monthId == day.date.monthValue
    }

    companion object {
        const val INIT_YEAR = 0
        const val LAST_YEAR = 10000
    }
}