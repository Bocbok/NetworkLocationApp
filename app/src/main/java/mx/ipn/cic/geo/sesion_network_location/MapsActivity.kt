package mx.ipn.cic.geo.sesion_network_location

import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.fragment.app.FragmentActivity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.util.Random

class MapsActivity : FragmentActivity(), OnMapReadyCallback, LocationListener {


    private var locationManager: LocationManager? = null
    private var currentLocation = LatLng(19.432608, -99.133208)
    private val moveCameraCurrentLocation = true
    private val timeUpdateLocation = 2000
    private val distanceUpateLocation = 0.05.toFloat()
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
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
        this.googleMap = googleMap

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        // this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        initLocationService()
    }


    private fun initLocationService() {
        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("initLocationService", "Registrando Servicio....")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET), 10)
                return
            }
        } else {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    timeUpdateLocation.toLong(), distanceUpateLocation, this)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            10 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return
                    }
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        timeUpdateLocation.toLong(), distanceUpateLocation, this)
                return
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        // This method is called when the location changes.
        val latitude = location.latitude
        val longitude = location.longitude
        currentLocation = LatLng(latitude, longitude)
        Log.d("onLocationChanged", "Latitud:$latitude Longitud:$longitude")
        googleMap!!.clear()
        googleMap!!.addMarker(MarkerOptions()
                .position(currentLocation)
                .title("Posición Actual")
                .icon(BitmapDescriptorFactory.defaultMarker(Random().nextInt(360).toFloat())))
        if (moveCameraCurrentLocation) {
            val cameraPosition = CameraPosition.Builder()
                    .target(currentLocation)
                    .zoom(DEFAULT_ZOOM_LEVEL.toFloat())
                    .build()
            googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    companion object {
        private val DEFAULT_ZOOM_LEVEL = 19
    }


}