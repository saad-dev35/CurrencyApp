package com.playaxis.currencyapp.core.base

import com.playaxis.currencyapp.utils.NetworkResult
import okio.IOException
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException

open class BaseRepository {

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (ex: SocketTimeoutException) {
            ex.printStackTrace()
            NetworkResult.Error("Request timeout, please try again later.")
        } catch (ex: IOException) {
            ex.printStackTrace()
            NetworkResult.Error(ex.message)
        } catch (ex: Exception) {
            ex.printStackTrace()
            NetworkResult.Error("Unexpected error occurred: ${ex.localizedMessage}")
        }
    }

    // Function to dynamically parse error messages from response
    private fun <T : Any> parseErrorMessage(response: Response<T>): String {
        return try {
            response.errorBody()?.let { errorBody ->
                val errorJson = JSONObject(errorBody.charStream().readText())
                when {
                    errorJson.has("message") -> errorJson.getString("message")
                    errorJson.has("error") -> errorJson.getString("error")
                    errorJson.has("errors") -> errorJson.getJSONArray("errors").join(", ")
                    else -> "Unknown error occurred"
                }
            } ?: "Unknown error with no details"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error occurred but failed to parse the error message."
        }
    }
}