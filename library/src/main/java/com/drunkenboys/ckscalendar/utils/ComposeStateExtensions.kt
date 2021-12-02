package com.drunkenboys.ckscalendar.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collect
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

private const val DAY_OF_WEEK = 7

private const val TIME_MILLIS_OF_DAY = 24 * 60 * 60 * 1000

// 참고: https://ichi.pro/ko/jetpack-compose-ui-muhan-moglog-peiji-moglog-194681336448872
@Composable
fun LazyListState.ShouldNextScroll(
    onLoadMore : () -> Unit
) {

    // emit
    val shouldLoadMore = remember {
        derivedStateOf {
            with(layoutInfo) {
                //  화면에 보이는 마지막 인덱스
                val lastIndex = visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true

                lastIndex.index >=  totalItemsCount - 1
            }
        }
    }

    // job
    LaunchedEffect(shouldLoadMore) {
        // collect
        snapshotFlow { shouldLoadMore.value }.collect { should ->
            if (should) { onLoadMore() }
        }
    }
}

@Composable
fun LazyListState.ShouldPrevScroll(
    onLoadMore: () -> Unit
) {

    val shouldLoadMore = remember {
        derivedStateOf {
            with(layoutInfo) {
                //화면에 보이는 첫번째 인덱스
                val firstIndex = visibleItemsInfo.firstOrNull() ?: return@derivedStateOf true

                firstIndex.index <=  1
            }
        }
    }

    // job
    LaunchedEffect(shouldLoadMore) {
        // collect
        snapshotFlow { shouldLoadMore.value }.collect { should ->
            if (should) { onLoadMore() }
        }
    }
}

@Composable
fun LazyListState.InitScroll() {
    val scroll = remember {
        derivedStateOf {
            with(layoutInfo) {
                visibleItemsInfo.firstOrNull() ?: return@derivedStateOf 0

                /** 이동해야할 스크롤 인덱스
                 * 1. 인덱스 1당 1주일이 넘어간다.
                 * 2. 달력의 빈 공간을 채워주기 위해 달이 넘어갈 때마다 비어있는 1주일이 생긴다.
                 * (오늘 - 달력의 첫날) / 7 + 빈공간
                  */
                (getStartDayToToday() / DAY_OF_WEEK / TIME_MILLIS_OF_DAY).toInt() + getPaddingWeeks()
            }
        }
    }

    LaunchedEffect(scroll) {
        snapshotFlow { scroll.value }.collect { index ->
            if (index > 0) scrollToItem(index)
        }
    }
}

private fun getStartDayToToday(): Long {
    val startDay = Calendar.getInstance().apply {
        set(Calendar.YEAR, LocalDate.now().year - 1)
        set(Calendar.MONTH, 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis

    val today = Calendar.getInstance().apply {
        set(Calendar.YEAR, LocalDate.now().year)
        set(Calendar.MONTH, LocalDate.now().monthValue)
        set(Calendar.DAY_OF_MONTH, LocalDate.now().dayOfMonth)
    }.timeInMillis

    return today - startDay
}

private fun getPaddingWeeks(): Int {
    var startDay = LocalDate.of(LocalDate.now().year - 1, 1, 1)
    val today = LocalDate.now()
    var result = 0

    while (startDay < today) {
        if (startDay.dayOfWeek != DayOfWeek.SUNDAY) result += 1
        startDay = startDay.plusMonths(1L)
    }

    return result
}