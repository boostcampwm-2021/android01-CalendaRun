package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.TimeUtils.isSameWeek
import java.time.DayOfWeek
import java.time.LocalDate

class YearCalendarViewModel: ViewModel() {

    private val _schedules = mutableStateOf<List<CalendarScheduleObject>>(emptyList())
    val schedules: State<List<CalendarScheduleObject>> = _schedules

    private val _design = mutableStateOf(CalendarDesignObject())
    val design: State<CalendarDesignObject> = _design

    private var initYear = LocalDate.now().year
    private var currentYear = initYear

    private var _calendar = mutableStateOf(createCalendarSets(currentYear))
    val calendar: State<List<CalendarSet>> = _calendar

    private val _clickedDay = mutableStateOf<CalendarDate?>(CalendarDate(LocalDate.now(), DayType.WEEKDAY))
    val clickedDay: State<CalendarDate?> = _clickedDay

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

    fun resetDesign() {
        _design.value = CalendarDesignObject()
    }

    fun getDayItemIndex(day: LocalDate = LocalDate.now()): Int {
        // (월 달력 12개 + 년 헤더 1개) + 이번달
        return day.monthValue + 13
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
        // TODO: 정렬
        val isStart = schedule.startDate.toLocalDate() == today
        val isSunday = today.dayOfWeek == DayOfWeek.SUNDAY
        val isFirstOfMonth = today.dayOfMonth == 1
        val isDateInScheduleRange = today in schedule.startDate.toLocalDate()..schedule.endDate.toLocalDate()
        isStart || ((isSunday || isFirstOfMonth) && (isDateInScheduleRange))
    }

    fun isFirstWeek(week: List<CalendarDate>, monthId: Int) = week.any { day ->
        day.date.dayOfMonth == 1 && monthId == day.date.monthValue
    }

    fun clickDay(day: CalendarDate) {
        _clickedDay.value = day
    }

    fun getDaySchedules(day: LocalDate) = schedules.value.filter { schedule ->
        day in schedule.startDate.toLocalDate()..schedule.endDate.toLocalDate()
    }

    fun fetchNextCalendarSet() {
        _calendar.value = _calendar.value.plus(createCalendarSets(++currentYear))
        recomposeScope?.invalidate()
    }

    fun fetchPrevCalendarSet() {
        _calendar.value = createCalendarSets(--initYear).plus(_calendar.value)
        recomposeScope?.invalidate()
    }

    private fun createCalendarSets(year: Int): List<CalendarSet> {
        val calendarMonth = mutableListOf<CalendarSet>()
        var startOfMonth: LocalDate
        var endOfMonth: LocalDate

        (1..12).forEach { month ->
            startOfMonth = LocalDate.of(year, month, 1)
            endOfMonth = startOfMonth.plusDays(startOfMonth.lengthOfMonth() - 1L)
            calendarMonth.add(CalendarSet(month, "${month}월", startOfMonth, endOfMonth))
        }

        return calendarMonth
    }
}