package com.drunkenboys.calendarun.fake

import android.graphics.Color
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import java.time.LocalDateTime

fun getFakeSchedules() = listOf(
    Schedule(
        id = 0,
        calendarId = 1L,
        name = "프로젝트 기획",
        startDate = LocalDateTime.of(2021, 10, 25, 0, 0),
        endDate = LocalDateTime.of(2021, 10, 27, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLUE
    ),

    Schedule(
        id = 1,
        calendarId = 1L,
        name = "프로토타입 작성",
        startDate = LocalDateTime.of(2021, 10, 27, 0, 0),
        endDate = LocalDateTime.of(2021, 10, 28, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.YELLOW
    ),

    Schedule(
        id = 2,
        calendarId = 1L,
        name = "라이브러리 설정",
        startDate = LocalDateTime.of(2021, 10, 28, 0, 0),
        endDate = LocalDateTime.of(2021, 10, 28, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.MAGENTA
    ),

    Schedule(
        id = 3,
        calendarId = 1L,
        name = "역할 분배 및 문서 작업",
        startDate = LocalDateTime.of(2021, 10, 29, 0, 0),
        endDate = LocalDateTime.of(2021, 10, 29, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.CYAN
    ),

    // ============================2주차 ======================================================
    Schedule(
        id = 4,
        calendarId = 1L,
        name = "데이터 설계",
        startDate = LocalDateTime.of(2021, 11, 1, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 1, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.RED
    ),

    Schedule(
        id = 5,
        calendarId = 1L,
        name = "인터페이스 결정",
        startDate = LocalDateTime.of(2021, 11, 1, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 1, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.GREEN
    ),

    Schedule(
        id = 6,
        calendarId = 1L,
        name = "달력 디자인 요소 정리",
        startDate = LocalDateTime.of(2021, 11, 2, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 3, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLUE
    ),

    Schedule(
        id = 7,
        calendarId = 1L,
        name = "달력 레이아웃 방식 실험",
        startDate = LocalDateTime.of(2021, 11, 2, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 3, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.YELLOW
    ),

    Schedule(
        id = 11,
        calendarId = 1L,
        name = "DB 구현",
        startDate = LocalDateTime.of(2021, 11, 2, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 2, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLACK
    ),

    Schedule(
        id = 12,
        calendarId = 1L,
        name = "일정 CRUD",
        startDate = LocalDateTime.of(2021, 11, 3, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 4, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.MAGENTA
    ),

    Schedule(
        id = 13,
        calendarId = 1L,
        name = "일정 검색",
        startDate = LocalDateTime.of(2021, 11, 4, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 5, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.CYAN
    ),

    Schedule(
        id = 14,
        calendarId = 1L,
        name = "메인화면 기능구현",
        startDate = LocalDateTime.of(2021, 11, 2, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 4, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.RED
    ),

    Schedule(
        id = 15,
        calendarId = 1L,
        name = "일정 알림 기능",
        startDate = LocalDateTime.of(2021, 11, 3, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 4, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLACK
    ),

    // ======================3주차 ===============================
    Schedule(
        id = 8,
        calendarId = 1L,
        name = "월달력 구현",
        startDate = LocalDateTime.of(2021, 11, 4, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 10, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.CYAN
    ),

    Schedule(
        id = 9,
        calendarId = 1L,
        name = "연달력 구현",
        startDate = LocalDateTime.of(2021, 11, 4, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 10, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLUE
    ),

    Schedule(
        id = 10,
        calendarId = 1L,
        name = "라이브러리 연동",
        startDate = LocalDateTime.of(2021, 11, 8, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 11, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.RED
    ),

    Schedule(
        id = 22,
        calendarId = 1L,
        name = "Flow로 마이그레이션",
        startDate = LocalDateTime.of(2021, 11, 8, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 11, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.GREEN
    ),


    // ===============================4주차 =========================
    Schedule(
        id = 16,
        calendarId = 1L,
        name = "휴가",
        startDate = LocalDateTime.of(2021, 11, 15, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 15, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.CYAN
    ),

    Schedule(
        id = 15,
        calendarId = 1L,
        name = "월달력 성능 개선",
        startDate = LocalDateTime.of(2021, 11, 16, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 18, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLACK
    ),


    Schedule(
        id = 17,
        calendarId = 1L,
        name = "연달력 성능 개선",
        startDate = LocalDateTime.of(2021, 11, 16, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 18, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.GREEN
    ),

    Schedule(
        id = 18,
        calendarId = 1L,
        name = "일정 페이징 기능",
        startDate = LocalDateTime.of(2021, 11, 16, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 18, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLUE
    ),

    // =================================5주차 ======================
    Schedule(
        id = 19,
        calendarId = 1L,
        name = "위젯 기능 추가",
        startDate = LocalDateTime.of(2021, 11, 22, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 25, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLACK
    ),

    Schedule(
        id = 20,
        calendarId = 1L,
        name = "QA",
        startDate = LocalDateTime.of(2021, 11, 24, 0, 0),
        endDate = LocalDateTime.of(2021, 11, 25, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.YELLOW
    ),

    // ===========================6주차===============================
    Schedule(
        id = 21,
        calendarId = 1L,
        name = "공휴일 지원",
        startDate = LocalDateTime.of(2021, 11, 29, 0, 0),
        endDate = LocalDateTime.of(2021, 12, 1, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.MAGENTA
    ),

    Schedule(
        id = 23,
        calendarId = 1L,
        name = "테스트",
        startDate = LocalDateTime.of(2021, 11, 29, 0, 0),
        endDate = LocalDateTime.of(2021, 12, 2, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLACK
    ),

    Schedule(
        id = 24,
        calendarId = 1L,
        name = "디버깅",
        startDate = LocalDateTime.of(2021, 11, 29, 0, 0),
        endDate = LocalDateTime.of(2021, 12, 2, 23, 59),
        memo = "",
        notificationType = Schedule.NotificationType.NONE,
        color = Color.BLACK
    ),
)

