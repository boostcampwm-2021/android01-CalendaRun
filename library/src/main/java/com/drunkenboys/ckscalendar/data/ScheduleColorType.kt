package com.drunkenboys.ckscalendar.data

import com.drunkenboys.ckscalendar.utils.TimeUtils

enum class ScheduleColorType(val color: Int) {
    RED(TimeUtils.getColorInt(220, 34, 39)),
    ORANGE(TimeUtils.getColorInt(255, 113, 67)),
    YELLOW(TimeUtils.getColorInt(254, 193, 7)),
    GREEN(TimeUtils.getColorInt(139, 194, 74)),
    BLUE(TimeUtils.getColorInt(75, 181, 222)),
    NAVY(TimeUtils.getColorInt(3, 169, 245)),
    PURPLE(TimeUtils.getColorInt(156, 40, 177)),
    GRAY(TimeUtils.getColorInt(225, 225, 225)),
    CYAN(TimeUtils.getColorInt(71, 214, 220)),
    MAGENTA(TimeUtils.getColorInt(255, 184, 120))
}