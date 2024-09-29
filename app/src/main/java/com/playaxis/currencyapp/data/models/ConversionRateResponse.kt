package com.playaxis.currencyapp.data.models

import com.google.gson.annotations.SerializedName

data class ConversionRateResponse(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("conversion_result")
	val conversionResult: Any? = null,

	@field:SerializedName("time_next_update_unix")
	val timeNextUpdateUnix: Int? = null,

	@field:SerializedName("target_code")
	val targetCode: String? = null,

	@field:SerializedName("time_next_update_utc")
	val timeNextUpdateUtc: String? = null,

	@field:SerializedName("documentation")
	val documentation: String? = null,

	@field:SerializedName("time_last_update_unix")
	val timeLastUpdateUnix: Int? = null,

	@field:SerializedName("base_code")
	val baseCode: String? = null,

	@field:SerializedName("time_last_update_utc")
	val timeLastUpdateUtc: String? = null,

	@field:SerializedName("terms_of_use")
	val termsOfUse: String? = null,

	@field:SerializedName("conversion_rate")
	val conversionRate: Any? = null
)
