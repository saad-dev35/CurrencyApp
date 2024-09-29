package com.playaxis.currencyapp.data.models

import com.google.gson.annotations.SerializedName

data class HistoryRatesResponse(

	@field:SerializedName("data")
	val data: Map<String, CurrencyRate>? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null
)

data class CurrencyRate(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("value")
	val value: Double? = null
)

data class Meta(

	@field:SerializedName("last_updated_at")
	val lastUpdatedAt: String? = null
)