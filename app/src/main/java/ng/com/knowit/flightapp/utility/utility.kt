package ng.com.knowit.flightapp.utility

import android.content.Context
import android.net.ConnectivityManager


class utility {


    companion object {

        fun isOnline(context: Context): Boolean {
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null &&
                    cm.activeNetworkInfo.isConnectedOrConnecting
        }
    }

}