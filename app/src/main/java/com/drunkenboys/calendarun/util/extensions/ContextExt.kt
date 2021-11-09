package com.drunkenboys.calendarun.util.extensions

import android.content.Context
import android.util.TypedValue

fun Context.dp2px(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
