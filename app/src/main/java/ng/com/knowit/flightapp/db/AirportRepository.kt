package ng.com.knowit.flightapp.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ng.com.knowit.flightapp.api.ApiInterface
import ng.com.knowit.flightapp.model.Airport


class AirportRepository(private val database: AirportDatabase) {

    private val isFetching = MutableLiveData<Boolean>()

    fun getAllAirport(): LiveData<List<Airport>> {
        return database.airportDao().getAllAirport()
    }

    fun getAllAirportList(): List<Airport> {
        return database.airportDao().getAllAirportList()
    }

    fun getAllAirportMatchingQuery(query: String): LiveData<List<Airport>> {
        return database.airportDao().findAirportByName("%$query%")
    }

    fun getAirportCode(query: String): String? {
        return database.airportDao().getSingleAirport("%$query%")[0].airportIataCode
    }

    suspend fun getAirportsFromApi() {
        withContext(Dispatchers.IO) {
            //I need to make this API call once
            if (database.airportDao().getAllAirportList().isEmpty()) {

                Log.d("AirportRepo", "Airports is called")
                val airportList = ApiInterface.AirportNetwork.apiInterface.getAirports().await()
                Log.d("AirportRepo", airportList.size().toString())

                for (item in airportList) {

                    val locationName = item.asJsonObject.get("locationName")?.asString
                    val location = item.asJsonObject.get("location")?.toString()
                    val country = item.asJsonObject.get("country")?.asString
                    val iatacode = item.asJsonObject.get("iataCode")?.asString

                    val airport = Airport(locationName, location, country, iatacode, null, null)

                    database.airportDao().insert(airport)


                }
            }


        }
    }
}
