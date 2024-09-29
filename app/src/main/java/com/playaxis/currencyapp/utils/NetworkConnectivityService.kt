package com.playaxis.currencyapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class NetworkConnectivityService private constructor() {
    private lateinit var wifiManager: WifiManager
    private lateinit var connectivityManager: ConnectivityManager

    private var isConnectedToNetwork: Boolean = false
    private var hasInternetAccess: Boolean = false

    companion object {
        val instance: NetworkConnectivityService by lazy { NetworkConnectivityService() }
    }

    // Initialize with application context, only once
    fun initializeWithApplicationContext(context: Context) {
        if (!this::connectivityManager.isInitialized) {
            wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            CoroutineScope(Dispatchers.IO).launch {
                registerNetworkCallback()
            }
        }
    }

    // Register for network change callbacks for real-time connectivity updates
    private suspend fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    isConnectedToNetwork = true
                    Log.e("NetworkService", "Network is available")
                    CoroutineScope(Dispatchers.IO).launch {
                        checkInternetConnection()
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    isConnectedToNetwork = false
                    hasInternetAccess = false
                    Log.e("NetworkService", "Network is lost")
                }
            }
        )
    }

    // Check if there is active internet access (connected but no internet case)
    private suspend fun checkInternetConnection() {
        // Perform a background operation to verify internet access
        withContext(Dispatchers.IO) {
            val urlList = listOf(
                "http://clients3.google.com/generate_204",
                "http://www.google.com",
                "http://www.example.com"
            )

            hasInternetAccess = false
            for (urlString in urlList) {
                try {
                    val url = URL(urlString)
                    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("User-Agent", "Android")
                    urlConnection.setRequestProperty("Connection", "close")
                    urlConnection.connectTimeout = 3000 // Increased timeout
                    urlConnection.connect()
                    if (urlConnection.responseCode == 204 || urlConnection.responseCode == 200) {
                        hasInternetAccess = true
                        break
                    }
                } catch (e: IOException) {
                    Log.e("NetworkService", "Error checking internet access with $urlString", e)
                }
            }

            Log.e("NetworkService", "Internet access: $hasInternetAccess")
        }
    }

    // Helper to check if the device is online and has internet access
    fun isOnline(): Pair<Boolean, String> {
        return when {
            !isConnectedToNetwork -> Pair(false, "No internet connection available.")
            !hasInternetAccess -> Pair(false, "No internet access.")
            else -> Pair(true, "Connected to the internet.")
        }
    }

    // Additional helper to check if only connected to a local network (without internet)
    fun isConnectedToLocalNetwork(): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    // For Wi-Fi, we can check more details (like signal strength) if needed
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}