package com.drunkenboys.ckscalendar.yearcalendar.composeView

import PaddingText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.toCalendarDatesList

@Composable
fun MonthCalendar(
    month: CalendarSet,
    listState: LazyListState,
    dayColumnModifier: (CalendarDate) -> Modifier,
    design: CalendarDesignObject,
    schedules: List<CalendarScheduleObject>
) {
    val weeks = month.toCalendarDatesList()
    var weekSchedules: Array<Array<CalendarScheduleObject?>> // 1주 스케줄

    weeks.forEach { week ->
        // 1주일
        // 연 표시
        ConstraintLayout(
            constraintSet = dayOfWeekConstraints(week.map { day -> day.date.toString() }),
            modifier = Modifier.fillMaxWidth()
        ) {
            weekSchedules = Array(7) { Array(design.visibleScheduleCount) { null } }
            // 월 표시
            if (isFirstWeek(week, month.id)) {
                AnimatedMonthHeader(
                    listState = listState,
                    month = month.id
                )
            }
            week.forEach { day ->
                when (day.dayType) {
                    // 빈 날짜
                    DayType.PADDING -> {
                        PaddingText(day = day, design = design)
                    }
                    // 1일
                    else -> {
                        Column(modifier = dayColumnModifier(day), horizontalAlignment = Alignment.CenterHorizontally) {
                            DayText(day = day, design = design)
                            ScheduleText(today = day.date, schedules, weekSchedules, design = design)
                        }
                    }
                }
            }
        }
    }
}

private fun isFirstWeek(week: List<CalendarDate>, monthId: Int) = week.any { day ->
    day.date.dayOfMonth == 1 && monthId == day.date.monthValue
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
