package com.playaxis.currencyapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rate")
data class ExchangeRateEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val currencyCode: String,
    val rate: Double,
)
