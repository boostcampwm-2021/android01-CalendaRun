package com.drunkenboys.calendarun.ui.savecalendar

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.drunkenboys.calendarun.KEY_CALENDAR_ID
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.calendar.local.FakeCalendarLocalDataSource
import com.drunkenboys.calendarun.data.slice.entity.Slice
import com.drunkenboys.calendarun.data.slice.local.FakeSliceLocalDataSource
import com.drunkenboys.calendarun.data.slice.local.SliceLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SearchScheduleViewModelTest {

    private lateinit var calendarDataSource: CalendarLocalDataSource
    private lateinit var sliceDataSource: SliceLocalDataSource

    private lateinit var viewModel: SaveCalendarViewModel

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        calendarDataSource = FakeCalendarLocalDataSource()
        sliceDataSource = FakeSliceLocalDataSource()
    }

    private fun createViewModel() = SaveCalendarViewModel(
        SavedStateHandle(mapOf(KEY_CALENDAR_ID to 0L)),
        calendarDataSource,
        sliceDataSource
    )

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `뷰모델_초기화_시_달력_이름_업데이트_테스트`() = testScope.runBlockingTest {
        val calendarName = "test calendar"
        calendarDataSource.insertCalendar(Calendar(0, calendarName, LocalDate.now(), LocalDate.now()))

        viewModel = createViewModel()

        assertEquals(calendarName, viewModel.calendarName.value)
    }

    @Test
    fun `체크포인트가_없을_때_뷰모델_초기화_테스트`() = testScope.runBlockingTest {
        viewModel = createViewModel()
        val result = viewModel.sliceItemList.value

        assertEquals(1, result.size)
    }

    @Test
    fun `체크포인트가_있을_때_뷰모델_초기화_테스트`() = testScope.runBlockingTest {
        val calendarName = "test calendar"
        calendarDataSource.insertCalendar(Calendar(0, calendarName, LocalDate.now(), LocalDate.now()))
        for (i in (1..10)) {
            sliceDataSource.insertSlice(
                Slice(i.toLong(), 0, "check point $i", LocalDate.now(), LocalDate.now())
            )
        }

        viewModel = createViewModel()
        val result = viewModel.sliceItemList.value

        assertEquals(10, result.size)
    }

    @Test
    fun `체크포인트_추가_테스트`() = testScope.runBlockingTest {
        viewModel = createViewModel()

        viewModel.addSlice()
        val result = viewModel.sliceItemList.value

        assertEquals(2, result.size)
    }

    @Test
    fun `체크포인트_삭제_테스트`() = testScope.runBlockingTest {
        val calendarName = "test calendar"
        calendarDataSource.insertCalendar(Calendar(0, calendarName, LocalDate.now(), LocalDate.now()))
        for (i in (1..10)) {
            sliceDataSource.insertSlice(
                Slice(i.toLong(), 0, "check point $i", LocalDate.now(), LocalDate.now())
            )
        }
        viewModel = createViewModel()
        viewModel.sliceItemList.value.slice(1..3).forEach {
            it.check = true
        }

        viewModel.deleteSliceItem(viewModel.sliceItemList.value)

        assertEquals(7, viewModel.sliceItemList.value.size)
    }

    @Test
    fun `달력_저장_테스트`() = testScope.runBlockingTest {
        viewModel = createViewModel()
        viewModel.calendarName.value = "test calendar"
        val checkPoint = viewModel.sliceItemList.value.first()
        checkPoint.name.value = "checkpoint"
        checkPoint.startDate.value = LocalDate.now()
        checkPoint.endDate.value = LocalDate.now()

        viewModel.saveCalendarEvent.test {
            viewModel.emitSaveCalendar()

            val result = awaitItem()
            assertTrue(result)
        }
    }

    @Test
    fun `달력_이름이_비어있을_때_달력_저장_실패`() = testScope.runBlockingTest {
        viewModel = createViewModel()
        val checkPoint = viewModel.sliceItemList.value.first()
        checkPoint.name.value = "checkpoint"
        checkPoint.startDate.value = LocalDate.now()
        checkPoint.endDate.value = LocalDate.now()

        viewModel.saveCalendarEvent.test {
            viewModel.emitSaveCalendar()

            val result = awaitItem()
            assertFalse(result)
        }
    }

    @Test
    fun `체크포인트_이름이_비어있을_때_달력_저장_실패`() = testScope.runBlockingTest {
        viewModel = createViewModel()
        viewModel.calendarName.value = "test calendar"
        val checkPoint = viewModel.sliceItemList.value.first()
        checkPoint.startDate.value = LocalDate.now()
        checkPoint.endDate.value = LocalDate.now()

        viewModel.saveCalendarEvent.test {
            viewModel.emitSaveCalendar()

            val result = awaitItem()
            assertFalse(result)
        }
    }

    @Test
    fun `체크포인트_시작_날짜가_비어있을_때_달력_저장_실패`() = testScope.runBlockingTest {
        viewModel = createViewModel()
        viewModel.calendarName.value = "test calendar"
        val checkPoint = viewModel.sliceItemList.value.first()
        checkPoint.name.value = "checkpoint"
        checkPoint.endDate.value = LocalDate.now()

        viewModel.saveCalendarEvent.test {
            viewModel.emitSaveCalendar()

            val result = awaitItem()
            assertFalse(result)
        }
    }

    @Test
    fun `체크포인트_끝_날짜가_비어있을_때_달력_저장_실패`() = testScope.runBlockingTest {
        viewModel = createViewModel()
        viewModel.calendarName.value = "test calendar"
        val checkPoint = viewModel.sliceItemList.value.first()
        checkPoint.name.value = "checkpoint"
        checkPoint.startDate.value = LocalDate.now()

        viewModel.saveCalendarEvent.test {
            viewModel.emitSaveCalendar()

            val result = awaitItem()
            assertFalse(result)
        }
    }
}
