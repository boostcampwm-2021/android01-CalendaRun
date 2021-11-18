package com.drunkenboys.calendarun.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme
import com.drunkenboys.calendarun.data.calendartheme.local.CalendarThemeLocalDataSource
import com.drunkenboys.calendarun.ui.theme.model.ThemeColorType
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val calendarThemeDataSource: CalendarThemeLocalDataSource
) : ViewModel() {

    private val defaultDesign = CalendarDesignObject.getDefaultDesign()

    private val _weekdayTextColor = MutableStateFlow(defaultDesign.weekDayTextColor)
    val weekdayTextColor: StateFlow<Int> = _weekdayTextColor

    private val _holidayTextColor = MutableStateFlow(defaultDesign.holidayTextColor)
    val holidayTextColor: StateFlow<Int> = _holidayTextColor

    private val _saturdayTextColor = MutableStateFlow(defaultDesign.saturdayTextColor)
    val saturdayTextColor: StateFlow<Int> = _saturdayTextColor

    private val _sundayTextColor = MutableStateFlow(defaultDesign.sundayTextColor)
    val sundayTextColor: StateFlow<Int> = _sundayTextColor

    private val _selectedFrameColor = MutableStateFlow(defaultDesign.selectedFrameColor)
    val selectedFrameColor: StateFlow<Int> = _selectedFrameColor

    private val _backgroundColor = MutableStateFlow(defaultDesign.backgroundColor)
    val backgroundColor: StateFlow<Int> = _backgroundColor

    private val _textSize = MutableStateFlow(defaultDesign.textSize)
    val textSize: StateFlow<Int> = _textSize

    private val _textAlign = MutableStateFlow(defaultDesign.textAlign)
    val textAlign: StateFlow<Int> = _textAlign

    private val _languageType = MutableStateFlow(CalendarTheme.LanguageType.KOREAN)
    val languageType: StateFlow<CalendarTheme.LanguageType> = _languageType

    private val _visibleScheduleCount = MutableStateFlow(defaultDesign.visibleScheduleCount)
    val visibleScheduleCount: StateFlow<Int> = _visibleScheduleCount

    init {
        initState()
    }

    private fun initState() {
        viewModelScope.launch {
            val calendarTheme = calendarThemeDataSource.fetchCalendarTheme().first()

            _weekdayTextColor.value = calendarTheme.weekDayTextColor
            _holidayTextColor.value = calendarTheme.holidayTextColor
            _saturdayTextColor.value = calendarTheme.saturdayTextColor
            _sundayTextColor.value = calendarTheme.sundayTextColor
            _selectedFrameColor.value = calendarTheme.selectedFrameColor
            _backgroundColor.value = calendarTheme.backgroundColor
            _textSize.value = calendarTheme.textSize
            _textAlign.value = calendarTheme.textAlign
            _languageType.value = calendarTheme.languageType
            _visibleScheduleCount.value = calendarTheme.visibleScheduleCount
        }
    }

    fun setColorType(color: Int, type: ThemeColorType) {
        val colorState = when (type) {
            ThemeColorType.WEEKDAY -> _weekdayTextColor
            ThemeColorType.HOLIDAY -> _holidayTextColor
            ThemeColorType.SATURDAY -> _saturdayTextColor
            ThemeColorType.SUNDAY -> _sundayTextColor
            ThemeColorType.SELECTED_DAY_STROKE -> _selectedFrameColor
            ThemeColorType.DAY_BACKGROUND -> _backgroundColor
        }

        colorState.value = color
        updateTheme()
    }

    fun setTextSize(textSize: Int) {
        _textSize.value = textSize
        updateTheme()
    }

    private fun updateTheme() {
        viewModelScope.launch {
            calendarThemeDataSource.updateCalendarTheme(createCalendarTheme())
        }
    }

    private fun createCalendarTheme() = CalendarTheme(
        weekDayTextColor = weekdayTextColor.value,
        holidayTextColor = holidayTextColor.value,
        saturdayTextColor = saturdayTextColor.value,
        sundayTextColor = sundayTextColor.value,
        selectedFrameColor = selectedFrameColor.value,
        backgroundColor = backgroundColor.value,
        textSize = textSize.value,
        textAlign = textAlign.value,
        languageType = languageType.value,
        visibleScheduleCount = visibleScheduleCount.value
    )
}
