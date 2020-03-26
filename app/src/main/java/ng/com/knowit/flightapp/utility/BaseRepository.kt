package ng.com.knowit.flightapp.utility

import android.util.Log
import com.google.gson.JsonObject
import retrofit2.Response
import java.io.IOException

open class BaseRepository {

    suspend fun <T : JsonObject> safeApiCall(
        call: suspend () -> Response<JsonObject>,
        errorMessage: String
    ): JsonObject? {

        val result: Result<JsonObject> = safeApiResult<JsonObject>(call, errorMessage)
        var data: JsonObject? = null

        when (result) {
            is Result.Success ->
                data = result.data
            is Result.Error -> {

                Log.d("1.DataRepository", "$errorMessage & Exception - ${result.exception}")
            }
        }


        return data

    }

    private suspend fun <T : JsonObject> safeApiResult(
        call: suspend () -> Response<JsonObject>,
        errorMessage: String
    ): Result<JsonObject> {
        val response = call.invoke()
        if (response.isSuccessful)
            return Result.Success(response.body()!!)

        return Result.Error(IOException("Error Occurred during getting safe Api result, Custom ERROR - $errorMessage"))
    }
}