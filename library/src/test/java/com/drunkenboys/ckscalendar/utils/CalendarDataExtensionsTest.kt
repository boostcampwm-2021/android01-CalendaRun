package com.drunkenboys.ckscalendar.utils

import com.drunkenboys.ckscalendar.data.CalendarSet
import org.junit.Assert.*

import org.junit.Test
import java.time.LocalDate

class CalendarDataExtensionsTest {
    private val id = 0
    private val name = "test"

    private val month1DayStart = LocalDate.of(2021, 1, 1)
    private val month1Day2 = LocalDate.of(2021, 1, 2)
    private val month1DayMid = LocalDate.of(2021, 1, 15)
    private val month1DayEnd = LocalDate.of(2021, 1, 31)

    private val month2DayStart = LocalDate.of(2021, 2, 1)
    private val month2DayMid = LocalDate.of(2021, 2, 15)

    @Test
    fun `6주일이_한_달인_슬라이스를_변환하면_6줄`() {
        // Given 1.1(금) ~ 1.31(일)
        val calendarSet = CalendarSet(
            id,
            name,
            startDate = month1DayStart,
            endDate = month1DayEnd
        )

        // When 변환
        val calendarDateList = calendarSet.toCalendarDatesList()

        assertEquals(6, calendarDateList.size)
    }

    @Test
    fun `6주일이_두_달_걸친_슬라이스를_변환하면_6줄`() {
        // Given 1.15(목) ~ 2.15(월)
        val calendarSet = CalendarSet(
            id,
            name,
            startDate = month1DayMid,
            endDate = month2DayMid
        )

        // When convert
        val calendarDateList = calendarSet.toCalendarDatesList()

        // Then 6줄
        assertEquals(6, calendarDateList.size)
    }

    @Test
    fun `1주일_보다_짧은_슬라이스를_변환하면_1줄`() {
        // Given 1.1 ~ 1.2
        val calendarSet = CalendarSet(
            id,
            name,
            startDate = month1DayStart,
            endDate = month1Day2
        )

        // When convert
        val calendarDateList = calendarSet.toCalendarDatesList()

        // Then list is not empty
        assertEquals(1, calendarDateList.size)
        assertTrue(calendarDateList.isNotEmpty())
        assertTrue(calendarDateList[0].isNotEmpty())
    }
}