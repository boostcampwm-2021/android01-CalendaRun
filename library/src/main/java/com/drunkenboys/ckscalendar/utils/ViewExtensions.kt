package com.drunkenboys.ckscalendar.utils

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView

fun Context.dp2px(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun RecyclerView.ViewHolder.context(): Context {
    return this.itemView.context
}
