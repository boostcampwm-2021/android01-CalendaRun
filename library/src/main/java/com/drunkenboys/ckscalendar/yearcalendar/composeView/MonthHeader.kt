package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedMonthHeader(
    listState: LazyListState,
    month: Int
) {
    val density: Float by animateFloatAsState(
        targetValue = if(listState.isScrollInProgress) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Text(
        text = "${month}월",
        color = MaterialTheme.colors.primary,
        modifier = Modifier.alpha(density).padding(start = (density * 5).dp)
    )
}