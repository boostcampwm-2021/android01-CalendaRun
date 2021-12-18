package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.utils.dp

@Composable
fun BaseText(
    text: String = " ",
    type: DayType,
    modifier: Modifier,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    design: CalendarDesignObject
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = design.textSize.dp(),
        color = when (type) {
            DayType.PADDING -> Color.Transparent
            DayType.HOLIDAY -> Color(design.holidayTextColor)
            DayType.SATURDAY -> Color(design.saturdayTextColor)
            DayType.SUNDAY -> Color(design.sundayTextColor)
            else -> MaterialTheme.colors.primary
        },
        fontWeight = fontWeight,
        textAlign = textAlign
    )
}