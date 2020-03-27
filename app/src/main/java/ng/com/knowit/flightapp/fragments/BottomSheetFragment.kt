package ng.com.knowit.flightapp.fragments


import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ng.com.knowit.flightapp.R
import ng.com.knowit.flightapp.activities.FlightScheduleActivity
import ng.com.knowit.flightapp.databinding.BottomSheetLayoutBinding
import ng.com.knowit.flightapp.model.Airport
import ng.com.knowit.flightapp.recycleradapters.LocationAdapter
import ng.com.knowit.flightapp.ui.AirportViewModel
import ng.com.knowit.flightapp.utility.CustomDialog
import java.text.DecimalFormat
import java.util.*


class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetLayoutBinding

    private lateinit var airportViewModel: AirportViewModel

    private lateinit var fragmentView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_layout,
            container,
            false
        )

        airportViewModel = ViewModelProviders.of(this).get(AirportViewModel::class.java)

        return binding.root
    }

    private fun selectedAirport(airport: Airport) {
        if (currentEditIsOrigin != null) {
            if (currentEditIsOrigin!!) {
                binding.flightOriginInput.setText(airport.airportLocationName)
            } else {
                binding.flightDestinationInput.setText(airport.airportLocationName)
            }
        }
    }

    private var currentEditIsOrigin: Boolean? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val progressBar = CustomDialog(context!!, false)

        binding.airportsRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        airportViewModel.getIsFetching().observe(this, Observer { value ->

            if (value == true) {
                Log.d("BSF", "Db is empty")
                progressBar.show()

            } else {
                Log.d("BSF", "Db is not empty")

                progressBar.dismiss()
            }

        })

        airportViewModel.getAllAirports().observe(this, Observer<List<Airport>> { airports ->

            if (airports.isNotEmpty()) {
                progressBar.dismiss()
            }

            val adapter = LocationAdapter(airports) { airport -> this.selectedAirport(airport) }
            binding.airportsRecyclerView.adapter = adapter


        })


        binding.flightOriginInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                currentEditIsOrigin = true
            }

        }
        binding.flightDestinationInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                currentEditIsOrigin = false
            }

        }

        binding.flightOriginInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                airportViewModel.getAirportsMatchingQuery(s.toString().trim())
                    .observe(this@BottomSheetFragment, Observer<List<Airport>> { airports ->

                        val adapter =
                            LocationAdapter(airports) { airport -> selectedAirport(airport) }
                        binding.airportsRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()

                    })

            }
        })

        binding.flightDestinationInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

                airportViewModel.getAirportsMatchingQuery(s.toString().trim())
                    .observe(this@BottomSheetFragment, Observer<List<Airport>> { airports ->

                        val adapter =
                            LocationAdapter(airports) { airport -> selectedAirport(airport) }
                        binding.airportsRecyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()

                    })
            }
        })


        binding.flightDateInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                val pickerDialog = DatePickerDialog(
                    context!!,
                    { _, year1, monthOfYear, dayOfMonth ->
                        binding.flightDateInput.setText(
                            year1.toString() + "-" + (DecimalFormat(
                                "00"
                            ).format(monthOfYear + 1)) + "-" + dayOfMonth
                        )
                    },
                    year, month, day
                )
                pickerDialog.show()
            }

        }

        binding.searchButton.setOnClickListener {

            //if the queries ain't empty

            if (binding.flightOriginInput.text.toString().trim().isNotEmpty() &&
                binding.flightDestinationInput.text.toString().trim().isNotEmpty() &&
                binding.flightDateInput.text.toString().trim().isNotEmpty()
            ) {

                try {

                    val intent = Intent(activity, FlightScheduleActivity::class.java)
                    intent.putExtra("originAirportCode", getOriginAirportCode())
                    intent.putExtra("destinationAirportCode", getDestinationAirportCode())
                    intent.putExtra("date", binding.flightDateInput.text.toString().trim())
                    startActivity(intent)

                } catch (e: IndexOutOfBoundsException) {
                    Toast.makeText(context, "Select an airport from the list", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(context, "Input Cannot be Empty", Toast.LENGTH_SHORT).show()
            }


        }


        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }


    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int { // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay
            .getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun getOriginAirportCode(): String {
        return airportViewModel.getAirportCode(binding.flightOriginInput.text.toString().trim())
    }

    private fun getDestinationAirportCode(): String {
        return airportViewModel.getAirportCode(binding.flightDestinationInput.text.toString().trim())
    }

}