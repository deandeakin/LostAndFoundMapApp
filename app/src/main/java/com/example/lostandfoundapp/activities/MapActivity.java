package com.example.lostandfoundapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lostandfoundapp.R;
import com.example.lostandfoundapp.database.DatabaseHelper;
import com.example.lostandfoundapp.model.Advert;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private DatabaseHelper databaseHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText etRadius;
    private Button btnShowNearby;
    private Location currentLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;

    // Fallback map location: Melbourne
    private static final LatLng DEFAULT_MAP_LOCATION = new LatLng(-37.8136, 144.9631);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etRadius = findViewById(R.id.etRadius);
        btnShowNearby = findViewById(R.id.btnShowNearby);

        btnShowNearby.setOnClickListener(v -> showNearbyAdverts());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // Called when Google Map is ready to be used.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        checkLocationPermissionAndLoadMarkers();
    }

    // Checks location permission, gets user location and loads nearby adverts.
    private void checkLocationPermissionAndLoadMarkers() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        map.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location;

                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13));
                        showNearbyAdverts();

                    } else {
                        Toast.makeText(this, "Can't get current location", Toast.LENGTH_SHORT).show();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_MAP_LOCATION, 13));
                        loadAllAdvertMarkers();
                    }
                });
    }

    // Shows only adverts within the specified radius.
    private void showNearbyAdverts() {
        if (map == null) {
            return;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Current location is unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        String radiusText = etRadius.getText().toString().trim();

        if (radiusText.isEmpty()) {
            Toast.makeText(this, "Radius required", Toast.LENGTH_SHORT).show();
            return;
        }

        double radiusKm;

        try {
            radiusKm = Double.parseDouble(radiusText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid radius", Toast.LENGTH_SHORT).show();
            return;
        }

        map.clear();

        ArrayList<Advert> adverts = databaseHelper.getAllAdverts();
        int shownCount = 0;

        for (Advert advert : adverts) {
            float[] distResults = new float[1];

            // Calculate distance between current location and advert location.
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), advert.getLatitude(), advert.getLongitude(), distResults);

            double distKm = distResults[0] / 1000.0;

            if (distKm <= radiusKm) {
                LatLng advertLocation = new LatLng(advert.getLatitude(), advert.getLongitude());

                map.addMarker(new MarkerOptions().position(advertLocation).title(advert.getTitle()).snippet(advert.getCategory() + " - " + advert.getLocation()));

                shownCount++;
            }
        }

        Toast.makeText(this, shownCount + " adverts found within " + radiusKm + " km", Toast.LENGTH_SHORT).show();
    }

    // Loads all adverts on the map.
    private void loadAllAdvertMarkers() {
        map.clear();

        ArrayList<Advert> adverts = databaseHelper.getAllAdverts();

        if (adverts.isEmpty()) {
            Toast.makeText(this, "No adverts found", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng firstLocation = null;

        for (Advert advert : adverts) {
            LatLng advertLocation = new LatLng(advert.getLatitude(), advert.getLongitude());

            map.addMarker(new MarkerOptions().position(advertLocation).title(advert.getTitle()).snippet(advert.getCategory() + " - " + advert.getLocation()));

            if (firstLocation == null) {
                firstLocation = advertLocation;
            }
        }

        if (firstLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 13));
        }
    }

    // Handles permission result from the user.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissionAndLoadMarkers();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_MAP_LOCATION, 13));
                loadAllAdvertMarkers();
            }
        }
    }
}