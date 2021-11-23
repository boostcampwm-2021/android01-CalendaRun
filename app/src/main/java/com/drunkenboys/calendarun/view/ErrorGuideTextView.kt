package com.drunkenboys.calendarun.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.util.extensions.dp2px

class ErrorGuideTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.autoCompleteTextViewStyle
) : AppCompatTextView(context, attrs, defStyle) {

    private val defaultHintTextColor = currentHintTextColor
    private val errorColor = ContextCompat.getColor(context, R.color.onError)

    var isError = false
        set(value) {
            if (value) startErrorAnimation()
            field = value
        }

    init {
        doOnTextChanged { _, _, _, _ ->
            if (isError) {
                setHintTextColor(defaultHintTextColor)
                isError = false
            }
        }
    }

    private fun startErrorAnimation() {
        val leftTranslationX = context.dp2px(-2f)
        val rightTranslationX = context.dp2px(2f)

        val hintTextColorAnimator = ObjectAnimator.ofArgb(
            this,
            HINT_TEXT_COLOR,
            currentHintTextColor,
            errorColor
        )
        val translationAnimator = ObjectAnimator.ofFloat(
            this,
            View.TRANSLATION_X,
            rightTranslationX,
            leftTranslationX,
            rightTranslationX,
            leftTranslationX,
            0f
        )

        AnimatorSet().apply {
            duration = DURATION
            play(hintTextColorAnimator).with(translationAnimator)
        }.start()
    }

    companion object {

        private const val DURATION = 300L
        private const val HINT_TEXT_COLOR = "hintTextColor"
    }
}
