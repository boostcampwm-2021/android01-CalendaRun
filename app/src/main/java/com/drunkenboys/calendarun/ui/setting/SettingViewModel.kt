package com.drunkenboys.calendarun.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {

    private val _manageCalendarClickEvent = MutableSharedFlow<Unit>()
    val manageCalendarClickEvent: SharedFlow<Unit> = _manageCalendarClickEvent

    private val _themeClickEvent = MutableSharedFlow<Unit>()
    val themeClickEvent: SharedFlow<Unit> = _themeClickEvent

    private val _infoClickEvent = MutableSharedFlow<Unit>()
    val infoClickEvent: SharedFlow<Unit> = _infoClickEvent

    private val _licenseClickEvent = MutableSharedFlow<Unit>()
    val licenseClickEvent: SharedFlow<Unit> = _licenseClickEvent

    fun emitManageCalendarClickEvent() {
        viewModelScope.launch {
            _manageCalendarClickEvent.emit(Unit)
        }
    }

    fun emitThemeClickEvent() {
        viewModelScope.launch {
            _themeClickEvent.emit(Unit)
        }
    }

    fun emitInfoClickEvent() {
        viewModelScope.launch {
            _infoClickEvent.emit(Unit)
        }
    }

    fun emitLicenseClickEvent() {
        viewModelScope.launch {
            _licenseClickEvent.emit(Unit)
        }
    }
}
