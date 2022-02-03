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
import androidx.compose.foundation.lazy.LazyListState
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

/**
 * 전체 달력을 나타내는 LazyColumn (RecyclerView와 유사)
 * slice가 각 viewHolder의 items가 됩니다.
 * viewHolder가 bind될 때에는 년 & 월과 달력 한 줄을 표시합니다.
 */
@Composable
fun CalendarLazyColumn(
    onDayClickListener: OnDayClickListener?,
    onDaySecondClickListener: OnDaySecondClickListener?,
    viewModel: YearCalendarViewModel
) {

    // LazyColumn의 상태를 관찰
    // item의 위치, 간략한 정보, 스크롤을 관리합니다.
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = viewModel.getInitScroll())

    val calendar: List<CalendarSet> by remember { viewModel.calendar }

    // 선택한 날짜를 기억 + 저장 (화면이 파괴돼도 기억)
    var clickedDay by rememberSaveable { mutableStateOf(LocalDate.now()) }

    // 날짜 선택 테두리 지정
    // 선택은 cell에서 하지만, 선택날짜는 LazyColumn에서 관리하기 때문에 해당 함수를 cell을 호출하는 함수의 인자로 넘겨줍니다.
    val dayColumnModifier = { day: CalendarDate ->
        when (clickedDay) {
            day.date -> Modifier.border(BorderStroke(2.dp, Color(viewModel.design.value.selectedFrameColor)), RoundedCornerShape(6.dp))
                .clickable {
                    onDaySecondClickListener?.onDayClick(day.date, 0)
                }
            else -> Modifier.clickable {
                onDayClickListener?.onDayClick(day.date, 0)
                clickedDay = day.date
            }
        }
    }

    // setSchedule 호출 시 recompose할 Scope 전달
    // schedule이 변경되어도 자동으로 recompose하지 않기 때문에 scope를 전달해 강제로 recompose 합니다.
    viewModel.setRecomposeScope(currentRecomposeScope)

    // RecyclerView와 유사
    LazyColumn(
        state = listState,
        modifier = Modifier.background(MaterialTheme.colors.background).fillMaxHeight()
    ) {
        calendar.forEach { slice ->
            // items = calendarSet (슬라이스) -> List<List<calendarDates>> (1슬라이스를 n주일, 1주일을 n일로 나타낸 2중 배열)
            items(items = calendarSetToCalendarDatesList(slice, viewModel.schedules.value)) { week ->
                // 해가 갱신될 때마다 상단에 연표시
                YearHeader(week)

                // 날짜를 가리지 않게 월 표시
                if (viewModel.isFirstWeek(week, slice))
                    AnimatedMonthHeader(listState = listState, monthName = slice.name)

                // 1주일씩 뷰를 그린다. 해당 1주일에는 날짜와 스케줄의 뷰가 포함되어 있다.
                WeekCalendar(
                    month = slice,
                    week = week,
                    dayColumnModifier = dayColumnModifier,
                    viewModel = viewModel
                )
            }
        }
    }

    // 무한 스크롤
    InfiniteScroll(listState, viewModel)
}

@Composable
private fun InfiniteScroll(listState: LazyListState, viewModel: YearCalendarViewModel) = with(listState){
    // 마지막 스크롤이라면 내년 데이터를 생성
    ShouldNextScroll {
        viewModel.fetchNextCalendarSet()
    }

    // 첫 스크롤이라면 이전년도 데이터를 생성
    ShouldPrevScroll {
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