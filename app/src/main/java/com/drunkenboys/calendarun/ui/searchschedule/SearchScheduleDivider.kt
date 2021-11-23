package com.drunkenboys.calendarun.ui.searchschedule

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.calendarun.util.extensions.dp2px
import kotlin.math.roundToInt

class SearchScheduleDivider(private val context: Context) : DividerItemDecoration(context, RecyclerView.VERTICAL) {

    private var leftInset = 16f
    private var rightInset = 16f

    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || drawable == null) {
            return
        }
        drawVertical(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val drawable = drawable ?: return

        canvas.save()
        var left: Int
        var right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        left += context.dp2px(leftInset).toInt()
        right -= context.dp2px(rightInset).toInt()

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child.tag == "DateItem") {
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                val bottom = mBounds.top + child.translationY.roundToInt()
                val top = bottom - drawable.intrinsicHeight
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(canvas)
            }
        }
        canvas.restore()
    }
}
