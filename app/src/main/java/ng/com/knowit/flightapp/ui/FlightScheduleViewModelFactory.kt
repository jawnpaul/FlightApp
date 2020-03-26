package ng.com.knowit.flightapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class FlightScheduleViewModelFactory(
    val application: Application,
    val origin: String,
    val destination: String,
    val date: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightScheduleViewModel::class.java)) {
            return FlightScheduleViewModel(application, origin, destination, date) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}