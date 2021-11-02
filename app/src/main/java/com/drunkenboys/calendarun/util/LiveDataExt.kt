package com.drunkenboys.calendarun.util

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.getOrThrow() = this.value ?: throw IllegalArgumentException()
