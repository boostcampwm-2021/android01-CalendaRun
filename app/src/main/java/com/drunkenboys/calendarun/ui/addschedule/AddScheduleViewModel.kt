package com.drunkenboys.calendarun.ui.addschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drunkenboys.calendarun.ui.addschedule.model.ScheduleNotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddScheduleViewModel @Inject constructor() : ViewModel() {

    val title = MutableLiveData("")

    private val _startDate = MutableLiveData<Date?>()
    val startDate: LiveData<Date?> = _startDate

    private val _endDate = MutableLiveData<Date?>()
    val endDate: LiveData<Date?> = _endDate

    val memo = MutableLiveData("")

    private val _calendarName = MutableLiveData("test")
    val calendarName: LiveData<String> = _calendarName

    private val _notification = MutableLiveData(ScheduleNotificationType.TEN_MINUTES_AGO)
    val notification: MutableLiveData<ScheduleNotificationType> = _notification

    private val _tagColor = MutableLiveData<Int>()
    val tagColor: LiveData<Int> = _tagColor
}
