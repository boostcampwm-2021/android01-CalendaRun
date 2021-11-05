package com.drunkenboys.calendarun.util

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.ViewHolder.context(): Context {
    return this.itemView.context
}
