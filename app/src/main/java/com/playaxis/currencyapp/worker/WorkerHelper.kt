package com.playaxis.currencyapp.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val WORK_NAME = "ExchangeRateWorker"
        private const val LOCATION_WORK_NAME = "LocationWorker"
        private const val BASE_CURRENCY_KEY = "base_currency"
    }

    //setting constraints
    private fun getConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()
    }

    fun schedulePeriodicWork(baseCurrency: String, repeatInterval: Long = 15) {
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            ExchangeRateWorker::class.java,
            repeatInterval, TimeUnit.MINUTES
        )
            .setInputData(workDataOf(BASE_CURRENCY_KEY to baseCurrency))
            .setConstraints(getConstraints())
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    fun cancelScheduledWork() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    // Schedule LocationWorker as a one-time worker
    fun startLocationUpdates() {
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(LocationWorker::class.java)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            LOCATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }

    // Stop LocationWorker
    fun stopLocationUpdates() {
        WorkManager.getInstance(context).cancelUniqueWork(LOCATION_WORK_NAME)
    }
}