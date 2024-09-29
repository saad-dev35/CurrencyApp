package com.playaxis.currencyapp.data.repositories

import com.playaxis.currencyapp.core.base.BaseRepository
import com.playaxis.currencyapp.data.data_source.local.ExchangeRateDao
import com.playaxis.currencyapp.data.data_source.remote.CurrencyService
import com.playaxis.currencyapp.data.entities.ExchangeRateEntity
import com.playaxis.currencyapp.utils.Constants
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val service: CurrencyService,
    private val dao: ExchangeRateDao
) : BaseRepository() {

    /*-------------------------------------------Remote-------------------------------------------*/

    suspend fun getExchangeRates(base: String, apiKey: String) = safeApiCall {
        service.getExchangeRates(base, apiKey)
    }

    suspend fun getConversionRate(base: String, target: String, amount: String) = safeApiCall {
        service.getConversionRate(baseCode = base, targetCode = target, amount = amount.toFloat())
    }

    suspend fun getSupportedCodes() = safeApiCall {
        service.getSupportedCodes()
    }

    suspend fun getHistoricalRates(currencyCode: String, date: String) = safeApiCall {
        service.getHistoricalRates(url = Constants.BASE_URL_2, apiKey = Constants.API_KEY_2,currencyCode = currencyCode,date=  date)
    }

    /*-------------------------------------------Local-------------------------------------------*/

    suspend fun getExchangeRateFromDb() = dao.getAllExchangeRates()

    suspend fun insertExchangeRateToDb(rates: List<ExchangeRateEntity>) = dao.insertRates(rates)

    suspend fun getRateByCurrencyCode(currencyCode: String) = dao.getRatesByCurrency(currencyCode)
}