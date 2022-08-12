package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.drunkenboys.ckscalendar.yearcalendar.CustomTheme
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel

/**
 * 월 표시
 */
@Composable
fun AnimatedMonthHeader(
    listState: LazyListState,
    monthName: String
) {
    // 0 ~ 1을 부드럽게 오가는 애니메이션
    // 스크롤을 하는 중이면 1이 된다.
    val density: Float by animateFloatAsState(
        targetValue = if(listState.isScrollInProgress) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    // 스크롤 할 때 투명도가 사라지는 애니메이션
    Card(
        modifier = Modifier.alpha(density).zIndex(10f),
        backgroundColor = MaterialTheme.colors.background,
        elevation = 10.dp
    ) {
        Text(
            text = monthName,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp,top = 5.dp, bottom = 5.dp)
        )
    }
}

@Preview
@Composable
fun PreviewMonthHeader() {
    val viewModel = YearCalendarViewModel()
    CustomTheme(design = viewModel.design.value) {
        Card(
            modifier = Modifier.wrapContentSize().background(color = Color.Black),
            elevation = 200.dp,
        ) {
            Text(
                text = "1월",
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
            )
        }
    }
}