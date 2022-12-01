package android.google.maps.api.cityguide

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.google.maps.api.cityguide.databinding.ActivityMapsBinding
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
//    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityMapsBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    // 1
    private lateinit var locationCallback: LocationCallback
    // 2
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        // 3
        private const val REQUEST_CHECK_SETTINGS = 2
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation!!
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        createLocationRequest()

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

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()

//        mMap = googleMap
//
//        mMap.getUiSettings().setZoomControlsEnabled(true)
//        mMap.setOnMarkerClickListener(this)
//
//        // Add a marker in to coord and move the camera
//        val coord = LatLng(40.73, -73.99)
//        val title = "Marker in New York"
//
//        mMap.addMarker(MarkerOptions().position(coord).title(title))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord))
//
//        //Zooms the camera more to the coord
//        // zoom = 0 -> zoom all the way out
//        // zoom = 20 -> max for developed places
//        // zoom = 13 -> max for remote areas
//
//        val zoom = 20.0f
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, zoom))


    }

    override fun onMarkerClick(p0: Marker): Boolean = false

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }


//        map.mapType = GoogleMap.MAP_TYPE_TERRAIN

//        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        map.mapType = GoogleMap.MAP_TYPE_SATELLITE

//        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }


    }


//    private fun setUpMap() {
//        if (ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
//            return
//        }
//
//        // 1
//        map.isMyLocationEnabled = true
//
//// 2
//        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
//            // Got last known location. In some rare situations this can be null.
//            // 3
//            if (location != null) {
//                lastLocation = location
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//            }
//        }
//
//    }

    private fun placeMarkerOnMap(location: LatLng) {

        // 1
        val markerOptions = MarkerOptions().position(location)

        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))

//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        // 2
        map.addMarker(markerOptions)
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 10000
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapsActivity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }


    // 2
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 3
    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }


}