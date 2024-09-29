package com.playaxis.currencyapp.data.data_source.remote

import com.playaxis.currencyapp.data.models.ConversionRateResponse
import com.playaxis.currencyapp.data.models.ExchangeRateResponse
import com.playaxis.currencyapp.data.models.HistoryRatesResponse
import com.playaxis.currencyapp.data.models.SupportedCodesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CurrencyService {

    @GET("latest/{base_code}")
    suspend fun getExchangeRates(
        @Path("base_code") baseCode: String,
        @Query("api_key") apiKey: String
    ): Response<ExchangeRateResponse>

    @GET("pair/{base_code}/{target_code}/{amount}")
    suspend fun getConversionRate(
        @Path("base_code") baseCode: String,
        @Path("target_code") targetCode: String,
        @Path("amount") amount: Float
    ): Response<ConversionRateResponse>

    @GET("codes")
    suspend fun getSupportedCodes(): Response<SupportedCodesResponse>

    @GET()
    suspend fun getHistoricalRates(
        @Url url: String,
        @Query("apikey") apiKey: String,
        @Query("currencies") currencyCode: String,
        @Query("date") date: String
    ): Response<HistoryRatesResponse>

}