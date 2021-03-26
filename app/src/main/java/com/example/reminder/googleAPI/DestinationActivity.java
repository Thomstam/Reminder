package com.example.reminder.googleAPI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.reminder.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DestinationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList<String> timeDirectionsNeeds;
    private RelativeLayout directionSection;
    private Button doneButton;
    private boolean gpsLocationGranted = false;
    private static final int REQUEST_CHECK_SETTINGS = 9001;
    private Marker startingPoint;
    private Marker destinationPoint;
    private AutocompleteSupportFragment startingPointSearchbar;
    private static String apiKey = null;
    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean locationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_CODE = 606;
    private static final float DEFAULT_ZOOM = 15f;
    private Polyline mPolyline;
    private String departureTimeInUTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        necessaryStuff();

        createLocationRequest();

        getLocationPermission();

        locationRepositioning();

        drawRoute();

        carDrawRouteSelected();

        walkingDrawRouteSelected();

        startingAutoCompleteFragment();

        destinationAutoCompleteFragment();

        doneButtonFinish();
    }

    private void necessaryStuff() {
        directionSection = findViewById(R.id.directionSection);
        timeDirectionsNeeds = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        departureTimeInUTC = bundle.getString("TimeInUTC");
        apiKey = getString(R.string.googleApi);
        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> gpsIsEnabled("Yes"));

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(DestinationActivity.this,
                            REQUEST_CHECK_SETTINGS);

                    gpsIsEnabled("No");
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                initializeMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
        }
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (locationPermissionGranted) {
                Task<Location> location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location currentLocation = task.getResult();
                        if (currentLocation != null) {
                            startingPoint = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                    .title("MyLocation"));
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            startingPoint.setVisible(false);
                        }
                    } else {
                        Toast.makeText(DestinationActivity.this, "cant find location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void locationRepositioning() {
        ImageButton currentLocation = findViewById(R.id.currentLocation);
        currentLocation.setOnClickListener(v -> {
            createLocationRequest();
            if (gpsLocationGranted) {
                getDeviceLocation();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        locationPermissionGranted = false;
                        return;
                    }
                }
                locationPermissionGranted = true;
                initializeMap();
            }
        }
    }

    private void drawRoute() {
        Button getDirectionButton = findViewById(R.id.getDirectionsButton);
        getDirectionButton.setOnClickListener(v -> {
            if (startingPoint != null) {
                if (destinationPoint != null) {
                    timeDirectionsNeeds.clear();
                    String tempPessimistDriving = getUrl(startingPoint.getPosition(), destinationPoint.getPosition(), "driving", "pessimistic");
                    String tempOptimistDriving = getUrl(startingPoint.getPosition(), destinationPoint.getPosition(), "driving", "optimistic");
                    String tempOptimistWalking = getUrl(startingPoint.getPosition(), destinationPoint.getPosition(), "walking", "optimistic");
                    DownloadTask walking = new DownloadTask();
                    DownloadTask pessimistDriving = new DownloadTask();
                    DownloadTask optimistDriving = new DownloadTask();
                    walking.execute(tempOptimistWalking);
                    pessimistDriving.execute(tempPessimistDriving);
                    optimistDriving.execute(tempOptimistDriving);
                } else {
                    Toast.makeText(this, "Set Destination Place", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Set Starting Point", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carDrawRouteSelected() {
        RadioButton carDirectionsDrawRoute = findViewById(R.id.carDirections);
        carDirectionsDrawRoute.setOnClickListener(v -> {
            String tempOptimistDriving = getUrl(startingPoint.getPosition(), destinationPoint.getPosition(), "driving", "pessimistic");
            DownloadTask walking = new DownloadTask();
            walking.execute(tempOptimistDriving);
        });
    }

    private void walkingDrawRouteSelected() {
        RadioButton walkingDirectionDrawRoute = findViewById(R.id.walkDirections);
        walkingDirectionDrawRoute.setOnClickListener(v -> {
            String tempOptimistWalking = getUrl(startingPoint.getPosition(), destinationPoint.getPosition(), "walking", "optimistic");
            DownloadTask driving = new DownloadTask();
            driving.execute(tempOptimistWalking);
        });
    }

    private void startingAutoCompleteFragment() {
        startingPointSearchbar = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentStartingLocation);
        assert startingPointSearchbar != null;
        startingPointSearchbar.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        startingPointSearchbar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                if (place.getLatLng() != null) {
                    moveCamera(place.getLatLng());
                    if (startingPoint != null) startingPoint.remove();
                    startingPoint = mMap.addMarker(new MarkerOptions()
                            .position(place.getLatLng())
                            .title(place.getName()));
                }
            }


            @Override
            public void onError(@NotNull Status status) {

            }
        });
    }

    private void destinationAutoCompleteFragment() {
        AutocompleteSupportFragment destinationPointSearchBar = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentDestination);
        assert destinationPointSearchBar != null;
        destinationPointSearchBar.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        destinationPointSearchBar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull @NotNull Place place) {
                if (place.getLatLng() != null) {
                    moveCamera(place.getLatLng());
                    if (destinationPoint != null) destinationPoint.remove();
                    destinationPoint = mMap.addMarker(new MarkerOptions()
                            .position(place.getLatLng())
                            .title(place.getName()));
                }
            }

            @Override
            public void onError(@NonNull @NotNull Status status) {

            }
        });
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (locationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            startingPointSearchbar.setText("Current Location");
        }
    }

    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DestinationActivity.DEFAULT_ZOOM));
    }

    private void gpsIsEnabled(String answer) {
        gpsLocationGranted = answer.equals("Yes");
    }

    private void doneButtonFinish() {
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("timeDirections", timeDirectionsNeeds);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    public String getUrl(LatLng origin, LatLng dest, String modeToAdd, String trafficModel) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + modeToAdd;
        String model = "traffic_model=" + trafficModel;
        String departureTime = "departure_time=" + departureTimeInUTC;
        String parameters = str_origin + "&" + str_dest + "&" + departureTime + "&" + mode + "&" + model;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + apiKey;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);


            urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.connect();


            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                JSONArray routesArray = jObject.getJSONArray("routes");
                JSONObject route = routesArray.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);
                JSONObject durationObject = leg.getJSONObject("duration");
                String duration = durationObject.getString("text");
                timeDirectionsNeeds.add((duration));
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions;

            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(0);
            if (mPolyline != null) {
                mPolyline.remove();
            }

            for (int i = 0; i < path.size(); i++) {
                HashMap<String, String> point = path.get(i);

                double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            lineOptions.addAll(points);
            lineOptions.width(20);
            lineOptions.color(Color.BLUE);
            mPolyline = mMap.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startingPoint.getPosition());
            builder.include(destinationPoint.getPosition());
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 300);
            mMap.animateCamera(cameraUpdate);
            directionSection.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.VISIBLE);
        }
    }

}