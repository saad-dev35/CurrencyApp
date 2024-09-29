package com.playaxis.currencyapp.ui.history_screen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playaxis.currencyapp.data.repositories.CurrencyRepository
import com.playaxis.currencyapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HistoryVm @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    //livedata for exchange rates
    private val _historyRates: MutableLiveData<NetworkResult<List<Double>>> by lazy { MutableLiveData() }
    val historyRates: LiveData<NetworkResult<List<Double>>> get() = _historyRates


    fun getHistoryRates(currencyCode: String) {
        viewModelScope.launch {
            _historyRates.postValue(NetworkResult.Loading())
            val today = LocalDate.now().minusDays(1)
            val entries = mutableListOf<Double>()

            try {
                //getting straight line in chart no changes in currency rate
                for (i in 1..12){
                    val date = today.minusDays(1)
                    val response =
                        repository.getHistoricalRates(currencyCode = currencyCode, date = date.toString())
                    response.data?.data?.let { dataMap ->
                        dataMap[currencyCode]?.let { currencyRate ->
                            entries.add(currencyRate.value!!)
                        }
                    }
                }
                _historyRates.postValue(NetworkResult.Success(entries))
            }catch (e: Exception){
                _historyRates.postValue(NetworkResult.Error("Failed to fetch data"))
                Log.e("History VM", "getHistoryRates: ${e.message}" )
            }
        }

    }

}