package com.playaxis.currencyapp.ui.home_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playaxis.currencyapp.data.mappers.toExchangeRateEntity
import com.playaxis.currencyapp.data.models.ExchangeRate
import com.playaxis.currencyapp.data.repositories.CurrencyRepository
import com.playaxis.currencyapp.utils.Constants
import com.playaxis.currencyapp.utils.NetworkConnectivityService
import com.playaxis.currencyapp.utils.NetworkResult
import com.playaxis.currencyapp.worker.WorkerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVm @Inject constructor(
    private val repository: CurrencyRepository,
    private val workerHelper: WorkerHelper
) : ViewModel() {

    //livedata for exchange rates
    private val _exchangeRates: MutableLiveData<NetworkResult<List<ExchangeRate>>> by lazy { MutableLiveData() }
    val exchangeRates: LiveData<NetworkResult<List<ExchangeRate>>> get() = _exchangeRates

    init {
        workerHelper.startLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        workerHelper.stopLocationUpdates()
    }

    fun getExchangeRates(baseCode: String) {
        viewModelScope.launch {
            _exchangeRates.postValue(NetworkResult.Loading())

            val (isOnline, message) = NetworkConnectivityService.instance.isOnline()
            if (isOnline){
                val rates = repository.getExchangeRates(base = baseCode, apiKey = Constants.API_KEY)
                val exchangeRates = rates.data?.conversionRates?.map { (currencyCode, rate) ->
                    ExchangeRate(currencyCode = currencyCode, rate = rate)
                }
                if (rates is NetworkResult.Success) {
                    _exchangeRates.postValue(NetworkResult.Success(exchangeRates))
                } else {
                    _exchangeRates.postValue(NetworkResult.Error(rates.message))
                }

                //saving rates locally
                val rateEntities = rates.data?.conversionRates?.map { (currencyCode, rate) ->
                    ExchangeRate(currencyCode = currencyCode, rate = rate).toExchangeRateEntity()
                } ?: emptyList()
                repository.insertExchangeRateToDb(rateEntities)

                //schedule periodic work
                schedulePeriodicExchangeRateFetch(baseCurrency = baseCode)
            }else {
                val rates = repository.getExchangeRateFromDb()
                if (rates.isNotEmpty()) {
                    val exchangeRates = rates.map { rateEntity ->
                        ExchangeRate(currencyCode = rateEntity.currencyCode, rate = rateEntity.rate)
                    }
                    _exchangeRates.postValue(NetworkResult.Success(exchangeRates))
                }else {
                    _exchangeRates.postValue(NetworkResult.Error("No Record Found"))
                }

            }
        }
    }

    private fun schedulePeriodicExchangeRateFetch(baseCurrency: String) {
        workerHelper.schedulePeriodicWork(baseCurrency)
    }

}