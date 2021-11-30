package com.drunkenboys.calendarun.ui.searchschedule

import androidx.lifecycle.viewModelScope
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.FakeScheduleLocalDataSource
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem
import com.drunkenboys.ckscalendar.data.ScheduleColorType
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SearchScheduleViewModelTest {

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
        val localDateTime = LocalDateTime.now()
        initDataSource(dateList = arrayOf(localDateTime.minusDays(1), localDateTime.plusDays(1)))

        viewModel.searchSchedule()
        advanceTimeBy(500)
        val result = viewModel.listItem.value

        assertEquals(2, result.size)
    }

    @Test
    fun `이후_일정이_없을_때_초기_검색_테스트`() = testScope.runBlockingTest {
        val localDateTime = LocalDateTime.now()
        initDataSource(dateList = arrayOf(localDateTime.minusDays(1), localDateTime.minusDays(1)))

        viewModel.searchSchedule()
        advanceTimeBy(500)
        val result = viewModel.listItem.value

        assertEquals(3, result.size)
    }

    @Test
    fun `검색_기능_테스트`() = testScope.runBlockingTest {
        val date = LocalDateTime.now().plusDays(1)
        initDataSource(name = "foo", dateList = arrayOf(date, date, date))
        initDataSource(name = "bar", dateList = arrayOf(date, date, date, date))
        initDataSource(name = "quz", dateList = arrayOf(date, date))

        viewModel.searchSchedule("bar")
        advanceTimeBy(500)
        val result = viewModel.listItem.value

        assertEquals(5, result.size)
        result.filterIsInstance(ScheduleItem::class.java)
            .all { "bar" in it.schedule.name }
            .let { assertTrue(it) }
    }

    @Test
    fun `검색_디바운스_테스트`() = testScope.runBlockingTest {
        val date = LocalDateTime.now().plusDays(1)
        initDataSource(name = "foo", dateList = arrayOf(date, date, date))
        initDataSource(name = "bar", dateList = arrayOf(date, date, date, date))
        initDataSource(name = "quz", dateList = arrayOf(date, date))

        viewModel.searchSchedule("bar")
        viewModel.searchSchedule("quz")
        advanceTimeBy(500)
        val result = viewModel.listItem.value

        assertEquals(3, result.size)
        result.filterIsInstance<ScheduleItem>()
            .all { "quz" in it.schedule.name }
            .let { assertTrue(it) }
    }

    @Test
    fun `이전_일정_검색_테스트`() = testScope.runBlockingTest {
        initDataSource(dateList = arrayOf(tomorrow, tomorrow, tomorrow, tomorrow))
        initData((1..35).toSchedules(yesterday))

        viewModel.searchSchedule()
        advanceTimeBy(500)
        viewModel.trySearchPrev()
        val result = viewModel.listItem.value

        assertEquals(36, result.size)
    }

    @Test
    fun `이전_일정_검색_페이징_테스트`() = runBlocking {
        initDataSource(dateList = arrayOf(tomorrow, tomorrow, tomorrow, tomorrow))
        initData((1..35).toSchedules(yesterday.minusDays(1)))
        initData((36..70).toSchedules(yesterday))

        viewModel.searchSchedule()
        delay(500)
        viewModel.trySearchPrev()
        delay(600)
        viewModel.trySearchPrev()
        val result = viewModel.listItem.value

        assertEquals(62, result.size)
        result.filterIsInstance<ScheduleItem>()
            .all { it.schedule.startDate < today }
            .let { assertTrue(it) }
        viewModel.viewModelScope.cancel()
    }

    private suspend fun initDataSource(name: String = "name", vararg dateList: LocalDateTime) {
        dateList.forEachIndexed { index, date ->
            dataSource.insertSchedule(
                Schedule(
                    id = index.toLong(),
                    calendarId = 1,
                    name = "$name$index",
                    startDate = date,
                    endDate = LocalDateTime.now(),
                    notificationType = Schedule.NotificationType.NONE,
                    memo = "memo$index",
                    color = 0
                )
            )
        }
    }

    private fun IntRange.toSchedules(date: LocalDateTime) = map {
        Schedule(
            it.toLong(),
            0,
            "일정 $it",
            date,
            date,
            Schedule.NotificationType.TEN_MINUTES_AGO,
            "메모 $it",
            ScheduleColorType.RED.color
        )
    }

    private suspend fun initData(scheduleList: List<Schedule>) {
        scheduleList.forEach {
            dataSource.insertSchedule(it)
        }
    }

    companion object {

        private val yesterday = LocalDateTime.now().minusDays(1)
        private val today = LocalDateTime.now()
        private val tomorrow = LocalDateTime.now().plusDays(1)
    }
}
