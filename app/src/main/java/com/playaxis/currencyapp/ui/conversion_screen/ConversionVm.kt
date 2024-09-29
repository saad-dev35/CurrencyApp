package com.playaxis.currencyapp.ui.conversion_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playaxis.currencyapp.data.models.ConversionRateResponse
import com.playaxis.currencyapp.data.repositories.CurrencyRepository
import com.playaxis.currencyapp.utils.NetworkConnectivityService
import com.playaxis.currencyapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversionVm @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    //live data for conversion response
    private val _conversionRate: MutableLiveData<NetworkResult<ConversionRateResponse>> by lazy { MutableLiveData() }
    val conversionRate: LiveData<NetworkResult<ConversionRateResponse>> get() = _conversionRate

    fun getConversionRate(base: String, target: String, amount: String) {
        viewModelScope.launch {
            _conversionRate.postValue(NetworkResult.Loading())
            val (isOnline, message) = NetworkConnectivityService.instance.isOnline()
            if (isOnline) {
                val result = repository.getConversionRate(base, target, amount)
                _conversionRate.postValue(result)
            } else {
                val rate = repository.getRateByCurrencyCode(target).rate
                val conversionRate = rate * amount.toFloat()
                _conversionRate.postValue(
                    NetworkResult.Success(
                        ConversionRateResponse(
                            conversionRate = rate,
                            conversionResult = conversionRate.toString()
                        )
                    )
                )
            }
        }
    }

}