package com.drunkenboys.calendarun.ui.saveschedule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.drunkenboys.calendarun.data.schedule.local.FakeScheduleLocalDataSource
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.saveschedule.model.BehaviorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

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

        viewModel.init(1, 2, calendarName, BehaviorType.INSERT)

        Assert.assertEquals(calendarName, viewModel.calendarName.value)
    }

    @Test
    fun `제목_미입력_시_저장_테스트`() = testScope.runBlockingTest {
        val calendarName = "내 캘린더"

        viewModel.init(1, 2, calendarName, BehaviorType.INSERT)
        viewModel.saveSchedule()

        Assert.assertEquals(null, viewModel.saveScheduleEvent.value)
    }

    @Test
    fun `정상_입력_시_저장_테스트`() {
        val calendarName = "내 캘린더"

        viewModel.init(1, 2, calendarName, BehaviorType.INSERT)
        viewModel.title.value = "내 일정"
        viewModel.saveSchedule()

        Assert.assertEquals(Unit, viewModel.saveScheduleEvent.value)
    }
}
