package com.drunkenboys.calendarun.data.holiday

import com.google.gson.annotations.SerializedName


data class ResponseHolidayInfo(
    val response: Response
)

data class Response(
    val header: Header,
    val body: Body
)

data class Header(
    val requestCode: String,
    val resultMsg: String
)

data class Body(
    val items: Items
)

data class Items(
    val item: List<Item>
)

data class Item(
    val dateName: String,
    @SerializedName("locdate")
    val localDate: Int
)

