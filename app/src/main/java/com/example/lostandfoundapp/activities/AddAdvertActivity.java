package com.example.lostandfoundapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.location.Address;
import android.location.Geocoder;

import com.bumptech.glide.Glide;
import com.example.lostandfoundapp.R;
import com.example.lostandfoundapp.database.DatabaseHelper;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.io.IOException;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lostandfoundapp.BuildConfig;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class AddAdvertActivity extends AppCompatActivity {

    private static final String TAG = "AddAdvertActivity";
    private RadioGroup rgPostType;
    private EditText etName, etPhone, etDescription, etLocation;
    private Spinner spnCategory;
    private ImageView ivSelected;
    private DatabaseHelper databaseHelper;

    private ActivityResultLauncher<String[]> imagePickerLauncher;
    private ActivityResultLauncher<Intent> placeAutocompleteLauncher;
    private String imageUriString = "";

    private FusedLocationProviderClient fusedLocationClient;
    private double lat = 0.0;
    private double lng = 0.0;
    private boolean hasSelectedLocation = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advert);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        databaseHelper = new DatabaseHelper(this);

        rgPostType = findViewById(R.id.rgPostType);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        spnCategory = findViewById(R.id.spnCategory);
        TextView tvDateTime = findViewById(R.id.tvDateTime);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnSaveAdvert = findViewById(R.id.btnSaveAdvert);
        ivSelected = findViewById(R.id.ivSelected);
        Button btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupCategorySpinner();
        setupImagePicker();
        setupPlaceAutocomplete();

        etLocation.setFocusable(false);
        etLocation.setOnClickListener(v -> openPlaceAutocomplete());

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        tvDateTime.setText("Date/time: " + currentDateTime);

        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());
        btnSaveAdvert.setOnClickListener(v -> saveAdvert());

    }

    // Loads the category options from strings.xml into the spinner.
    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.advert_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);
    }

    // Opens the Android document picker and stores the selected image URI as a string.
    // Persistable read permission lets the app reload the image as needed.
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        imageUriString = uri.toString();
                        try {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            Log.e(TAG, "Failed to take persistable URI permission", e);
                        }
                        Glide.with(this).load(uri).centerCrop().into(ivSelected);
                    }
                }
        );
    }

    private void openImagePicker() {
        imagePickerLauncher.launch(new String[]{"image/*"});
    }

    // Validates the user inputs and saves the advert, image URI, and coordinates to the DB.
    private void saveAdvert() {
        int selectedPostTypeId = rgPostType.getCheckedRadioButtonId();

        if (selectedPostTypeId == -1) {
            Toast.makeText(this, " Lost or Found must be selected", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedPostType = findViewById(selectedPostTypeId);
        String postType = selectedPostType.getText().toString();

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = spnCategory.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasSelectedLocation) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUriString.isEmpty()) {
            Toast.makeText(this, "An image must be uploaded", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = databaseHelper.insertAdvert(postType, name, phone, description, location, category, imageUriString, lat, lng);

        if (inserted) {
            Toast.makeText(this, "Advert has been saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }

    // gets the device's current location and stores its coordinates for the advert.
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        hasSelectedLocation = true;

                        String address = getAddressFromLocation(lat, lng);
                        etLocation.setText(address);

                        Toast.makeText(this, "Current location selected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Sets up Google Places autocomplete results for the location field.
    private void setupPlaceAutocomplete() {
        placeAutocompleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());

                        if (place.getLocation() != null) {
                            String placeText = place.getFormattedAddress();

                            if (placeText == null || placeText.isEmpty()) {
                                placeText = place.getDisplayName();
                            }

                            etLocation.setText(placeText);

                            lat = place.getLocation().latitude;
                            lng = place.getLocation().longitude;
                            hasSelectedLocation = true;
                        }
                    }
                }
        );
    }

    // Opens Google Places autocomplete screen to select a location.
    private void openPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION
        );

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);

        placeAutocompleteLauncher.launch(intent);
    }

    // Converts coordinates to an address using Geocoder.
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                if (address.getAddressLine(0) != null) {
                    return address.getAddressLine(0);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed", e);
        }

        return "Current location: " + latitude + ", " + longitude;
    }

    // Handles permission result from the user.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
