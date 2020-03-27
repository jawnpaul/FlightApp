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

        /*windowManager2 =
            this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutInflater =
            this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.progress_bar, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val windowManager = this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager*/

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))



        setContentView(R.layout.progress_bar)
        setCancelable(isCancellable)
    }
}