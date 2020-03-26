package ng.com.knowit.flightapp.activities

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ng.com.knowit.flightapp.R
import ng.com.knowit.flightapp.api.ApiInterface
import ng.com.knowit.flightapp.databinding.ActivityFlightScheduleBinding
import ng.com.knowit.flightapp.db.AirportDatabase
import ng.com.knowit.flightapp.model.*
import ng.com.knowit.flightapp.recycleradapters.FlightScheduleAdapter
import ng.com.knowit.flightapp.ui.FlightScheduleViewModel
import ng.com.knowit.flightapp.ui.FlightScheduleViewModelFactory
import ng.com.knowit.flightapp.utility.CustomDialog
import ng.com.knowit.flightapp.utility.SharedPreference
import ng.com.knowit.flightapp.utility.convertTo
import ng.com.knowit.flightapp.utility.utility
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.Serializable

class FlightScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlightScheduleBinding

    private lateinit var flightScheduleViewModel: FlightScheduleViewModel

    private lateinit var flightScheduleViewModelFactory: FlightScheduleViewModelFactory


    private var origin: String? = ""
    private var destination: String? = ""
    private var date: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_flight_schedule
        )

        val intent = intent
        if (intent != null) {
            origin = intent.getStringExtra("originAirportCode")
            destination = intent.getStringExtra("destinationAirportCode")
            date = intent.getStringExtra("date")
        }


        val progressBar = CustomDialog(this, false)

        binding.flightScheduleRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        flightScheduleViewModelFactory =
            FlightScheduleViewModelFactory(this.application, origin!!, destination!!, date!!)

        val identifier = "$origin-$destination"

        flightScheduleViewModel = ViewModelProviders.of(this, flightScheduleViewModelFactory)
            .get(FlightScheduleViewModel::class.java)


        /*flightScheduleViewModel.getIsFetching().observe(this, Observer { value->

            if(value == true){
                progressBar.show()

            } else{

                progressBar.dismiss()
            }

        })*/

        flightScheduleViewModel.getAllFlightSchedule(identifier)
            .observe(this, Observer<List<FlightSchedule>> { flightList ->

                val adapter = FlightScheduleAdapter(
                    scheduleList(flightList),
                    this
                ) { schedule -> this.selectedSchedule(schedule) }

                binding.flightScheduleRecyclerView.adapter = adapter

            })

    }

    private fun selectedSchedule(schedule: Schedule) {

        val intent = Intent(this, MapsActivity::class.java)
        intent.setClass(this, MapsActivity::class.java)

        intent.putExtra("list", getLocationList(schedule.flights) as Serializable)
        startActivity(intent)
    }


    private fun getLocationList(list: List<Flight>): List<Airport> {
        val airportList = mutableListOf<Airport>()
        val airportDao = AirportDatabase.DatabaseProvider.getDatabase(this).airportDao()

        val sharedPreference = SharedPreference(this)

        for (item in list) {
            val departureCode = item.departure.airportCode
            val arrivalCode = item.arrival.airportCode



            GlobalScope.launch {

                try {

                    if (utility.isOnline(this@FlightScheduleActivity)) {


                        getAirportLocation(departureCode, sharedPreference.getTokenValue()!!)
                        getAirportLocation(arrivalCode, sharedPreference.getTokenValue()!!)

                    }


                } catch (e: NetworkErrorException) {

                    Log.d("FSA", e.toString())
                }

            }


            //get airport on db matching these codes
            val departureAirport = airportDao.getAirportByIataCode(departureCode)
            val arrivalAirport = airportDao.getAirportByIataCode(arrivalCode)

            airportList.add(departureAirport)
            airportList.add(arrivalAirport)

        }

        return airportList
    }

    suspend fun getAirportLocation(airportCode: String, accessToken: String) {


        fun attachScheduleTokenToRetrofit(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                .addInterceptor { chain: Interceptor.Chain ->
                    val request: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken).build()

                    chain.proceed(request)

                }.build()

            return builder
        }

        val apiInterface = ApiInterface.AirportLocationNetwork
        val apiCall = apiInterface.createRetrofit(attachScheduleTokenToRetrofit())
            .create(ApiInterface::class.java)


        val response = apiCall.getAirportLocationFromApi(airportCode).await()

        val obj = response.get("AirportResource")
            ?.asJsonObject?.get("Airports")
            ?.asJsonObject?.get("Airport")
            ?.asJsonObject?.get("Position")
            ?.asJsonObject?.get("Coordinate")
            ?.asJsonObject


        val lat = obj?.get("Latitude")?.asDouble
        val lon = obj?.get("Longitude")?.asDouble

        val location = Location()
        location.longitude = lon
        location.latitude = lat

        val airportDao =
            AirportDatabase.DatabaseProvider.getDatabase(this@FlightScheduleActivity).airportDao()
        val airport = airportDao.getAirportByIataCode(airportCode)
        airport.airportLatitude = location.latitude
        airport.airportLongitude = location.longitude
        airportDao.update(airport)

        Log.d("FSACt", "Airport Updated")


    }

    private fun scheduleList(fsList: List<FlightSchedule>): List<Schedule> {
        val list = mutableListOf<Schedule>()

        for (item in fsList) {

            val scheduleResource = scheduleResource(item)
            val schedule = singleSchedule(scheduleResource)

            for (scheduleItem in schedule) {
                list.add(scheduleItem)
            }
        }

        return list

    }

    private fun scheduleResource(flightSchedule: FlightSchedule): ScheduleResource {
        val gson = Gson()
        val jsonObject = gson.fromJson(flightSchedule.data, JsonObject::class.java).asJsonObject

        return jsonObject.convertTo(ScheduleResource::class.java) ?: ScheduleResource()
    }

    private fun singleSchedule(scheduleResource: ScheduleResource): List<Schedule> {
        return scheduleResource.schedule

    }
}
