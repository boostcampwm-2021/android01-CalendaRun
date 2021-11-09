package com.drunkenboys.calendarun.data.idstore

object IdStore {

    private val idMap = mutableMapOf<String, Int>()

    fun getId(key: String) = idMap[key] ?: 0

    fun putId(key: String, id: Int) {
        idMap[key] = id
    }

    fun clearId(key: String) = idMap.remove(key)

    const val KEY_CALENDAR_ID = "calendarId"
    const val KEY_SCHEDULE_ID = "scheduleId"
}
