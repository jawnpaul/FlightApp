package ng.com.knowit.flightapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ng.com.knowit.flightapp.model.FlightSchedule

@Database(entities = arrayOf(FlightSchedule::class), version = 3)
abstract class FlightScheduleDatabase : RoomDatabase() {


    object DatabaseProvider {

        private lateinit var INSTANCE: FlightScheduleDatabase
        fun getDatabase(context: Context): FlightScheduleDatabase {
            synchronized(FlightScheduleDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        FlightScheduleDatabase::class.java,
                        "flight_schedule_database"
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }

    abstract fun flightScheduleDao(): FlightScheduleDao

}