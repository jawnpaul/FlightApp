package ng.com.knowit.flightapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import ng.com.knowit.flightapp.utility.utility

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (utility.isOnline(this)) {
            Fabric.with(this, Crashlytics())
        }

    }
}
