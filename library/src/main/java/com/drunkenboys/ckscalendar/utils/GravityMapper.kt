package com.drunkenboys.ckscalendar.utils

import android.text.Layout
import android.view.Gravity
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign

object GravityMapper {
    fun toTextAlign(gravity: Int): TextAlign = when (gravity) {
        Gravity.CENTER -> TextAlign.Center
        Gravity.END -> TextAlign.End
        Gravity.RIGHT -> TextAlign.Right
        Gravity.LEFT -> TextAlign.Left
        Gravity.START -> TextAlign.Start
        else -> TextAlign.Center
    }

    fun toColumnAlign(gravity: Int): Alignment.Horizontal = when (gravity) {
        Gravity.CENTER -> Alignment.CenterHorizontally
        Gravity.END -> Alignment.End
        Gravity.START -> Alignment.Start
        else -> Alignment.CenterHorizontally
    }
}