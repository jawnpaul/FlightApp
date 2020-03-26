package ng.com.knowit.flightapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ng.com.knowit.flightapp.R
import ng.com.knowit.flightapp.fragments.BottomSheetFragment
import ng.com.knowit.flightapp.model.Airport
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var polyline: Polyline

    private lateinit var latLngList: List<LatLng>

    lateinit var airportList: List<Airport>

    private lateinit var bottomSheet: View


    private lateinit var tapActionLayout: LinearLayout

    private val REQUEST_LOCATION_PERMISSION = 1

    //key = yr46gzu4nsyqheepzpewm8ct
    //secret  = rht2PAx8eH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val intent = intent
        if (intent != null) {

            val list = (intent.extras?.getSerializable("list"))

            try {
                list as List<Airport>

                //latLngList = getLatLng(list)

                airportList = list

                //getPolyLine(airportList, map)
                Log.d("MapsA", "Polyline Gotten")

            } catch (e: TypeCastException) {

                Log.d("MapsA", e.toString())
            }


        }


        bottomSheet = findViewById(R.id.bottom_sheet1)

        var bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 120
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


        tapActionLayout = findViewById(R.id.tap_action_layout)

        bottomSheet.setOnClickListener {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }


        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapActionLayout.setVisibility(View.VISIBLE)
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapActionLayout.setVisibility(View.GONE)
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapActionLayout.setVisibility(View.GONE)
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            }
        })

        tapActionLayout.setOnClickListener {
            /*if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            }*/
            showBottomSheetDialogFragment()
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        setMapLongClick(map)

        setPoiClick(map)
        enableLocation()



        if (this::airportList.isInitialized) {
            updateMap(airportList)
            var airportLatitude: Double
            var airportLongitude: Double
            try {

                airportLatitude = airportList[0].airportLatitude!!
                airportLongitude = airportList[0].airportLongitude!!
                val departureLatLng = LatLng(airportLatitude, airportLongitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(departureLatLng, 6f))
                Log.d("MapsA", "Map Updated")

            } catch (e: NullPointerException) {

                //Unable to get Location or location hasn't been updated in the db
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)

            )
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableLocation()
            }
        }
    }


    private fun showBottomSheetDialogFragment() {
        val bottomSheetFragment =
            BottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)


    }

    private fun getPolyOptions(list: List<Airport>): PolylineOptions {

        val pathOptions = PolylineOptions().color(Color.BLACK)
        pathOptions.addAll(getLatLng(list))
        return pathOptions
    }

    private fun getLatLng(airportList: List<Airport>): List<LatLng> {
        val list = mutableListOf<LatLng>()
        for (item in airportList) {

            var airportLatitude: Double
            var airportLongitude: Double

            try {

                airportLatitude = item.airportLatitude!!
                airportLongitude = item.airportLongitude!!

                val latLng = LatLng(airportLatitude, airportLongitude)

                Log.d("MAPS", latLng.latitude.toString())

                list.add(latLng)
            } catch (e: KotlinNullPointerException) {
                Log.d("MAPS", e.toString())
            }


        }

        return list
    }

    private fun updateMap(list: List<Airport>) {
        map.addPolyline(getPolyOptions(list))
    }
}

