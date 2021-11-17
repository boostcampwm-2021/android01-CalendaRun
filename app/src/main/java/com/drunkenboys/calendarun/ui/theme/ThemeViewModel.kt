package com.drunkenboys.calendarun.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.ui.theme.model.ThemeColorType
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {

    private val defaultDesign = CalendarDesignObject.getDefaultDesign()

    private val _weekdayColor = MutableStateFlow(defaultDesign.weekDayTextColor)
    val weekdayColor: StateFlow<Int> = _weekdayColor

    private val _holidayColor = MutableStateFlow(defaultDesign.holidayTextColor)
    val holidayColor: StateFlow<Int> = _holidayColor

    private val _saturdayColor = MutableStateFlow(defaultDesign.saturdayTextColor)
    val saturdayColor: StateFlow<Int> = _saturdayColor

    private val _sundayColor = MutableStateFlow(defaultDesign.sundayTextColor)
    val sundayColor: StateFlow<Int> = _sundayColor

    private val _selectedDayStrokeColor = MutableStateFlow(defaultDesign.selectedFrameColor)
    val selectedDayStrokeColor: StateFlow<Int> = _selectedDayStrokeColor

    private val _dayBackgroundColor = MutableStateFlow(defaultDesign.backgroundColor)
    val dayBackgroundColor: StateFlow<Int> = _dayBackgroundColor

    fun setColorType(color: Int, type: ThemeColorType) {
        val colorState = when (type) {
            ThemeColorType.WEEKDAY -> _weekdayColor
            ThemeColorType.HOLIDAY -> _holidayColor
            ThemeColorType.SATURDAY -> _saturdayColor
            ThemeColorType.SUNDAY -> _sundayColor
            ThemeColorType.SELECTED_DAY_STROKE -> _selectedDayStrokeColor
            ThemeColorType.DAY_BACKGROUND -> _dayBackgroundColor
        }

        viewModelScope.launch {
            colorState.emit(color)
        }
    }
}
