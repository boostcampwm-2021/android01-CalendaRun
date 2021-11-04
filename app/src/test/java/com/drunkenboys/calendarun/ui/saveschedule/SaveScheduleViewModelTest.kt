package com.drunkenboys.calendarun.ui.saveschedule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.FakeScheduleLocalDataSource
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SaveScheduleViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: ScheduleLocalDataSource

    private lateinit var viewModel: SaveScheduleViewModel

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataSource = FakeScheduleLocalDataSource()
        viewModel = SaveScheduleViewModel(dataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `뷰모델_초기화_테스트`() {
        val calendarName = "내 캘린더"

        viewModel.init(SaveScheduleFragmentArgs(1, 2, calendarName, BehaviorType.INSERT))

        assertEquals(calendarName, viewModel.calendarName.value)
    }

    @Test
    fun `일정_수정_시_뷰모델_초기화_테스트`() = testScope.runBlockingTest {
        val calendarName = "내 캘린더"
        val startDate = Date()
        val endDate = Date()
        dataSource.insertSchedule(Schedule(0, 0, "test", startDate, endDate, Schedule.NotificationType.A_HOUR_AGO, "memo", 0))

        viewModel.init(SaveScheduleFragmentArgs(0, 0, calendarName, BehaviorType.UPDATE))

        assertEquals("test", viewModel.title.value)
        assertEquals(startDate, viewModel.startDate.value)
        assertEquals(endDate, viewModel.endDate.value)
        assertEquals(Schedule.NotificationType.A_HOUR_AGO, viewModel.notificationType.value)
        assertEquals("memo", viewModel.memo.value)
        assertEquals(0, viewModel.tagColor.value)
    }

    @Test
    fun `제목_미입력_시_저장_테스트`() = testScope.runBlockingTest {
        val calendarName = "내 캘린더"

        viewModel.init(SaveScheduleFragmentArgs(1, 2, calendarName, BehaviorType.INSERT))
        viewModel.saveSchedule()

        assertEquals(null, viewModel.saveScheduleEvent.value)
    }

    @Test
    fun `정상_입력_시_저장_테스트`() {
        val calendarName = "내 캘린더"

        viewModel.init(SaveScheduleFragmentArgs(1, 2, calendarName, BehaviorType.INSERT))
        viewModel.title.value = "내 일정"
        viewModel.saveSchedule()

        assertEquals(Unit, viewModel.saveScheduleEvent.value)
    }

    @Test
    fun `일정_삭제_테스트`() = testScope.runBlockingTest {
        val calendarName = "내 캘린더"
        val startDate = Date()
        val endDate = Date()
        dataSource.insertSchedule(Schedule(0, 0, "test", startDate, endDate, Schedule.NotificationType.A_HOUR_AGO, "memo", 0))
        viewModel.init(SaveScheduleFragmentArgs(0, 0, calendarName, BehaviorType.UPDATE))
        
        viewModel.deleteSchedule()

        assertEquals(Unit, viewModel.saveScheduleEvent.value)
    }
}
