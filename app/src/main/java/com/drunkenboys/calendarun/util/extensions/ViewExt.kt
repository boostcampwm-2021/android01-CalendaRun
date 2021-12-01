package com.drunkenboys.calendarun.util.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

fun View.startAnimation(animationResId: Int) {
    val anim = AnimationUtils.loadAnimation(context, animationResId)
    this.startAnimation(anim)
}

fun View.startAnimation(animationResId: Int, onAnimationEnd: () -> Unit) {
    val anim = AnimationUtils.loadAnimation(context, animationResId)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd.invoke()
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    })
    this.startAnimation(anim)
}

fun TextView.updateCompoundDrawablesRelativeWithIntrinsicBounds(
    start: Drawable? = compoundDrawablesRelative[0],
    top: Drawable? = compoundDrawablesRelative[1],
    end: Drawable? = compoundDrawablesRelative[2],
    bottom: Drawable? = compoundDrawablesRelative[3]
) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
}
