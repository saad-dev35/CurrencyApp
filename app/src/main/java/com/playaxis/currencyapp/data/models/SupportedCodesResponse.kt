package com.playaxis.currencyapp.data.models

import com.google.gson.annotations.SerializedName

data class SupportedCodesResponse(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("documentation")
	val documentation: String? = null,

	@field:SerializedName("supported_codes")
	val supportedCodes: List<List<String?>?>? = null,

	@field:SerializedName("terms_of_use")
	val termsOfUse: String? = null
)
