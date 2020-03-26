package ng.com.knowit.flightapp.utility

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import ng.com.knowit.flightapp.R

class CustomDialog(context: Context, private val isCancellable: Boolean = false) :
    ProgressDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.progress_bar)
        setCancelable(isCancellable)
    }
}