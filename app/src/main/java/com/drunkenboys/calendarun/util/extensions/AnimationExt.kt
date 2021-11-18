package com.drunkenboys.calendarun.util.extensions

import android.view.animation.Animation

fun Animation.setAnimationListener(
    onStart: ((Animation?) -> Unit)? = null,
    onEnd: ((Animation?) -> Unit)? = null,
    onRepeat: ((Animation?) -> Unit)? = null
) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart?.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd?.invoke(animation)
        }

        override fun onAnimationRepeat(animation: Animation?) {
            onRepeat?.invoke(animation)
        }
    })
}
