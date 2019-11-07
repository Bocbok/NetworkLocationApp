package mx.ipn.cic.geo.sesion_network_location;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener {
    private LocationManager locationManager = null;
    private LatLng currentLocation = new LatLng(19.432608, -99.133208);
    private boolean moveCameraCurrentLocation = true;
    private static final int DEFAULT_ZOOM_LEVEL = 19;
    private int timeUpdateLocation = 2000;
    private float distanceUpateLocation = (float)0.05;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.initLocationService();
    }

    @Override
    public void onLocationChanged(Location location) {
        // This method is called when the location changes.
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        currentLocation = new LatLng(latitude, longitude);
        Log.d("onLocationChanged", "Latitud:" + latitude + " Longitud:" + longitude);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Posición Actual")
                .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
        if(moveCameraCurrentLocation) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLocation)
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void initLocationService() {
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Log.d("initLocationService", "Registrando Servicio....");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(new String[] {
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, 10);
            }
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    timeUpdateLocation, distanceUpateLocation, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 10) {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        timeUpdateLocation, distanceUpateLocation, this);
        }
    }
}
