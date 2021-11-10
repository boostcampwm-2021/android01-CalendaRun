package com.drunkenboys.calendarun.util.extensions

import androidx.lifecycle.LiveData

fun <T> LiveData<T>.getOrThrow() = this.value ?: throw IllegalArgumentException()
