package com.drunkenboys.calendarun.ui.savecalendar.model

import androidx.lifecycle.MutableLiveData

data class CheckPointItem(
    val name: MutableLiveData<String> = MutableLiveData(),
    val date: MutableLiveData<String> = MutableLiveData(),
    var check: Boolean = false
)
