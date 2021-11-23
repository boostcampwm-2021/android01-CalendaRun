package com.drunkenboys.ckscalendar.yearcalendar.composeView

import android.view.Gravity
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.utils.ShouldNextScroll
import com.drunkenboys.ckscalendar.utils.ShouldPrevScroll
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
    val listState = rememberLazyListState()

    val calendar by remember { viewModel.calendar }
    val clickedDay by remember { viewModel.clickedDay }
    val clickedEdge = { day: CalendarDate ->
        BorderStroke(
            width = 2.dp,
            color = if (clickedDay == day.date) Color(viewModel.design.value.selectedFrameColor) else Color.Transparent
        )
    }

    // viewModel의 recomposer를 LazyColumn으로 설정해서 setSchedule 호출 시 recompose
    viewModel.setRecomposeScope(currentRecomposeScope)

    // state hoisting
    val dayColumnModifier = { day: CalendarDate ->
        Modifier
            .layoutId(day.date.toString())
            .border(clickedEdge(day))
            .clickable(onClick = {
                if (clickedDay != day.date) onDayClickListener?.onDayClick(day.date, 0)
                else onDaySecondClickListener?.onDayClick(day.date, 0)
                viewModel.clickDay(day)
            })
    }

    // RecyclerView와 유사
    LazyColumn(state = listState) {
        items(calendar, key = { slice -> slice.startDate }) { slice ->
            val firstOfYear = LocalDate.of(slice.endDate.year, 1, 1)

            if (firstOfYear in slice.startDate..slice.endDate)
                Text(
                    text = "${firstOfYear.year}년",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.background)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

            MonthCalendar(
                month = slice,
                listState = listState,
                dayColumnModifier = dayColumnModifier,
                viewModel = viewModel
            )
        }
    }

    // 뷰가 호출되면 오늘 날짜가 보이게 스크롤
    LaunchedEffect(listState) {
        listState.scrollToItem(index = viewModel.getDayItemIndex())
    }

    listState.ShouldNextScroll {
        viewModel.fetchNextCalendarSet()
    }

    listState.ShouldPrevScroll {
        viewModel.fetchPrevCalendarSet()
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

    CustomTheme(design = viewModel.design.value) {
        CalendarLazyColumn(
            onDayClickListener = null,
            onDaySecondClickListener = null,
            viewModel = viewModel
        )
    }
}