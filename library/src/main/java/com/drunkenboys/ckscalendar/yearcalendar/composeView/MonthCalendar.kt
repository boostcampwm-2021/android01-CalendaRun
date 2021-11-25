package com.drunkenboys.ckscalendar.yearcalendar.composeView

import android.view.Gravity
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.GravityMapper
import com.drunkenboys.ckscalendar.utils.calendarSetToCalendarDatesList
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.LocalDate

@Composable
fun MonthCalendar(
    month: CalendarSet,
    listState: LazyListState,
    dayColumnModifier: (CalendarDate) -> Modifier,
    viewModel: YearCalendarViewModel
) {
    val weeks = calendarSetToCalendarDatesList(month)
    var weekSchedules: Array<Array<CalendarScheduleObject?>> // 1주 스케줄
    val isFirstOfCalendarSet = { day: CalendarDate ->
        month.id > 0 && (day.date.dayOfMonth == 1 || day.date == month.startDate)
    }

    weeks.forEach { week ->
        // 1주일
        // 연 표시
        ConstraintLayout(
            constraintSet = dayOfWeekConstraints(week.map { day -> day.date.toString() }),
            modifier = Modifier.fillMaxWidth()
        ) {
            weekSchedules = Array(7) { Array(viewModel.design.value.visibleScheduleCount) { null } }
            // 월 표시
            if (viewModel.isFirstWeek(week, month))
                AnimatedMonthHeader(listState = listState, monthName = month.name)

            week.forEach { day ->
                when (day.dayType) {
                    // 빈 날짜
                    DayType.PADDING -> Column(
                        modifier = Modifier.layoutId(day.date.toString())
                            .border(border = BorderStroke(
                                width = 0.1f.dp,
                                color = Color(viewModel.design.value.weekDayTextColor).copy(alpha = 0.1f)
                            )
                        )
                    ) {
                        repeat(viewModel.design.value.visibleScheduleCount + 1) {
                            Text(text = " ")
                        }
                    }

                    // 1일
                    else -> Column(
                        modifier = dayColumnModifier(day),
                        horizontalAlignment = GravityMapper.toColumnAlign(viewModel.design.value.textAlign)
                    ) {
                        DayText(
                            day = day,
                            viewModel = viewModel,
                            isFirstOfCalendarSet = isFirstOfCalendarSet(day)
                        )

                        ScheduleText(
                            today = day.date,
                            weekScheduleList = weekSchedules,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

private fun dayOfWeekConstraints(weekIds: List<String>) = ConstraintSet {
    val week = weekIds.map { id ->
        createRefFor(id)
    }

    week.forEachIndexed { i, ref ->
        constrain(ref) {
            width = Dimension.fillToConstraints
            top.linkTo(parent.top)

            if (i != 0) start.linkTo(week[i - 1].end)
            else start.linkTo(parent.start)

            if (i != week.size - 1) end.linkTo(week[i + 1].start)
            else end.linkTo(parent.end)
        }
    }
}

@Preview
@Composable
fun PreviewMonth() {
    val month = CalendarSet(
        id = 0,
        name = "",
        startDate = LocalDate.now().withDayOfMonth(1),
        endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
    )
    val dayColumnModifier = { day: CalendarDate ->
        Modifier.layoutId(day.date.toString())
    }

    val viewModel = YearCalendarViewModel()
    viewModel.setDesign(CalendarDesignObject(
        textAlign = Gravity.END
    ))

    MonthCalendar(
        month = month,
        listState = rememberLazyListState(),
        dayColumnModifier = dayColumnModifier,
        viewModel = viewModel
    )
}