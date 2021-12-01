package com.drunkenboys.calendarun.data.holiday

import com.google.gson.annotations.SerializedName

data class ResponseHolidayListInfo(
    val response: HolidayListResponse
)

data class HolidayListResponse(
    val body: HolidayListBody
)

data class HolidayListBody(
    val items: HolidayListItems
)

data class HolidayListItems(
    val item: List<Item>
)

data class ResponseHolidayInfo(
    val response: HolidayResponse
)

data class HolidayResponse(
    val body: HolidayBody
)

data class HolidayBody(
    val items: HolidayItems
)

data class HolidayItems(
    val item: Item
)


data class Item(
    val dateName: String,
    @SerializedName("locdate")
    val localDate: Int
)
