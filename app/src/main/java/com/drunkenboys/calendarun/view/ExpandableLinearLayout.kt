package com.drunkenboys.calendarun.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.get
import androidx.core.view.isVisible
import com.drunkenboys.calendarun.util.extensions.setAnimationListener

class ExpandableLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private var isExpand = false
    private var isAnimationEnd = true

    private var originalHeight = 0

    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
        if (isAnimationEnd && isExpand) {
            collapse()
        } else if (isAnimationEnd && !isExpand) {
            expand()
        }
        return super.performClick()
    }

    private fun expand() {
        if (isExpand) return
        isAnimationEnd = false

        val contentView = get(1)

        originalHeight = height
        val contentViewHeight = getMeasuredHeightOf(contentView)

        val anim = ExpandAnimation(contentViewHeight)
        anim.setAnimationListener(onEnd = {
            contentView.isVisible = true
            contentView.alpha = 0f
            isExpand = true

            ObjectAnimator.ofFloat(contentView, ALPHA, 1f).apply {
                duration = DEFAULT_ANIMATION_DURATION
                addListener(onEnd = { isAnimationEnd = true })
            }.start()
        })

        startAnimation(anim)
    }

    private fun collapse() {
        if (!isExpand) return
        isAnimationEnd = false

        val contentView = get(1)

        originalHeight = height
        val contentViewHeight = contentView.height

        val anim = CollapseAnimation(contentViewHeight)
        anim.setAnimationListener(onEnd = {
            contentView.isVisible = false
            isExpand = false
            isAnimationEnd = true
        })

        ObjectAnimator.ofFloat(contentView, ALPHA, 0f).apply {
            duration = DEFAULT_ANIMATION_DURATION
            addListener(onEnd = { startAnimation(anim) })
        }.start()
    }

    private fun getMeasuredHeightOf(view: View): Int {
        val matchParentMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        return view.measuredHeight
    }

    private inner class ExpandAnimation(private val targetHeight: Int) : Animation() {

        init {
            duration = DEFAULT_ANIMATION_DURATION
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val diff = targetHeight * interpolatedTime
            layoutParams.height = (originalHeight + diff).toInt()
            requestLayout()
        }
    }

    private inner class CollapseAnimation(private val targetHeight: Int) : Animation() {

        init {
            duration = DEFAULT_ANIMATION_DURATION
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val diff = targetHeight * interpolatedTime
            layoutParams.height = (originalHeight - diff).toInt()
            requestLayout()
        }
    }

    companion object {

        private const val DEFAULT_ANIMATION_DURATION = 150L
    }
}
