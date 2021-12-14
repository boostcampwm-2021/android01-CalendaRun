package com.drunkenboys.ckscalendar.yearcalendar

import androidx.compose.runtime.*
import com.drunkenboys.ckscalendar.data.*
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.TimeUtils.isSameWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class YearCalendarViewModel {

    private val _schedules = mutableStateOf<List<CalendarScheduleObject>>(emptyList())
    val schedules: State<List<CalendarScheduleObject>> = _schedules

    private val _design = mutableStateOf(CalendarDesignObject())
    val design: State<CalendarDesignObject> = _design

    private var _calendar = mutableStateOf(createCalendarSets(LocalDate.now().year))
    val calendar: State<List<CalendarSet>> = _calendar

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
                if (space == todaySchedule) {
                    return@forEach
                }

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

    private fun isDefaultCalendar() = _calendar.value.all { month -> month.id < 0}

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
    }

    fun getInitScroll(): Int {
        // 기본 캘린더일 때 몇 주차에 오늘 날짜가 있는지 반환.
        // 몇 주차는 LazyColumn에서 items의 인덱스를 뜻함.

        return (getStartDayToToday() / DAY_OF_WEEK / TIME_MILLIS_OF_DAY).toInt() + getPaddingWeeks()
    }

    private fun getStartDayToToday(): Long {
        val startDay = Calendar.getInstance().apply {
            set(Calendar.YEAR, LocalDate.now().year - 1)
            set(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis

        val today = Calendar.getInstance().apply {
            set(Calendar.YEAR, LocalDate.now().year)
            set(Calendar.MONTH, LocalDate.now().monthValue)
            set(Calendar.DAY_OF_MONTH, LocalDate.now().dayOfMonth)
        }.timeInMillis

        return today - startDay
    }

    private fun getPaddingWeeks(): Int {
        var startDay = LocalDate.of(LocalDate.now().year - 1, 1, 1)
        val today = LocalDate.now()
        var result = 0

        while (startDay < today) {
            if (startDay.dayOfWeek != DayOfWeek.SUNDAY) result += 1
            startDay = startDay.plusMonths(1L)
        }

        return result
    }


    companion object {
        private const val DAY_OF_WEEK = 7

        private const val TIME_MILLIS_OF_DAY = 24 * 60 * 60 * 1000
    }
}