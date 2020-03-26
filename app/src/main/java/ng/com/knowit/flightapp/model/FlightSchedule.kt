package ng.com.knowit.flightapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flight_schedule_table")
data class FlightSchedule(
    @ColumnInfo(name = "flightScheduleData") var data: String?,
    @ColumnInfo(name = "flightScheduleIdentifier") var flightScheduleIdentifier: String?
) {


    @PrimaryKey(autoGenerate = true)
    var flightScheduleLocalId: Int = 0
}
