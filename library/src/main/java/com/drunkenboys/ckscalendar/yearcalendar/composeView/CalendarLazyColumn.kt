package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drunkenboys.ckscalendar.FakeFactory
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarView
import java.time.LocalDate

@Composable
fun CalendarLazyColumn(
    design: CalendarDesignObject,
    onDayClickListener: OnDayClickListener?,
    onDaySecondClickListener: OnDaySecondClickListener?,
    schedules: List<CalendarScheduleObject>
) {
    // RecyclerView의 상태를 관찰
    val listState = rememberLazyListState()
    val today = CalendarDate(LocalDate.now(), DayType.PADDING, true) // 초기화를 위한 dummy

    var clickedDay by remember { mutableStateOf<CalendarDate?>(today) }
    val clickedEdge = { day: CalendarDate ->
        BorderStroke(
            width = 2.dp,
            color = if (clickedDay?.date == day.date) Color(design.selectedFrameColor) else Color.Transparent
        )
    }

    val dayColumnModifier = { day: CalendarDate ->
        Modifier
            .layoutId(day.date.toString())
            .border(clickedEdge(day))
            .clickable(onClick = {
                if (clickedDay != day) onDayClickListener?.onDayClick(day.date, 0)
                else onDaySecondClickListener?.onDayClick(day.date, 0)
                clickedDay = day
            })
    }

    // RecyclerView와 유사
    LazyColumn(state = listState) {
        yearList.forEach { year ->
            // 달력
            item {
                Text(
                    text = "${year[0].startDate.year}년",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.background)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            items(year, key = {month -> month.startDate}) { month ->
                MonthCalendar(
                    month = month,
                    listState = listState,
                    dayColumnModifier = dayColumnModifier,
                    design = design,
                    schedules = schedules
                )
            }
        }
    }

    // 뷰가 호출되면 오늘 날짜가 보이게 스크롤
    LaunchedEffect(listState) {
        listState.scrollToItem(index = YearCalendarView.LAST_YEAR - 1) // preload
        listState.scrollToItem(index = getTodayItemIndex())
    }
}

private val yearList = (YearCalendarView.INIT_YEAR..YearCalendarView.LAST_YEAR).map { year ->
    FakeFactory.createFakeCalendarSetList(year)
}

private fun getTodayItemIndex(): Int {
    val today = LocalDate.now()

    // (월 달력 12개 + 년 헤더 1개) + 이번달
    return (today.year - YearCalendarView.INIT_YEAR) * 13 + today.monthValue
}