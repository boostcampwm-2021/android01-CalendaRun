package com.drunkenboys.calendarun.ui.addschedule.model

enum class ScheduleNotificationType(val message: String) {
    NONE("없음"),
    TEN_MINUTES_AGO("10분 전 알림"),
    A_HOUR_AGO("한 시간 전 알림"),
    A_DAY_AGO("하루 전 알림")
}
