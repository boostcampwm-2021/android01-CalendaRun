package com.drunkenboys.ckscalendar.yearcalendar

import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.TimeUtils
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period

class YearCalendarController {

    fun dayOfWeekConstraints(weekIds: List<String>) = ConstraintSet {
        val sunday = createRefFor(weekIds[0])
        val monday = createRefFor(weekIds[1])
        val tuesday = createRefFor(weekIds[2])
        val wednesday = createRefFor(weekIds[3])
        val thursday = createRefFor(weekIds[4])
        val friday = createRefFor(weekIds[5])
        val saturday = createRefFor(weekIds[6])

        constrain(sunday) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(monday.start)
            width = Dimension.fillToConstraints
        }

        constrain(monday) {
            top.linkTo(parent.top)
            start.linkTo(sunday.end)
            end.linkTo(tuesday.start)
            width = Dimension.fillToConstraints
        }

        constrain(tuesday) {
            top.linkTo(parent.top)
            start.linkTo(monday.end)
            end.linkTo(wednesday.start)
            width = Dimension.fillToConstraints
        }

        constrain(wednesday) {
            top.linkTo(parent.top)
            start.linkTo(tuesday.end)
            end.linkTo(thursday.start)
            width = Dimension.fillToConstraints
        }

        constrain(thursday) {
            top.linkTo(parent.top)
            start.linkTo(wednesday.end)
            end.linkTo(friday.start)
            width = Dimension.fillToConstraints
        }

        constrain(friday) {
            top.linkTo(parent.top)
            start.linkTo(thursday.end)
            end.linkTo(saturday.start)
            width = Dimension.fillToConstraints
        }

        constrain(saturday) {
            top.linkTo(parent.top)
            start.linkTo(friday.end)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
    }

    fun calendarSetToCalendarDates(month: CalendarSet): List<List<CalendarDate>> {
        // n주
        val weekList = mutableListOf<MutableList<CalendarDate>>()
        var oneDay = month.startDate
        var paddingPrev = month.startDate
        var paddingNext = month.endDate

        // 앞쪽 패딩
        if (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) weekList.add(mutableListOf())
        while (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) {
            paddingPrev = paddingPrev.minusDays(1)
            weekList.last().add(CalendarDate(paddingPrev, DayType.PADDING))
        }

        // n주일 추가
        repeat(month.startDate.lengthOfMonth()) {
            // 일요일에는 1주일 갱신
            if (oneDay.dayOfWeek == DayOfWeek.SUNDAY) weekList.add(mutableListOf())

            // 1주일 추가
            weekList.last().add(CalendarDate(oneDay, TimeUtils.parseDayWeekToDayType(oneDay.dayOfWeek)))

            oneDay = oneDay.plusDays(1L)
        }

        // 뒤쪽 패딩
        while (paddingNext.dayOfWeek != DayOfWeek.SATURDAY) {
            paddingNext = paddingNext.plusDays(1)
            weekList.last().add(CalendarDate(paddingNext, DayType.PADDING))
        }

        return weekList
    }

    fun isFirstWeek(week: List<CalendarDate>, monthId: Int) = week.any { day ->
        day.date.dayOfMonth == 1 && monthId == day.date.monthValue
    }

    fun getStartScheduleList(today: LocalDate, scheduleList: List<CalendarScheduleObject>) = scheduleList.filter { schedule ->
        val isStart = schedule.startDate == today
        val isSunday = today.dayOfWeek == DayOfWeek.SUNDAY
        val isFirstOfMonth = today.dayOfMonth == 1
        val isDateInScheduleRange = today in schedule.startDate..schedule.endDate
        isStart || ((isSunday || isFirstOfMonth) && (isDateInScheduleRange))
    }

    fun setWeekSchedule(
        todayScheduleList: List<CalendarScheduleObject>,
        weekScheduleList: Array<Array<CalendarScheduleObject?>>,
        today: LocalDate
    ) {
        val todayOfWeek = today.dayOfWeek.dayValue()

        todayScheduleList.forEach { todaySchedule ->
            weekScheduleList[todayOfWeek].forEachIndexed { index, schedule ->
                if (schedule == null) {
                    (todayOfWeek..getEndDateOfWeek(today, todaySchedule.endDate)).forEach { dayOfWeek ->
                        weekScheduleList[dayOfWeek][index] = todaySchedule
                    }
                    return@forEach
                }
            }
        }
    }

    fun getEndDateOfWeek(today: LocalDate, scheduleEndDate: LocalDate): Int =
        if (!isSameWeek(today, scheduleEndDate)) DayOfWeek.SATURDAY.value
        else scheduleEndDate.dayOfWeek.dayValue()

    fun isSameWeek(startDate: LocalDate, endDate: LocalDate): Boolean {
        val weekDuration = (endDate.dayOfWeek.dayValue()) - (startDate.dayOfWeek.dayValue())
        val dayDuration = Period.between(startDate, endDate).days
        return  weekDuration == dayDuration
    }

    companion object {
        const val WEEK_COUNT = 7
    }
}