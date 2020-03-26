package ng.com.knowit.flightapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ScheduleResponse(scheduleResource: ScheduleResource) {
    @SerializedName("ScheduleResource")
    @Expose
    var scheduleResource: ScheduleResource = ScheduleResource()
}

class ScheduleResource {
    @SerializedName("Schedule")
    var schedule: List<Schedule> = emptyList()
}

class Schedule {
    @SerializedName("TotalJourney")
    var totalJourney: TotalJourney = TotalJourney()

    @SerializedName("Flight")
    var flights: List<Flight> = emptyList()

    fun hasMultipleStops() = flights.size > 1
}

class TotalJourney {
    @SerializedName("Duration")
    var duration: String = ""
}

class Flight {
    @SerializedName("Departure")
    var departure: Departure = Departure()

    @SerializedName("Arrival")
    var arrival: Arrival = Arrival()

    @SerializedName("MarketingCarrier")
    var marketingCarrier: MarketingCarrier = MarketingCarrier()

    @SerializedName("Details")
    var details: Details = Details()
}

class Departure {
    @SerializedName("ScheduledTimeLocal")
    var scheduledTimeLocal: ScheduledTimeLocal = ScheduledTimeLocal()

    @SerializedName("AirportCode")
    var airportCode: String = ""


}

class ScheduledTimeLocal {
    @SerializedName("DateTime")
    val dateTime: String = ""
}

class Arrival {

    @SerializedName("AirportCode")
    var airportCode: String = ""

    @SerializedName("ScheduledTimeLocal")
    var scheduledTimeLocal: ScheduledTimeLocal = ScheduledTimeLocal()

}

class MarketingCarrier {
    @SerializedName("AirlineID")
    val airlineId: String = ""

    @SerializedName("FlightNumber")
    val flightNumber: String = ""
}

class Details {
    @SerializedName("Stops")
    var stops: Stops = Stops()
}

class Stops {
    @SerializedName("StopQuantity")
    val stopQuantity: Int = Int.MIN_VALUE

}

class Location : Serializable {
    @SerializedName("Latitude")
    @Expose
    var latitude: Double? = Double.MIN_VALUE

    @SerializedName("Longitude")
    @Expose
    var longitude: Double? = Double.MIN_VALUE

}