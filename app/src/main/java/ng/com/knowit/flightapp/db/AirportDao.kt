package ng.com.knowit.flightapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import ng.com.knowit.flightapp.model.Airport

@Dao
interface AirportDao {

    @Insert
    fun insert(airport: Airport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(airports: List<Airport>)

    @Update
    fun update(airport: Airport)

    @Delete
    fun delete(airport: Airport)

    @Query("SELECT * FROM airport_table ORDER BY airportLocalId ASC")
    fun getAllAirport(): LiveData<List<Airport>>

    @Query("SELECT * FROM airport_table ORDER BY airportLocalId ASC")
    fun getAllAirportList(): List<Airport>

    @Query("SELECT * FROM airport_table WHERE airportLocationName LIKE :query")
    fun getSingleAirport(query: String): List<Airport>

    @Query("SELECT * FROM airport_table WHERE airportIataCode LIKE :query")
    fun getAirportByIataCode(query: String): Airport

    @Query("SELECT * FROM airport_table WHERE airportLocationName LIKE :search OR airportLocation LIKE :search")
    fun findAirportByName(search: String): LiveData<List<Airport>>


}