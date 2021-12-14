package com.drunkenboys.ckscalendar.yearcalendar.composeView

import android.view.Gravity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.utils.*
import com.drunkenboys.ckscalendar.yearcalendar.CustomTheme
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.LocalDate

@Composable
fun CalendarLazyColumn(
    onDayClickListener: OnDayClickListener?,
    onDaySecondClickListener: OnDaySecondClickListener?,
    viewModel: YearCalendarViewModel
) {
    // RecyclerView의 상태를 관찰
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = viewModel.getInitScroll())
    val calendar by remember { viewModel.calendar }
    var clickedDay by rememberSaveable { mutableStateOf(LocalDate.now()) }

    // state hoisting
    val dayColumnModifier = { day: CalendarDate ->
        when (clickedDay) {
            day.date -> Modifier
                .border(BorderStroke(2.dp, Color(viewModel.design.value.selectedFrameColor)), RoundedCornerShape(6.dp))
                .clickable {
                    onDaySecondClickListener?.onDayClick(day.date, 0)
                }
            else -> Modifier
                .clickable {
                    onDayClickListener?.onDayClick(day.date, 0)
                    clickedDay = day.date
                }
        }
    }

    // setSchedule 호출 시 recompose할 Scope 전달
    viewModel.setRecomposeScope(currentRecomposeScope)

    // RecyclerView와 유사
    LazyColumn(
        state = listState,
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
    ) {
        calendar.forEach { slice ->
            items(items = calendarSetToCalendarDatesList(slice, viewModel.schedules.value)) { week ->
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

                // 월 표시
                if (viewModel.isFirstWeek(week, slice))
                    AnimatedMonthHeader(listState = listState, monthName = slice.name)

                WeekCalendar(
                    month = slice,
                    week = week,
                    dayColumnModifier = dayColumnModifier,
                    viewModel = viewModel
                )
            }
        }
    }

    with(listState) {
        ShouldNextScroll {
            viewModel.fetchNextCalendarSet()
        }

        ShouldPrevScroll {
            viewModel.fetchPrevCalendarSet()
        }
    }
}

@Preview
@Composable
fun PreviewCalendar() {
    val viewModel = YearCalendarViewModel()

    // design 커스텀 테스트
    viewModel.setDesign(CalendarDesignObject(
        textAlign = Gravity.CENTER
    ))

    // slice test
    val newSlice = listOf(CalendarSet(
        id = 0,
        name = "슬라이스",
        startDate = LocalDate.now().minusDays(30L),
        endDate = LocalDate.now().plusDays(30L)
    ))

//    viewModel.setCalendarSetList(newSlice)

    CustomTheme(design = viewModel.design.value) {
        CalendarLazyColumn(
            onDayClickListener = null,
            onDaySecondClickListener = null,
            viewModel = viewModel
        )
    }
}