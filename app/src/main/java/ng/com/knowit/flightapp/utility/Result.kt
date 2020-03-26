package ng.com.knowit.flightapp.utility

import com.google.gson.JsonObject

sealed class Result<out T : JsonObject> {
    data class Success<out T : JsonObject>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}