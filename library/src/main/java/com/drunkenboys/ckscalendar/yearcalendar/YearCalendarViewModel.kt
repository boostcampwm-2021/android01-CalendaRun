package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.TimeUtils.isSameWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class YearCalendarViewModel: ViewModel() {

    private val _schedules = mutableStateOf<List<CalendarScheduleObject>>(emptyList())
    val schedules: State<List<CalendarScheduleObject>> = _schedules

    private val _design = mutableStateOf(CalendarDesignObject())
    val design: State<CalendarDesignObject> = _design

    private var _calendar = mutableStateOf(createCalendarSets(LocalDate.now().year))
    val calendar: State<List<CalendarSet>> = _calendar

    private val _clickedDay = mutableStateOf<LocalDate?>(LocalDate.now())
    val clickedDay: State<LocalDate?> = _clickedDay

    private val _scrollPosition = mutableStateOf(0)
    val scrollPosition: State<Int> = _scrollPosition

    private var recomposeScope: RecomposeScope? = null

    fun setRecomposeScope(recompose: RecomposeScope) {
        this.recomposeScope = recompose
    }

    fun setSchedule(schedule: CalendarScheduleObject) {
        this._schedules.value = this.schedules.value.plus(schedule)
        recomposeScope?.invalidate()
    }

    fun setSchedules(schedules: List<CalendarScheduleObject>) {
        this._schedules.value = schedules
        recomposeScope?.invalidate()
    }

    fun setDesign(design: CalendarDesignObject) {
        _design.value = design
    }

    fun setDefaultDesign() {
        _design.value = CalendarDesignObject.getDefaultDesign()
    }

    // FIXME: 체크포인트 속에서 오늘의 날짜 찾기
    fun getDayScrollPosition(day: LocalDate = LocalDate.now()): Int {
        return day.monthValue
    }

    fun setWeekSchedules(
        weekSchedules: Array<Array<CalendarScheduleObject?>>,
        today: LocalDate
    ) {
        val todayOfWeek = today.dayOfWeek.dayValue()

        getStartSchedules(today).forEach { todaySchedule ->
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

    private fun getStartSchedules(today: LocalDate) = schedules.value.filter { schedule ->
        val isStart = schedule.startDate.toLocalDate() == today
        val isSunday = today.dayOfWeek == DayOfWeek.SUNDAY
        val isFirstOfMonth = today.dayOfMonth == 1
        val isDateInScheduleRange = today in schedule.startDate.toLocalDate()..schedule.endDate.toLocalDate()
        isStart || ((isSunday || isFirstOfMonth) && (isDateInScheduleRange))
    }.sortedBy { schedule ->
        schedule.startDate
    }

    fun isFirstWeek(week: List<CalendarDate>, month: CalendarSet) = week.any { day ->
        day.date == month.startDate && day.dayType != DayType.PADDING
    }

    fun clickDay(day: CalendarDate) {
        _clickedDay.value = day.date
    }

    fun getDaySchedules(day: LocalDateTime) = schedules.value.filter { schedule ->
        day in schedule.startDate..schedule.endDate
    }

    fun fetchNextCalendarSet() {
        if (isDefaultCalendar()) {
            val nextYear = _calendar.value.last().endDate.year + 1

            _calendar.value = _calendar.value.plus(createCalendarSets(nextYear))
        }
    }

    fun fetchPrevCalendarSet() {
        if (isDefaultCalendar()) {
            val prevYear = _calendar.value.first().startDate.year - 1

            _calendar.value = createCalendarSets(prevYear).plus(_calendar.value)
        }
    }

    fun isDefaultCalendar() = _calendar.value.all { month -> month.id < 0}

    private fun createCalendarSets(year: Int): List<CalendarSet> {
        val calendarMonth = mutableListOf<CalendarSet>()
        var startOfMonth: LocalDate
        var endOfMonth: LocalDate

        (1..12).forEach { month ->
            startOfMonth = LocalDate.of(year, month, 1)
            endOfMonth = startOfMonth.plusDays(startOfMonth.lengthOfMonth() - 1L)
            calendarMonth.add(CalendarSet(-month, "${month}월", startOfMonth, endOfMonth))
        }

        return calendarMonth
    }

    fun setCalendarSetList(calendarSetList: List<CalendarSet>) {
        if (calendarSetList.isEmpty()) setupDefaultCalendarSet()
        else _calendar.value = calendarSetList
    }

    fun setupDefaultCalendarSet() {
        // 앞 뒤로 여유 1년씩 추가
        fetchPrevCalendarSet()
        _calendar.value = createCalendarSets(LocalDate.now().year)
        fetchNextCalendarSet()

        // 오늘 선택
        _clickedDay.value = LocalDate.now()
    }
}