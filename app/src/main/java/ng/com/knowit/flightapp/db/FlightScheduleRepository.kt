package ng.com.knowit.flightapp.db

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import ng.com.knowit.flightapp.api.ApiInterface
import ng.com.knowit.flightapp.model.FlightSchedule
import ng.com.knowit.flightapp.utility.BaseRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

class FlightScheduleRepository(private val database: FlightScheduleDatabase) : BaseRepository() {

    fun getAllFlightSchedule(flightScheduleIdentifier: String): LiveData<List<FlightSchedule>> {
        return database.flightScheduleDao().getAllFlightSchedule(flightScheduleIdentifier)
    }


    suspend fun getFlightScheduleFromApi(
        origin: String,
        destination: String,
        date: String,
        accessToken: String
    ) {
        fun attachScheduleTokenToRetrofit(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                .addInterceptor { chain: Interceptor.Chain ->
                    val request: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken).build()

                    Log.d("FSRepo", request.url().toString())
                    chain.proceed(request)

                }.build()

            return builder
        }

        val apiInterface = ApiInterface.FlightScheduleNetwork
        val apiCall = apiInterface.createRetrofit(attachScheduleTokenToRetrofit())
            .create(ApiInterface::class.java)


        try {
            // some code

            val response = safeApiCall<JsonObject>(
                call = { apiCall.getFlights(origin, destination, date).await() },
                errorMessage = "Error Fetching Flight Schedule"
            )

            Log.d("FlightScheduleRepo", "Flight Schedule is called")
            val gson = Gson()

            val identifier = "$origin-$destination"
            val flightScheduleDao: FlightScheduleDao = database.flightScheduleDao()

            val flightSchedule = FlightSchedule(gson.toJson(response!!.asJsonObject), identifier)

            if (flightScheduleDao.getAllFlightScheduleList().isEmpty()) {
                //If db is empty
                flightScheduleDao.insert(flightSchedule)

                Log.d("FSR", "db is empty")
            }

            if (flightScheduleDao.getSingleFlightSchedule(identifier).isEmpty() && flightScheduleDao.getAllFlightScheduleList().isNotEmpty()) {
                //record does not exist
                flightScheduleDao.insert(flightSchedule)
                Log.d("FSR", "new record inserted")
            } else {
                //record exist
                flightScheduleDao.update(flightSchedule)

                Log.d("FSR", "record updated")
            }


        } catch (e: NullPointerException) {
            // handler

            Log.e("FSR", e.toString())
        }


    }

    suspend fun generateToken(clientId: String, clientSecret: String): String? {

        val hashMap = HashMap<String, Any>()
        hashMap["client_id"] = clientId
        hashMap["client_secret"] = clientSecret
        hashMap["grant_type"] = "client_credentials"

        val token = ApiInterface.TokenGenerator.apiInterface.synchronousLogin(hashMap).await()
            .get("access_token").asString

        //save token and expiryDate

        Log.d("FlightSchedRepo", token)

        return token
    }

}



