package com.playaxis.currencyapp.data.models

data class ExchangeRate(
    var id: Int = 0,
    var currencyCode: String,
    var rate: Double,
)
