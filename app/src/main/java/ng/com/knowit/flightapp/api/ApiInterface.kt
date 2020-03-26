package ng.com.knowit.flightapp.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiInterface {

    @GET("airports")
    fun getAirports(): Deferred<JsonArray>

    @FormUrlEncoded
    @POST("oauth/token")
    fun synchronousLogin(@FieldMap hashMap: HashMap<String, @JvmSuppressWildcards Any>): Deferred<JsonObject>

    @GET("operations/schedules/{origin}/{destination}/{fromDateTime}")
    fun getFlights(
        @Path("origin") origin: String, @Path("destination") destination: String,
        @Path("fromDateTime") fromDateTime: String
    ): Deferred<Response<JsonObject>>

    @GET("mds-references/airports/{airportCode}")
    fun getAirportLocationFromApi(@Path("airportCode") airportCode: String): Deferred<JsonObject>


    object AirportNetwork {

        // Configure retrofit to parse JSON and use coroutines
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.easypnr.com/v4/")
            .client(attachAirportTokenToRetrofit("gEtxumGvzoAqVRTfbxGbZuzZRFymLx"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        fun attachAirportTokenToRetrofit(accessToken: String): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor { chain: Interceptor.Chain ->
                    val request: Request = chain.request().newBuilder()
                        .addHeader("X-Api-Key", accessToken).build()
                    chain.proceed(request)
                }.build()
        }


    }

    object FlightScheduleNetwork {

        fun createRetrofit(client: OkHttpClient): Retrofit {

            return Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        }

    }


    object TokenGenerator {

        private val httpClientBuilder = OkHttpClient.Builder()
        // Configure retrofit to parse JSON and use coroutines
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.lufthansa.com/v1/")
            .client(httpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

    }

    object AirportLocationNetwork {

        fun createRetrofit(client: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        }

    }

}