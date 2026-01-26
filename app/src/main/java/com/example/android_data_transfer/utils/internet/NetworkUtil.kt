package com.example.android_data_transfer.utils.internet

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkConnected(context: Context): Boolean {
    if (context.getSystemService(Context.CONNECTIVITY_SERVICE) == null) {
        // when no internet
        return false
    }
    val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivity.activeNetwork ?: return false
    val activeNetwork = connectivity.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

