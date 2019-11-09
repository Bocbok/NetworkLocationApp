package mx.ipn.cic.geo.sesion_network_location

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.View
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.text.DecimalFormat
import java.util.Random

class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private var locationManager: LocationManager? = null
    private var currentLocation = LatLng(19.432608, -99.133208)
    private val moveCameraCurrentLocation = true
    private val timeUpdateLocation = 2000
    private val distanceUpateLocation = 0.05.toFloat()
    private var googleMap: GoogleMap? = null
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private var distanceInMeters: FloatArray = FloatArray(1)
    private var startPoint = LatLng(0.toDouble(),0.toDouble())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initGoogleMaps()
    }

    private fun initGoogleMaps() {
        //Obtain the SupportMapFragment and get notified whem the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Add a marker in Mexico City and move the camera
        val mexicoCity = LatLng(-99.133209, 19.432608)
        this.googleMap!!.addMarker(MarkerOptions().position(mexicoCity).title("Marker in Sydney"))
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(mexicoCity))
        initLocationService()
    }

    private fun initLocationService() {
        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("initLocationService", "Registrando Servicio....")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET), 10)
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
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

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        // This method is called when the location changes.

        latitude = location.latitude
        longitude = location.longitude
        currentLocation = LatLng(latitude, longitude)


        if (startPoint.latitude == 0.toDouble() && startPoint.longitude == 0.toDouble()) {
            startPoint = LatLng(latitude, longitude)
        } else {
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                    startPoint.latitude, startPoint.longitude, distanceInMeters)

            val df = DecimalFormat()
            df.maximumFractionDigits = 4
            val distance = findViewById<TextView>(R.id.distance)
            distance.text = "Distance : " + df.format(distanceInMeters[0].toInt())
        }


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
        private const val DEFAULT_ZOOM_LEVEL = 10
    }
}