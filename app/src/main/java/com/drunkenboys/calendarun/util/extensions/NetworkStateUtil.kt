package com.drunkenboys.calendarun.util.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun Context.getNetworkConnectState(): Boolean {
    var isConnected = false
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (capabilities != null) {
            isConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
    } else {
        val activeNetwork = cm.activeNetworkInfo
        isConnected = activeNetwork?.isConnectedOrConnecting == true
    }
    return isConnected
}
