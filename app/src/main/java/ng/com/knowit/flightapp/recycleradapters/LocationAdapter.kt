package ng.com.knowit.flightapp.recycleradapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ng.com.knowit.flightapp.R
import ng.com.knowit.flightapp.model.Airport

class LocationAdapter(
    val airportList: List<Airport>,
    val clickListener: (airport: Airport) -> Unit
) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_airport_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return airportList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val airport: Airport = airportList[position]
        holder.airportLocationTextView.text = airport.airportLocation
        holder.airportNameTextView.text = airport.airportLocationName

        holder.itemView.setOnClickListener {
            clickListener(airportList[holder.adapterPosition])
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val airportNameTextView = itemView.findViewById(R.id.airport_name_text_view) as TextView
        val airportLocationTextView =
            itemView.findViewById(R.id.airport_location_text_view) as TextView

    }


}