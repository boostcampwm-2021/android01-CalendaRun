package com.drunkenboys.calendarun.data.holiday

import com.google.gson.annotations.SerializedName

data class ResponseHolidayInfo(
    val header: Header,
    val body: Body
)

data class Header(
    val requestCode: String,
    val resultMsg: String
)

data class Body(
    val items: List<Item>
)

data class Item(
    val dataKind: String,
    val dateName: String,
    val isHoliday: String,
    @SerializedName("locdate")
    val localDate: Long
)
