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
import com.drunkenboys.ckscalendar.data.CalendarDesignObject

@Composable
fun WeekHeader(
    design: CalendarDesignObject
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        design.weekSimpleStringSet.forEach { dayId ->
            Text(
                text = dayId,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}