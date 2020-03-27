package ng.com.knowit.flightapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ng.com.knowit.flightapp.model.FlightSchedule

@Dao
interface FlightScheduleDao {

    @Insert
    fun insert(flightSchedule: FlightSchedule)

    @Update
    fun update(flightSchedule: FlightSchedule)

    @Delete
    fun delete(flightSchedule: FlightSchedule)

    @Query("SELECT * FROM flight_schedule_table WHERE flightScheduleIdentifier LIKE :flightScheduleIdentifier")
    fun getAllFlightSchedule(flightScheduleIdentifier: String): LiveData<List<FlightSchedule>>

    @Query("SELECT * FROM flight_schedule_table WHERE flightScheduleIdentifier LIKE :flightScheduleIdentifier")
    fun getAllFlightScheduleListt(flightScheduleIdentifier: String): List<FlightSchedule>

    @Query("SELECT * FROM flight_schedule_table ORDER BY flightScheduleLocalId ASC")
    fun getAllFlightScheduleLiveList(): LiveData<List<FlightSchedule>>

    @Query("SELECT * FROM flight_schedule_table ORDER BY flightScheduleLocalId ASC")
    fun getAllFlightScheduleList(): List<FlightSchedule>

    @Query("SELECT * FROM flight_schedule_table WHERE flightScheduleIdentifier LIKE :flightScheduleIdentifier")
    fun getSingleFlightSchedule(flightScheduleIdentifier: String): List<FlightSchedule>
}