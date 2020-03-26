package ng.com.knowit.flightapp.recycleradapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ng.com.knowit.flightapp.R
import ng.com.knowit.flightapp.model.Schedule

class FlightScheduleAdapter(
    val scheduleList: List<Schedule>,
    val context: Context,
    val clickListener: (schedule: Schedule) -> Unit
) : RecyclerView.Adapter<FlightScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_schedule_flight_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {

        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.schedule = scheduleList[position]

        holder.itemView.setOnClickListener {
            clickListener(scheduleList[holder.adapterPosition])
        }

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val flightScheduleAirportDepatureCodeTextView =
            itemView.findViewById(R.id.flight_schedule_departure_airport_code_text_view) as TextView
        val flightScheduleAirportArrivalCodeTextView =
            itemView.findViewById(R.id.flight_schedule_arrival_airport_code_text_view) as TextView

        val flightScheduleDepartureTimeTextView =
            itemView.findViewById(R.id.flight_schedule_departure_time_text_view) as TextView
        val flightScheduleArrivalTimeTextView =
            itemView.findViewById(R.id.flight_schedule_arrival_time_text_view) as TextView

        val flightScheduleAirlineIdTextView =
            itemView.findViewById(R.id.flight_schedule_airline_id_text_view) as TextView
        val flightScheduleFlightNumberTextView =
            itemView.findViewById(R.id.flight_schedule_flight_number_text_view) as TextView


        var schedule: Schedule = Schedule()
            set(schedule) {
                field = schedule

                if (schedule.hasMultipleStops()) {
                    val departureFlight = schedule.flights.first()
                    val arrivalFlight = schedule.flights.last()

                    // multiple

                    flightScheduleAirportDepatureCodeTextView.text =
                        departureFlight.departure.airportCode
                    flightScheduleAirportArrivalCodeTextView.text =
                        arrivalFlight.arrival.airportCode

                    flightScheduleDepartureTimeTextView.text =
                        departureFlight.departure.scheduledTimeLocal.dateTime
                    flightScheduleArrivalTimeTextView.text =
                        arrivalFlight.arrival.scheduledTimeLocal.dateTime

                    flightScheduleAirlineIdTextView.text = arrivalFlight.marketingCarrier.airlineId
                    flightScheduleFlightNumberTextView.text =
                        arrivalFlight.marketingCarrier.flightNumber

                } else {
                    //single
                    flightScheduleAirportDepatureCodeTextView.text =
                        schedule.flights[0].departure.airportCode
                    flightScheduleAirportArrivalCodeTextView.text =
                        schedule.flights[0].arrival.airportCode

                    flightScheduleDepartureTimeTextView.text =
                        schedule.flights[0].departure.scheduledTimeLocal.dateTime
                    flightScheduleArrivalTimeTextView.text =
                        schedule.flights[0].arrival.scheduledTimeLocal.dateTime

                    flightScheduleAirlineIdTextView.text =
                        schedule.flights[0].marketingCarrier.airlineId
                    flightScheduleFlightNumberTextView.text =
                        schedule.flights[0].marketingCarrier.flightNumber

                }
            }
    }
}


