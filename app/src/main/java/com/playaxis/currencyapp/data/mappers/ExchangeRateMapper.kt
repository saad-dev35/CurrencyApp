package com.playaxis.currencyapp.data.mappers

import com.playaxis.currencyapp.data.entities.ExchangeRateEntity
import com.playaxis.currencyapp.data.models.ExchangeRate

fun ExchangeRateEntity.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        id = id,
        currencyCode = currencyCode,
        rate = rate
    )
}

fun ExchangeRate.toExchangeRateEntity(): ExchangeRateEntity {
    return ExchangeRateEntity(
        id = id,
        currencyCode = currencyCode,
        rate = rate
    )
}