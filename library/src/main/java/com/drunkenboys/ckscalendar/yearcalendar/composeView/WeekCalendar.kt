package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.GravityMapper
import com.drunkenboys.ckscalendar.utils.dp
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.LocalDate

@Composable
fun WeekCalendar(
    month: CalendarSet,
    week: List<CalendarDate>,
    dayColumnModifier: (CalendarDate) -> Modifier,
    viewModel: YearCalendarViewModel
) {
    val weekDayColor = MaterialTheme.colors.primary
    val weekSchedules: Array<Array<CalendarScheduleObject?>> =
        Array(7) { Array(viewModel.design.value.visibleScheduleCount) { null } }

    // 1주일 TODO: Row로 변환
    ConstraintLayout(
        constraintSet = dayOfWeekConstraints(week.map { day -> day.date.toString() }),
        modifier = Modifier.fillMaxWidth()
    ) {

        week.forEach { day ->
            // 빈 날짜인지 구분
            when (day.dayType) {
                DayType.PADDING -> Column(
                    modifier = Modifier.layoutId(day.date.toString())
                        .border(BorderStroke(width = 0.1f.dp, color = weekDayColor.copy(alpha = 0.1f)))
                ) {
                    // 빈 날짜
                    BaseText(
                        type = DayType.PADDING,
                        modifier = Modifier.padding(5.dp),
                        design = viewModel.design.value
                    )

                    // 빈 스케줄
                    repeat(viewModel.design.value.visibleScheduleCount) {
                        BaseText(
                            type = DayType.PADDING,
                            modifier = Modifier.fillMaxWidth().background(Color.Transparent).padding(4.dp),
                            design = viewModel.design.value
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                // 날짜 표시 + 일정 표시
                // 선택될 때 함께 테두리를 지정하기 위해 Column으로 감싼다.
                // 테두리는 draw로 그림.
                else -> Column(
                    modifier = Modifier.layoutId(day.date.toString())
                        .then(dayColumnModifier(day))
                        .drawBehind {
                            drawLine(
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                color = weekDayColor.copy(alpha = 0.1f)
                            )
                            drawLine(
                                start = Offset(size.width, 0f),
                                end = Offset(size.width, size.height),
                                color = weekDayColor.copy(alpha = 0.1f)
                            )
                            drawLine(
                                start = Offset(size.width, size.height),
                                end = Offset(0f, size.height),
                                color = weekDayColor.copy(alpha = 0.1f)
                            )
                        },
                    horizontalAlignment = GravityMapper.toColumnAlign(viewModel.design.value.textAlign)
                ) {
                    DayText(
                        day = day,
                        viewModel = viewModel,
                        isFirstOfCalendarSet = month.id > 0 && (day.date.dayOfMonth == 1 || day.date == month.startDate)
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