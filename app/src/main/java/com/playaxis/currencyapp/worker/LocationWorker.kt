package com.playaxis.currencyapp.worker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class LocationWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context,workerParams) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun doWork(): Result {
        // Check location permissions
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        try {
            // Request location updates
            val locationRequest = LocationRequest.Builder( 5000L)
                .build()

            val location = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()

            // Fetch periodic location updates
            fetchLocationUpdates(locationRequest)

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }

        // Schedule next execution
        return Result.success()

    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchLocationUpdates(locationRequest: LocationRequest) =
        suspendCancellableCoroutine { continuation ->
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        updateLocation(location)
                    }
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                // Let coroutine stay alive to allow continuous updates
                continuation.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            } catch (e: Exception) {
                continuation.resume(Unit)
            }
        }

    private fun updateLocation(location: Location?) {
        if (location != null) {
            // Send the location data to the UI or a backend server
            // For example, save it to a shared database or emit via a LiveData
            println("Location: Latitude = ${location.latitude}, Longitude = ${location.longitude}")
        }
    }
}