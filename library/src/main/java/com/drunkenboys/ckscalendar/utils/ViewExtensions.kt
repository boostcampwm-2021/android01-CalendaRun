package com.drunkenboys.ckscalendar.utils

import android.content.Context
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView


fun Context.dp2px(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun View.tintStroke(color: Int, width: Float) {
    this.resources.displayMetrics.density.let { scale ->
        this.background?.constantState?.let {
            val origin = (it.newDrawable().mutate())
            when (origin) {
                is GradientDrawable -> origin.setStroke((width * scale).toInt(), color)
                is StateListDrawable -> {
                    val drawableContainerState = origin.constantState as DrawableContainer.DrawableContainerState
                    val children = drawableContainerState.children
                    children.filterNotNull()
                        .forEach {
                            it as GradientDrawable
                            it.setStroke((width * scale).toInt(), color)
                        }
                }
            }
            this.background = origin
        }
    }
}

fun RecyclerView.ViewHolder.context(): Context {
    return this.itemView.context
}
