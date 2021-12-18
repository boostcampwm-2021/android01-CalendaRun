package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.drunkenboys.ckscalendar.utils.GravityMapper
import com.drunkenboys.ckscalendar.yearcalendar.CustomTheme
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel

@Composable
fun WeekHeader(
    viewModel: YearCalendarViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        viewModel.design.value.weekSimpleStringSet.forEach { dayId ->
            Text(
                text = dayId,
                color = MaterialTheme.colors.primary,
                textAlign = GravityMapper.toTextAlign(viewModel.design.value.textAlign)
            )
        }
    }
}

@Preview
@Composable
fun PreviewWeekHeader() {
    val viewModel = YearCalendarViewModel()

    CustomTheme(design = viewModel.design.value) {
        WeekHeader(viewModel = viewModel)
    }
}