package com.example.data.network

import retrofit2.Response

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val code: Int, val message: String?, val exception: Throwable? = null) : NetworkResult<Nothing>
    data class Exception(val exception: Throwable) : NetworkResult<Nothing>
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.code(), "Response body was null")
            }
        } else {
            NetworkResult.Error(response.code(), response.message() ?: "Unknown API error")
        }
    } catch (e: Exception) {
        NetworkResult.Exception(e)
    }
}
