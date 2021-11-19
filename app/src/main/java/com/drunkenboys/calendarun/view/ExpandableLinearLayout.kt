package com.drunkenboys.calendarun.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.core.animation.addListener
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import com.drunkenboys.calendarun.util.extensions.setAnimationListener

class ExpandableLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val mDetector = GestureDetectorCompat(context, SingleTapListener())

    private var isExpand = false
    private var isAnimationEnd = true

    private val collapseHeight by lazy { measuredHeight }
    private val contentHeight by lazy { getMeasuredHeightOf(get(1)) }
    private val expandHeight by lazy { collapseHeight + contentHeight }

    private lateinit var layoutAnimation: Animation
    private lateinit var alphaAnimation: ValueAnimator

    init {
        isClickable = true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val result = super.dispatchTouchEvent(ev)
        mDetector.onTouchEvent(ev)
        return result
    }

    private fun expand() {
        if (isExpand) return
        isAnimationEnd = false

        val contentView = get(1)

        val anim = ExpandAnimation(height, expandHeight - height)
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

        val anim = CollapseAnimation(height, height - collapseHeight)
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

    private inner class ExpandAnimation(private val height: Int, private val additionalHeight: Int) : Animation() {

        init {
            duration = DEFAULT_ANIMATION_DURATION
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val diff = additionalHeight * interpolatedTime
            layoutParams.height = (height + diff).toInt()
            requestLayout()
        }
    }

    private inner class CollapseAnimation(private val height: Int, private val additionalHeight: Int) : Animation() {

        init {
            duration = DEFAULT_ANIMATION_DURATION
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            val diff = additionalHeight * interpolatedTime
            layoutParams.height = (height - diff).toInt()
            requestLayout()
        }
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (isAnimationEnd && isExpand) {
                collapse()
            } else if (isAnimationEnd && !isExpand) {
                expand()
            }
            return super.onSingleTapUp(e)
        }
    }

    companion object {

        private const val DEFAULT_ANIMATION_DURATION = 150L
    }
}
