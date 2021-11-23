package com.drunkenboys.ckscalendar.weekcalendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.calendarSetToCalendarDatesList
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import com.drunkenboys.ckscalendar.yearcalendar.composeView.DayText
import com.drunkenboys.ckscalendar.yearcalendar.composeView.PaddingText
import com.drunkenboys.ckscalendar.yearcalendar.composeView.ScheduleText
import java.time.LocalDate

private val currentMonth = CalendarSet(
    id = -1,
    name = "temp",
    startDate = LocalDate.now().withDayOfMonth(1),
    endDate = LocalDate.now().withDayOfMonth(LocalDate.now().dayOfMonth)
)

private val currentWeeks = calendarSetToCalendarDatesList(currentMonth)

@Composable
fun WeekCalendar(
    viewModel: YearCalendarViewModel = YearCalendarViewModel(),
    week: List<CalendarDate> = currentWeeks.find { currentWeek -> currentWeek.any { day -> day.date == LocalDate.now()} }!!,
    dayModifier: (CalendarDate) -> Modifier = { Modifier }
) {
    viewModel.setRecomposeScope(currentRecomposeScope)

    ConstraintLayout(
        constraintSet = dayOfWeekConstraints(week.map { day -> day.date.toString() }),
        modifier = Modifier.fillMaxWidth()
    ) {
        val weekSchedules: Array<Array<CalendarScheduleObject?>> =
            Array(7) { Array(viewModel.design.value.visibleScheduleCount) { null } }

        week.forEach { day ->
            when (day.dayType) {
                // 빈 날짜
                DayType.PADDING -> PaddingText(day = day, viewModel = viewModel)

                // 1일
                else -> Column(modifier = dayModifier(day).layoutId(day.date.toString()), horizontalAlignment = Alignment.CenterHorizontally) {
                    DayText(day = day, viewModel = viewModel, false)
                    ScheduleText(today = day.date, weekSchedules, viewModel = viewModel)
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
fun PreviewWeek() {
    WeekCalendar()
}