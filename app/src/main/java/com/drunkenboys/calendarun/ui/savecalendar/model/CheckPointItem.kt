package com.drunkenboys.calendarun.ui.savecalendar.model

import kotlinx.coroutines.flow.MutableStateFlow

data class CheckPointItem(
    val name: MutableStateFlow<String> = MutableStateFlow(""),
    val date: MutableStateFlow<String> = MutableStateFlow(""),
    var check: Boolean = false
)
