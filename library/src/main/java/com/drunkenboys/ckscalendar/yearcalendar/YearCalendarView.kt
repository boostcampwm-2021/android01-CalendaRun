package com.drunkenboys.ckscalendar.yearcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drunkenboys.ckscalendar.databinding.LayoutYearCalendarBinding
import com.drunkenboys.ckscalendar.FakeFactory
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.TimeUtils.isSameWeek
import com.drunkenboys.ckscalendar.utils.dp
import com.drunkenboys.ckscalendar.utils.toCalendarDatesList
import java.time.DayOfWeek
import java.time.LocalDate

class YearCalendarView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutYearCalendarBinding by lazy { LayoutYearCalendarBinding.inflate(LayoutInflater.from(context), this, true) }

    private var design = CalendarDesignObject()

    private var onDateClickListener: OnDayClickListener? = null
    private var onDateSecondClickListener: OnDaySecondClickListener? = null

    private var schedules = mutableListOf<CalendarScheduleObject>()

    private val yearList = (INIT_YEAR..LAST_YEAR).map { year ->
            FakeFactory.createFakeCalendarSetList(year)
        }

    init {
        binding.composeYearCalendarViewYearCalendar.setContent {
            //
            CustomTheme(design = design) {
                // 위 -> 아래가 아닌 안 -> 밖으로 생성.
                // 요일 표시가 가장 바깥에 오지 않으면 날짜에 가려진다.
                CalendarLazyColumn()
                WeekHeader()
            }
        }
    }

    @Composable
    private fun CalendarLazyColumn() {
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
                    if (clickedDay != day) onDateClickListener
                    else onDateSecondClickListener
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
                        modifier = Modifier
                            .background(color = MaterialTheme.colors.background)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                items(year) { month ->
                    MonthCalendar(
                        month = month,
                        listState = listState,
                        dayColumnModifier = dayColumnModifier
                    )
                }
            }
        }

        // 뷰가 호출되면 오늘 날짜가 보이게 스크롤
        LaunchedEffect(listState) {
            listState.scrollToItem(index = LAST_YEAR - 1) // preload
            listState.scrollToItem(index = getTodayItemIndex())
        }
    }
    
    @Composable
    private fun WeekHeader() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            design.weekSimpleStringSet.forEach { dayId ->
                Text(
                    text = dayId,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun MonthCalendar(
        month: CalendarSet,
        listState: LazyListState,
        dayColumnModifier: (CalendarDate) -> Modifier
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
                            PaddingText(day = day)
                        }
                        // 1일
                        else -> {
                            Column(modifier = dayColumnModifier(day), horizontalAlignment = Alignment.CenterHorizontally) {
                                DayText(day = day)
                                ScheduleText(today = day.date, schedules, weekSchedules)
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

    @Composable
    private fun AnimatedMonthHeader(
        listState: LazyListState,
        month: Int
    ) {
        val density: Float by animateFloatAsState(
            targetValue = if(listState.isScrollInProgress) 1f else 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        Text(
            text = "${month}월",
            modifier = Modifier.alpha(density).padding(start = (density * 5).dp)
        )
    }

    @Composable
    private fun DayText(day: CalendarDate) {
        val color = when (day.dayType) { // FIXME: month 와 통합
            DayType.HOLIDAY -> Color(design.holidayTextColor)
            DayType.SATURDAY -> Color(design.saturdayTextColor)
            DayType.SUNDAY -> Color(design.sundayTextColor)
            else -> MaterialTheme.colors.primary
        }

        // FIXME: mapper 추가
        val align = when (design.textAlign) {
            -1 -> TextAlign.Start
            0 -> TextAlign.Center
            1 -> TextAlign.End
            else -> TextAlign.Center
        }

        Text(
            text = "${day.date.dayOfMonth}",
            color = color,
            modifier = Modifier.layoutId(day.date.toString()),
            textAlign = align,
            fontSize = design.textSize.dp()
        )
    }

    @Composable
    private fun PaddingText(day: CalendarDate) {
        Text(
            text = "${day.date.dayOfMonth}",
            modifier = Modifier
                .layoutId(day.date.toString())
                .alpha(0f),
            textAlign = TextAlign.Center,
            fontSize = design.textSize.dp()
        )
    }

    @Composable
    private fun ScheduleText(
        today: LocalDate,
        scheduleList: List<CalendarScheduleObject>,
        weekScheduleList: Array<Array<CalendarScheduleObject?>>
    ) {
        val weekNum = (today.dayOfWeek.dayValue())

        val scheduleText = { schedule: CalendarScheduleObject? ->
            when {
                schedule == null -> " "
                schedule.startDate.toLocalDate() == today || today.dayOfWeek == DayOfWeek.SUNDAY -> schedule.text
                else -> " "
            }
        }

        setWeekSchedule(getStartScheduleList(today, scheduleList), weekScheduleList, today)

        weekScheduleList[weekNum].forEach { schedule ->
            val modifier =
                if (schedule != null) Modifier
                    .fillMaxWidth()
                    .background(color = Color(schedule.color))
                else Modifier.fillMaxWidth()

            Text(
                text = scheduleText(schedule),
                maxLines = 1,
                modifier = modifier,
                overflow = TextOverflow.Ellipsis,
                fontSize = design.textSize.dp()
            )
            Spacer(modifier = Modifier.height(2.dp))
        }
    }

    private fun setWeekSchedule(
        todaySchedules: List<CalendarScheduleObject>,
        weekSchedules: Array<Array<CalendarScheduleObject?>>,
        today: LocalDate
    ) {
        val todayOfWeek = today.dayOfWeek.dayValue()

        todaySchedules.forEach { todaySchedule ->
            val weekEndDate =
                if (!today.isSameWeek(todaySchedule.endDate.toLocalDate())) DayOfWeek.SATURDAY.value
                else todaySchedule.endDate.dayOfWeek.dayValue()

            weekSchedules[todayOfWeek].forEachIndexed { index, space ->
                if (space == null) {
                    (todayOfWeek..weekEndDate).forEach { weekNum ->
                        weekSchedules[weekNum][index] = todaySchedule
                    }
                    return@forEach
                }
            }
        }
    }

    private fun getStartScheduleList(today: LocalDate, scheduleList: List<CalendarScheduleObject>) = scheduleList.filter { schedule ->
        val isStart = schedule.startDate.toLocalDate() == today
        val isSunday = today.dayOfWeek == DayOfWeek.SUNDAY
        val isFirstOfMonth = today.dayOfMonth == 1
        val isDateInScheduleRange = today in schedule.startDate.toLocalDate()..schedule.endDate.toLocalDate()
        isStart || ((isSunday || isFirstOfMonth) && (isDateInScheduleRange))
    }

    private fun getTodayItemIndex(): Int {
        val today = LocalDate.now()

        // (월 달력 12개 + 년 헤더 1개) + 이번달
        return (today.year - INIT_YEAR) * 13 + today.monthValue
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

    fun setOnDateClickListener(onDateClickListener: OnDayClickListener) {
        this.onDateClickListener = onDateClickListener
    }

    fun setOnDaySecondClickListener(onDateSecondClickListener: OnDaySecondClickListener) {
        this.onDateSecondClickListener = onDateSecondClickListener
    }

    fun setSchedule(schedule: CalendarScheduleObject) {
        schedules.add(schedule)
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        this.schedules.addAll(schedules)
    }

    fun setTheme(designObject: CalendarDesignObject) {
        design = designObject
    }

    fun resetTheme() {
        design = CalendarDesignObject()
    }

    companion object {
        const val TAG = "YEAR_CALENDAR"
        const val INIT_YEAR = 0
        const val LAST_YEAR = 10000
    }
}