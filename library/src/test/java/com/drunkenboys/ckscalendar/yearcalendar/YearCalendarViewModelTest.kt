package com.drunkenboys.ckscalendar.yearcalendar

import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class YearCalendarViewModelTest {

    private val viewModel = YearCalendarViewModel()
    private val today = LocalDateTime.now()
    private val fakeSchedules = (1..10).map { i ->
        CalendarScheduleObject(
            id = i,
            color = viewModel.design.value.weekDayTextColor,
            text = "$i",
            startDate = today.minusDays(i.toLong()),
            endDate = today.plusDays(i.toLong())
        )
    }

    @Test
    fun `해당_날짜_일정들_찾기_테스트`() {
        // Given 10 schedule
        // And today
        viewModel.setSchedules(fakeSchedules)

        // When get today schedules
        val findSchedules = viewModel.getDaySchedules(LocalDateTime.now())

        // Then schedule size is 10
        assertEquals(10, findSchedules.size)

        // And all schedules include today
        assertTrue(findSchedules.all { schedule -> today in schedule.startDate..schedule.endDate })
    }
}