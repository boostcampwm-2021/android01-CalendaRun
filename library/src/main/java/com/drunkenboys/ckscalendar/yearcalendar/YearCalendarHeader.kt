package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.data.CalendarSet

class YearCalendarHeader {

    private val controller = YearCalendarController()

    @Composable
    fun WeekHeader() {
        val weekIds = listOf(
            stringResource(R.string.sunday),
            stringResource(R.string.monday),
            stringResource(R.string.tuesday),
            stringResource(R.string.wednesday),
            stringResource(R.string.thursday),
            stringResource(R.string.friday),
            stringResource(R.string.saturday)
        )

        ConstraintLayout(controller.dayOfWeekConstraints(weekIds)) {
            weekIds.forEach { dayId ->
                DayOfWeekTextView(dayOfWeek = dayId)
            }
        }
    }

    @Composable
    private fun DayOfWeekTextView(dayOfWeek: String) {
        Text(
            text = dayOfWeek,
            modifier = Modifier.layoutId(dayOfWeek),
            textAlign = TextAlign.Center
        )
    }

    @Composable
    fun YearHeader(year: List<CalendarSet>) {
        Text( //FIXME: background 투명하지 않게 설정
            text = "${year[0].startDate.year}년",
            modifier = Modifier.background(color = Color.White).fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
