package com.drunkenboys.calendarun.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import com.drunkenboys.calendarun.ui.theme.model.ThemeColorType

class ThemeViewModel : ViewModel() {

    fun setColorType(color: Int, type: ThemeColorType) {
        Log.d("ThemeViewModel", "setColorType: $color, $type")
    }
}
