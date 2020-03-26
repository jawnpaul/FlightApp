package ng.com.knowit.flightapp.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ng.com.knowit.flightapp.model.Airport

@Database(entities = arrayOf(Airport::class), version = 3)
abstract class AirportDatabase : RoomDatabase() {

    object DatabaseProvider {

        private lateinit var INSTANCE: AirportDatabase
        fun getDatabase(context: Context): AirportDatabase {
            synchronized(AirportDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AirportDatabase::class.java,
                        "airport_database"
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'airport_table' ADD COLUMN 'airportLongitude' NUMBER")
                database.execSQL("ALTER TABLE 'airport_table' ADD COLUMN 'airportLatitude' NUMBER")

                Log.d("AirportDB", "Migration worked")
            }
        }
    }


    abstract fun airportDao(): AirportDao

}