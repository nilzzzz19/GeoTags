package android.google.maps.api.cityguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.google.maps.api.cityguide.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



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
        mMap = googleMap

        mMap.getUiSettings().setZoomControlsEnabled(true)
        mMap.setOnMarkerClickListener(this)

        // Add a marker in to coord and move the camera
        val coord = LatLng(40.73, -73.99)
        val title = "Marker in New York"

        mMap.addMarker(MarkerOptions().position(coord).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord))

        //Zooms the camera more to the coord
        // zoom = 0 -> zoom all the way out
        // zoom = 20 -> max for developed places
        // zoom = 13 -> max for remote areas

        val zoom = 20.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, zoom))


    }

    override fun onMarkerClick(p0: Marker): Boolean = false

}