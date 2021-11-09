package com.drunkenboys.calendarun.ui.searchschedule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.FakeScheduleLocalDataSource
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SearchScheduleViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: ScheduleLocalDataSource

    private lateinit var viewModel: SearchScheduleViewModel

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataSource = FakeScheduleLocalDataSource()
        viewModel = SearchScheduleViewModel(dataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `이후_일정이_있을_때_초기_검색_테스트`() = testScope.runBlockingTest {
        val tomorrowCalendar = Calendar.getInstance()
        tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1)
        val yesterdayCalendar = Calendar.getInstance()
        yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
        initDataSource(dateList = arrayOf(yesterdayCalendar.time, tomorrowCalendar.time))

        viewModel.fetchScheduleList()
        val result = viewModel.listItem.value

        if (result.isNullOrEmpty()) {
            fail("LiveData's value is null or empty")
            return@runBlockingTest
        }
        assertEquals(1, result.size)
    }

    @Test
    fun `이후_일정이_없을_때_초기_검색_테스트`() = testScope.runBlockingTest {
        val yesterdayCalendar = Calendar.getInstance()
        yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)
        initDataSource(dateList = arrayOf(yesterdayCalendar.time, yesterdayCalendar.time))

        viewModel.fetchScheduleList()
        val result = viewModel.listItem.value

        if (result == null) {
            fail("LiveData's value is null")
            return@runBlockingTest
        }
        assertEquals(0, result.size)
    }

    @Test
    fun `검색_기능_테스트`() = testScope.runBlockingTest {
        val date = Date()
        initDataSource(name = "foo", dateList = arrayOf(date, date, date))
        initDataSource(name = "bar", dateList = arrayOf(date, date, date, date))
        initDataSource(name = "quz", dateList = arrayOf(date, date))

        viewModel.searchSchedule("bar")
        advanceTimeBy(500)
        val result = viewModel.listItem.value

        if (result == null) {
            fail("LiveData's value is null")
            return@runBlockingTest
        }
        assertEquals(1, result.size)
        assertEquals(4, result[0].scheduleList.size)
        assertTrue(result[0].scheduleList.all { it.schedule.name.startsWith("bar") })
    }

    private suspend fun initDataSource(name: String = "name", vararg dateList: Date) {
        dateList.forEachIndexed { index, date ->
            dataSource.insertSchedule(
                Schedule(
                    id = index.toLong(),
                    calendarId = 1,
                    name = "$name$index",
                    startDate = date,
                    endDate = Date(),
                    notificationType = Schedule.NotificationType.NONE,
                    memo = "memo$index",
                    color = 0
                )
            )
        }
    }
}
