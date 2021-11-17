package com.drunkenboys.calendarun.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.util.extensions.updateCompoundDrawablesRelativeWithIntrinsicBounds

class ColorIndicatorTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    init {
        getAttrs(attrs, defStyle)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        attrs ?: return

        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ColorIndicatorTextView, defStyle, 0
        )

        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val drawableEndTint = typedArray.getColor(R.styleable.ColorIndicatorTextView_indicatorTint, 0)

        val drawableEnd = compoundDrawablesRelative[2]

        drawableEnd.setTintList(ColorStateList.valueOf(drawableEndTint))

        typedArray.recycle()
    }

    fun setIndicatorTint(color: Int) {
        if (color == ContextCompat.getColor(context, R.color.white)) {
            updateCompoundDrawablesRelativeWithIntrinsicBounds(end = ContextCompat.getDrawable(context, R.drawable.bg_tag_color_stroke))
        } else {
            updateCompoundDrawablesRelativeWithIntrinsicBounds(end = ContextCompat.getDrawable(context, R.drawable.bg_tag_color))
            compoundDrawablesRelative[2].setTintList(ColorStateList.valueOf(color))
        }
    }
}
