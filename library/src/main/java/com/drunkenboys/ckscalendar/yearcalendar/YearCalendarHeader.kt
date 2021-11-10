package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarSet

class YearCalendarHeader(private val design: CalendarDesignObject) {

    private val controller = YearCalendarController()

    @Composable
    fun WeekHeader() {
        val weekIds = design.weekSimpleStringSet

        ConstraintLayout(
            constraintSet = controller.dayOfWeekConstraints(weekIds),
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) {
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
        Text(
            text = "${year[0].startDate.year}년",
            modifier = Modifier
                .background(color = Color(design.backgroundColor))
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
