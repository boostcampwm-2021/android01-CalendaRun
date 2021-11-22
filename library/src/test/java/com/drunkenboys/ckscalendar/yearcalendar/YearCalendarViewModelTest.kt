package com.drunkenboys.ckscalendar.yearcalendar

import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import org.junit.Assert.*

import org.junit.Test
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
    fun `해당 날짜가 포함된 모든 일정 찾기 테스트`() {
        // Given 오늘이 포함된 10개의 스케줄
        viewModel.setSchedules(fakeSchedules)

        // When 해당 날짜의 일정을 모두 반환
        val findSchedules = viewModel.getDaySchedules(LocalDateTime.now())

        // Then 탐색한 개수와 일정 개수가 일치해야 한다.
        assertEquals(fakeSchedules.size, findSchedules.size)

        // And 탐색한 일정 모두 오늘이 포함된 일정이어야 한다.
        assertTrue(findSchedules.all { schedule -> today in schedule.startDate..schedule.endDate })
    }

    @Test
    fun `해당 날짜 이전만 포함된 일정 탐색하지 않기 테스트`() {
        // Given 오늘 날짜가 아닌 스케줄들
        val notTodaySchedules = (2..100000).map { minusDay ->
            CalendarScheduleObject(
                id = minusDay,
                color = 0,
                text = "test",
                startDate = today.minusDays(minusDay.toLong()),
                endDate = today.minusDays(minusDay.toLong() - 1)
            )
        }

        viewModel.setSchedules(notTodaySchedules)

        // When 해당 날짜의 일정을 모두 반환
        val findSchedules = viewModel.getDaySchedules(today)

        // Then 빈 리스트가 반환돼야 한다.
        assertTrue(findSchedules.isEmpty())
    }

    @Test
    fun `오늘 하루짜리 일정 추가 시 오늘 일정 탐색 테스트`() {
        // Given 오늘 시작하고 오늘 끝나는 일정
        val oneDaySchedule = CalendarScheduleObject(
            id = 0,
            color = 0,
            text = "",
            startDate = today,
            endDate = today
        )

        viewModel.setSchedule(oneDaySchedule)

        // When 오늘의 일정을 모두 반환
        val findSchedules = viewModel.getDaySchedules(today)

        // Then 추가한 일정과 같은 일정이 반환돼야 한다.
        assertEquals(oneDaySchedule, findSchedules[0])
    }

    @Test
    fun `다수의 일정 추가 시 이전에 추가된 일정 제거 테스트`() {
        // Given 이전 일정이 추가된 뷰모델
        viewModel.setSchedules(fakeSchedules)

        // And 빈 일정 배열
        val newSchedules = listOf<CalendarScheduleObject>()

        // When 빈 일정 배열을 추가
        viewModel.setSchedules(newSchedules)

        // Then 일정이 비어 있어야 한다.
        assertTrue(viewModel.schedules.value.isEmpty())
    }
}