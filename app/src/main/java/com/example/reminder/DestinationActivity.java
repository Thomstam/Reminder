package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

public class DestinationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean locationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_CODE = 606;
    private static final String TAG = "DestinationActivity";
    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);
        getLocationPermission();
    }


    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
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
        mMap = googleMap;
        if (locationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void getDeviceLocation() {
        Log.i(TAG, "getDeviceLocation: getting it");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionGranted) {
                Log.d(TAG, "got here");
                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "Found Location");
                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation != null){
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            }
                        }else {
                            Log.d(TAG, "Location not found");
                            Toast.makeText(DestinationActivity.this, "cant find location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "Security Exception");
            Log.i(TAG, "failed to get Location");
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getting permissions: getting it");
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                Log.d(TAG, "permissions already given");
                initializeMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
            Log.d(TAG, "got permissions");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_CODE:{
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    locationPermissionGranted= true;
                    initializeMap();
                }
            }
        }
    }

    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DestinationActivity.DEFAULT_ZOOM));
    }

}