package ng.com.knowit.flightapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "airport_table")
data class Airport(
    @ColumnInfo(name = "airportLocationName") var airportLocationName: String?,
    @ColumnInfo(name = "airportLocation") var airportLocation: String?,
    @ColumnInfo(name = "airportCountry") var airportCountry: String?,


    @ColumnInfo(name = "airportIataCode") var airportIataCode: String?,

    @SerializedName("airportLongitude")
    @Expose
    @ColumnInfo(name = "airportLongitude") var airportLongitude: Double?,

    @SerializedName("airportLatitude")
    @Expose
    @ColumnInfo(name = "airportLatitude") var airportLatitude: Double?
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var airportLocalId: Int = 0
}