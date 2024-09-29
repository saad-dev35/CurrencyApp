package com.playaxis.currencyapp.data.data_source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playaxis.currencyapp.data.entities.ExchangeRateEntity

@Dao
interface ExchangeRateDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<ExchangeRateEntity>)

    @Query("SELECT * FROM exchange_rate")
    suspend fun getAllExchangeRates(): List<ExchangeRateEntity>

    @Query("SELECT * FROM exchange_rate WHERE currencyCode = :currencyCode")
    suspend fun getRatesByCurrency(currencyCode: String): ExchangeRateEntity

    @Query("DELETE FROM exchange_rate")
    suspend fun clearRates()

}