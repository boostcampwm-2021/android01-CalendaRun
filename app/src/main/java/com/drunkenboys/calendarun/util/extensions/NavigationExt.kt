package com.drunkenboys.calendarun.util.extensions

import androidx.navigation.NavController
import androidx.navigation.NavDirections

/**
 * NavController의 현재 destination에서 해당 action으로
 * 이동할 수 있는 destination이 있는지 확인 후 navigate.
 */
fun NavController.navigateSafe(action: NavDirections) {
    currentDestination?.getAction(action.actionId) ?: return
    navigate(action)
}
