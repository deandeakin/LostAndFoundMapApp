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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lostandfoundapp.R;
import com.example.lostandfoundapp.database.DatabaseHelper;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddAdvertActivity extends AppCompatActivity {

    private RadioGroup rgPostType;
    private EditText etName, etPhone, etDescription, etLocation;
    private Spinner spnCategory;
    private TextView tvDateTime;
    private Button btnSelectImage, btnSaveAdvert;
    private ImageView ivSelected;
    private DatabaseHelper databaseHelper;

    private ActivityResultLauncher<String[]> imagePickerLauncher;
    private String imageUriString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advert);

        databaseHelper = new DatabaseHelper(this);

        rgPostType = findViewById(R.id.rgPostType);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        spnCategory = findViewById(R.id.spnCategory);
        tvDateTime = findViewById(R.id.tvDateTime);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSaveAdvert = findViewById(R.id.btnSaveAdvert);
        ivSelected = findViewById(R.id.ivSelected);

        setupCategorySpinner();
        setupImagePicker();

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        tvDateTime.setText("Date/time: " + currentDateTime);

        btnSelectImage.setOnClickListener(v -> openImagePicker());
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
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        Glide.with(this).load(uri).centerCrop().into(ivSelected);
                    }
                }
        );
    }

    private void openImagePicker() {
        imagePickerLauncher.launch(new String[]{"image/*"});
    }

    // Validates the user inputs and saves the record to the DB.
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

        if (imageUriString.isEmpty()) {
            Toast.makeText(this, "An image must be uploaded", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = databaseHelper.insertAdvert(postType, name, phone, description, location, category, imageUriString);

        if (inserted) {
            Toast.makeText(this, "Advert has been saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save advert", Toast.LENGTH_SHORT).show();
        }
    }
}
