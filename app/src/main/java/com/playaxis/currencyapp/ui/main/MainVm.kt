package com.playaxis.currencyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playaxis.currencyapp.data.models.SupportedCodesResponse
import com.playaxis.currencyapp.data.repositories.CurrencyRepository
import com.playaxis.currencyapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVm @Inject constructor(
    private val repository: CurrencyRepository
): ViewModel() {

    //live data for supported codes
    private val _supportedCodes: MutableLiveData<NetworkResult<SupportedCodesResponse>> by lazy { MutableLiveData() }
    val supportedCodes: LiveData<NetworkResult<SupportedCodesResponse>> get() = _supportedCodes

    init {
        getSupportedCodes()
    }

    private fun getSupportedCodes() {
        viewModelScope.launch {
            _supportedCodes.postValue(NetworkResult.Loading())
            val result = repository.getSupportedCodes()
            _supportedCodes.postValue(result)
        }
    }

}