package com.drunkenboys.calendarun.fake

import com.drunkenboys.calendarun.data.slice.entity.Slice
import java.time.LocalDate

private var id = 20211202L

fun getFakeSlices() = listOf(
    Slice(
        id = id++,
        calendarId = 20211202L,
        name = "1주차",
        startDate = LocalDate.of(2021, 10, 25),
        endDate = LocalDate.of(2021, 10, 31),
    ),

    Slice(
        id = id++,
        calendarId = 20211202L,
        name = "2주차",
        startDate = LocalDate.of(2021, 11, 1),
        endDate = LocalDate.of(2021, 11, 7),
    ),

    // 3주차는 시연에서 추가
    Slice(
        id = id++,
        calendarId = 20211202L,
        name = "4주차",
        startDate = LocalDate.of(2021, 11, 15),
        endDate = LocalDate.of(2021, 11, 21),
    ),

    Slice(
        id = id++,
        calendarId = 20211202L,
        name = "5주차",
        startDate = LocalDate.of(2021, 11, 22),
        endDate = LocalDate.of(2021, 11, 28),
    ),

    Slice(
        id = id++,
        calendarId = 20211202L,
        name = "6주차",
        startDate = LocalDate.of(2021, 11, 27),
        endDate = LocalDate.of(2021, 12, 3),
    ),

    Slice(
        id = id++,
        calendarId = 20211202L,
        name = "마무리",
        startDate = LocalDate.of(2021, 12, 4),
        endDate = LocalDate.of(2021, 12, 7),
    ),
)
