package com.drunkenboys.ckscalendar.yearcalendar

import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method
import java.time.LocalDate

class YearCalendarControllerTest {

    private val controller = YearCalendarController()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {

    }

    @Test
    fun `저번주_목요일과_이번주_화요일이_다른주인지_테스트`() {
        // Given 저번 목요일, 이번 수요일
        val prev = LocalDate.of(2021, 11, 4) //목
        val next = LocalDate.of(2021, 11, 9) //화

        // Then 다른 주
        assertEquals(false, controller.isSameWeek(prev, next))
    }

    @Test
    fun `이번주_일요일과_이번주_토요일이_같은주인지_테스트`() {
        // Given 이번 일요일, 이번 토요일
        val prev = LocalDate.of(2021, 11, 7) //일
        val next = LocalDate.of(2021, 11, 13) //토

        // Then 같은 주
        assertEquals(true, controller.isSameWeek(prev, next))
    }

    @Test
    fun `같은날이_같은주인지_테스트`() {
        // Given 이전 목요일, 이후 수요일
        val prev = LocalDate.of(2021, 11, 7)
        val next = LocalDate.of(2021, 11, 7)

        // Then 같은 주
        assertEquals(true, controller.isSameWeek(prev, next))
    }
}