package com.drunkenboys.ckscalendar.utils

import android.view.Gravity
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
}