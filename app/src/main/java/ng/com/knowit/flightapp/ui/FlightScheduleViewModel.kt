package ng.com.knowit.flightapp.ui

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ng.com.knowit.flightapp.db.FlightScheduleDatabase.DatabaseProvider.getDatabase
import ng.com.knowit.flightapp.db.FlightScheduleRepository
import ng.com.knowit.flightapp.model.FlightSchedule
import ng.com.knowit.flightapp.utility.SharedPreference
import java.io.IOException

class FlightScheduleViewModel(
    application: Application,
    origin: String,
    destination: String,
    date: String
) : AndroidViewModel(application) {

    private val repository = FlightScheduleRepository(getDatabase(application))

    private val origin = origin
    private val destination = destination
    private val date = date

    fun getAllFlightSchedule(flightScheduleIdentifier: String): LiveData<List<FlightSchedule>> {
        return repository.getAllFlightSchedule(flightScheduleIdentifier)
    }

    val progressBar = ProgressBar(application)
    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        progressBar.visibility = View.VISIBLE
        getDataFromApi()
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    private fun getDataFromApi() {


        viewModelScope.launch {
            try {

                val tokenToBeUsed: String?
                val sharedPreference = SharedPreference(getApplication())


                //Token has expired
                if (System.currentTimeMillis() > sharedPreference.getTokenExpiryDate()) {

                    tokenToBeUsed =
                        repository.generateToken("yr46gzu4nsyqheepzpewm8ct", "rht2PAx8eH")

                    sharedPreference.saveToken(tokenToBeUsed)

                    sharedPreference.saveTokenExpiryDate(System.currentTimeMillis().plus(21600000L))

                } else {
                    //use saved token to make api call
                    tokenToBeUsed = sharedPreference.getTokenValue()
                }
                Log.d("FSViewmodel", tokenToBeUsed!!)

                repository.getFlightScheduleFromApi(origin, destination, date, tokenToBeUsed)

                progressBar.visibility = View.GONE
                viewModelJob.cancel()

                Log.d("FSVM", "Job cancelled")

            } catch (networkError: IOException) {
                // Show a Toast error message.

                progressBar.visibility = View.GONE
                //For some reasons, this guy isn't always called
                Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
                Log.d("FSVM", "No internet connection")

            }
        }

    }

    fun getIsFetching(identifier: String): LiveData<Boolean> {

        val currentBooleanValue: MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>()
        }
        currentBooleanValue.value = repository.getAllFlightScheduleList(identifier).isEmpty()

        return currentBooleanValue
    }

}