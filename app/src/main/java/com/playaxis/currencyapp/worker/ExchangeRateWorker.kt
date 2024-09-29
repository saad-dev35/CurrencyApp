package com.playaxis.currencyapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.playaxis.currencyapp.data.repositories.CurrencyRepository
import com.playaxis.currencyapp.utils.Constants
import com.playaxis.currencyapp.utils.NetworkResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ExchangeRateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CurrencyRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val baseCurrency = inputData.getString("base_currency") ?: "USD"
            val result = repository.getExchangeRates(base = baseCurrency, Constants.API_KEY)
            if (result is NetworkResult.Success) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}