package com.drunkenboys.calendarun.fake

import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import java.time.LocalDate

// 부스트캠프 그룹 프로젝트 + 네트워킹 데이의 기간이 포함된 달력
fun getFakeCalendar() = Calendar(
    id = -1L,
    name = "",
    startDate = LocalDate.of(2021, 10, 25),
    endDate = LocalDate.of(2021, 12, 7)
)