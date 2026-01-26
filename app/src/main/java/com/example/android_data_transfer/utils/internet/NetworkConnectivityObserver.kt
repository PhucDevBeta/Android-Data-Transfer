package com.example.android_data_transfer.utils.internet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


class NetworkConnectivityObserver @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @SuppressLint("MissingPermission")
    override fun observe(): Flow<ConnectivityObserver.Status> = callbackFlow @RequiresPermission(
        Manifest.permission.ACCESS_NETWORK_STATE
    ) {
        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                launch {
                    val status = checkInternetAccess()
                    trySend(status).isSuccess
                }
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                launch {
                    trySend(ConnectivityObserver.Status.Losing).isSuccess
                }
            }

            override fun onLost(network: Network) {
                launch {
                    trySend(ConnectivityObserver.Status.Lost).isSuccess
                }
            }

            override fun onUnavailable() {
                launch {
                    trySend(ConnectivityObserver.Status.Unavailable).isSuccess
                }
            }
        }

        try {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } catch (e: Exception) {
            //Timber.e("Too many network callbacks: ${e.message}")
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    override suspend fun checkInternetAccess(): ConnectivityObserver.Status =
        withContext(Dispatchers.IO) {
            val testUrls = listOf(
                "http://clients3.google.com/generate_204",
                "https://www.google.com/generate_204",
                "https://connectivitycheck.gstatic.com/generate_204"
            )

            for (testUrl in testUrls) {
                var connection: HttpURLConnection? = null
                try {
                    connection = URL(testUrl).openConnection() as HttpURLConnection
                    connection.setRequestProperty("User-Agent", "Android")
                    connection.setRequestProperty("Connection", "close")
                    connection.connectTimeout = 1500
                    connection.readTimeout = 1500
                    connection.connect()

                    val code = connection.responseCode
                    // Timber.d("Connectivity check: $testUrl -> $code")

                    if (code == 204) {
                        //  Timber.d("Internet access verified via $testUrl ")
                        return@withContext ConnectivityObserver.Status.Available
                    }
                } catch (e: Exception) {
                    //Timber.w("Connectivity check failed: $testUrl (${e.message})")
                } finally {
                    connection?.disconnect()
                }
            }

            //Timber.d("All connectivity checks failed ")
            ConnectivityObserver.Status.Unavailable
        }


}

