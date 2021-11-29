package com.drunkenboys.ckscalendar.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.Period

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
fun LazyListState.InitScroll(
    clickedDay: LocalDate
) {
    val scroll = remember {
        derivedStateOf {
            with(layoutInfo) {
                val firstIndex = visibleItemsInfo.firstOrNull() ?: return@derivedStateOf 0
                val between = Period.between(firstIndex.key as LocalDate, clickedDay)
                firstVisibleItemIndex +  between.months + between.years * 12
            }
        }
    }

    LaunchedEffect(scroll) {
        snapshotFlow { scroll.value }.collect { index ->
            scrollToItem(index)
        }
    }
}