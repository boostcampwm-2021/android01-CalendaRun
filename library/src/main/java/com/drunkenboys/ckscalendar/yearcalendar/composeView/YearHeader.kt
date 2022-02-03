package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.DayType
import java.time.LocalDate

@Composable
fun YearHeader(week: List<CalendarDate>) {
    val startDate = week.first { day -> day.dayType != DayType.PADDING }.date
    val endDate = week.last { day -> day.dayType != DayType.PADDING }.date
    val firstOfYear = LocalDate.of(endDate.year, 1, 1)

    // 해가 갱신될 때마다 상단에 연표시
    if (firstOfYear in startDate..endDate)
        Text(
            text = "${startDate.year}년",
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            textAlign = TextAlign.Center
        )
}