package com.playaxis.currencyapp.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.playaxis.currencyapp.data.entities.ExchangeRateEntity

@Database(
    entities = [ExchangeRateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDB : RoomDatabase() {

    abstract fun exchangeRateDao(): ExchangeRateDao

}