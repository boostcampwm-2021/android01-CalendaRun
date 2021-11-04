package com.drunkenboys.ckscalendar.utils

object TimeUtils {
    fun getColorInt(R: Int, G: Int, B: Int): Int {
        return 0xff shl 24 or (R and 0xff shl 16) or (G and 0xff shl 8) or (B and 0xff)
    }
}