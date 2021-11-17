package com.drunkenboys.ckscalendar.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collect

// 참고: https://ichi.pro/ko/jetpack-compose-ui-muhan-moglog-peiji-moglog-194681336448872
@Composable
fun LazyListState.ShouldNextScroll(
    onLoadMore : () -> Unit
) {

    // emit
    val shouldLoadMore = remember {
        derivedStateOf {
            with(layoutInfo) {
                visibleItemsInfo.last().index >=  totalItemsCount - 1
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

// FIXME: 첫 아이템보다 이전으로 가는 로직
@Composable
fun LazyListState.ShouldPrevScroll(
    onLoadMore: () -> Unit
) {

    val shouldLoadMore = remember {
        derivedStateOf {
            with(layoutInfo) {
                visibleItemsInfo.last().index >=  totalItemsCount - 1
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