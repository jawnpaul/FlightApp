package ng.com.knowit.flightapp.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ng.com.knowit.flightapp.db.AirportDatabase.DatabaseProvider.getDatabase
import ng.com.knowit.flightapp.db.AirportRepository
import ng.com.knowit.flightapp.model.Airport
import ng.com.knowit.flightapp.utility.utility
import java.io.IOException


class AirportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AirportRepository(getDatabase(application))


    //nirtefefyu@enayu.com

    fun getAllAirports(): LiveData<List<Airport>> {
        return repository.getAllAirport()
    }

    fun getAllAirportList(): List<Airport> {
        return repository.getAllAirportList()
    }

    fun getAirportsMatchingQuery(query: String): LiveData<List<Airport>> {
        return repository.getAllAirportMatchingQuery(query)
    }


    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        if (utility.isOnline(application)) {
            getDataFromRepository()
        } else {
            Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_SHORT)
                .show()
        }

    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    private fun getDataFromRepository() {
        viewModelScope.launch {
            try {
                repository.getAirportsFromApi()

            } catch (networkError: IOException) {

                Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun getAirportCode(search: String): String {
        return repository.getAirportCode(search).toString()
    }

    fun getIsFetching(): LiveData<Boolean> {

        val currentBooleanValue: MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>()
        }
        currentBooleanValue.value = repository.getAllAirportList().isEmpty()

        return currentBooleanValue
    }
}