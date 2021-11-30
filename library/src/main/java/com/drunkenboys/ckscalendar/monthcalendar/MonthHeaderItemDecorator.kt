package com.drunkenboys.ckscalendar.monthcalendar

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.utils.dp2px

internal class MonthHeaderItemDecorator : RecyclerView.ItemDecoration() {

    private val textPaint = Paint()
    private val backgroundPaint = Paint()

    private val rect = Rect()

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val startYearHash = (parent.adapter as? MonthAdapter)?.startDayWithYearFlags
        startYearHash?.let {
            if (parent.scrollState == SCROLL_STATE_DRAGGING) {
                (0 until parent.childCount / 7).forEach { yearIndex ->
                    val childView = parent.getChildAt(yearIndex * 7)
                    val adapterPosition = parent.getChildAdapterPosition(childView)
                    startYearHash.entries.forEach {
                        if (adapterPosition / 7 == it.value / 7) {
                            val drawText = "${it.key}${childView.context.getString(R.string.common_year)}"
                            val dp4 = childView.context.dp2px(4f)
                            val dp8 = childView.context.dp2px(8f)
                            val dp16 = childView.context.dp2px(16f)
                            textPaint.apply {
                                textSize = childView.context.dp2px(20f)
                                color = ContextCompat.getColor(childView.context, R.color.calendar_black)
                            }
                            backgroundPaint.color = ContextCompat.getColor(childView.context, R.color.calendar_white)
                            backgroundPaint.setShadowLayer(dp4, 0f, 2f, ContextCompat.getColor(childView.context, R.color.calendar_shadow))
                            textPaint.getTextBounds(drawText, 0, drawText.length, rect)
                            rect.set(
                                (rect.left + childView.x - dp4).toInt(),
                                (rect.top + childView.y + dp16 + dp8).toInt(),
                                (rect.right + childView.x + dp16).toInt(),
                                (rect.bottom + childView.y + dp16 + dp8).toInt()
                            )
                            canvas.drawRoundRect(
                                rect.left.toFloat(),
                                rect.top - dp8,
                                rect.right.toFloat(),
                                rect.bottom + dp8,
                                16f,
                                16f,
                                backgroundPaint
                            )
                            canvas.drawText(drawText, childView.x + dp8, childView.y + dp16 + dp8, textPaint)
                        }
                    }
                }
            }
        }
    }
}
