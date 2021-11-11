package com.drunkenboys.calendarun.data.idstore

object IdStore {

    private val idMap: MutableMap<String, Long> = mutableMapOf()

    fun getId(key: String) = idMap[key] ?: 0

    fun putId(key: String, id: Long) {
        idMap[key] = id
    }

    fun clearId(key: String) = idMap.remove(key)

    const val KEY_CALENDAR_ID = "calendarId"
    const val KEY_SCHEDULE_ID = "scheduleId"
}
