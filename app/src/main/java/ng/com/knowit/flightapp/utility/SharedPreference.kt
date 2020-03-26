package ng.com.knowit.flightapp.utility

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) {
    private val PREFS_NAME = "flight_app_prefs"
    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(tokenvalue: String?) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putString("token", tokenvalue)
        editor.apply()
    }


    fun getTokenValue(): String? {
        return sharedPref.getString("token", null)

    }

    fun saveTokenExpiryDate(tokenExpiryDateValue: Long) {
        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putLong("tokenExpiryDate", tokenExpiryDateValue)
        editor.apply()
    }

    fun getTokenExpiryDate(): Long {

        //This default is to take care of first call, I set a date in the past
        return sharedPref.getLong("tokenExpiryDate", 1569193200000L)

    }

}