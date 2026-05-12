package com.example.lostandfoundapp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfoundapp.R;
import com.example.lostandfoundapp.database.DatabaseHelper;
import com.example.lostandfoundapp.model.Advert;
import com.bumptech.glide.Glide;

public class DetailAdvertActivity extends AppCompatActivity {

    private ImageView ivDetail;
    private TextView tvDetailTitle, tvDetailDateTime, tvDetailCategory, tvDetailLocation, tvDetailDescription, tvDetailContact;
    private Button btnRemoveAdvert;

    private DatabaseHelper databaseHelper;
    private int advertId = -1; // Default value for advert ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_advert);

        databaseHelper = new DatabaseHelper(this);

        ivDetail = findViewById(R.id.ivDetail);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDateTime = findViewById(R.id.tvDetailDateTime);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailContact = findViewById(R.id.tvDetailContact);
        btnRemoveAdvert = findViewById(R.id.btnRemoveAdvert);

        advertId = getIntent().getIntExtra("advert_id", -1);

        if (advertId == -1) {
            Toast.makeText(this, "Advert cannot be found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAdvertDetails();

        btnRemoveAdvert.setOnClickListener(v -> confirmRemoveAdvert());
    }

    // Loads the selected advert from the DB using the ID from the list screen.
    private void loadAdvertDetails() {
        Advert advert = databaseHelper.getAdvertById(advertId);

        if (advert == null) {
            Toast.makeText(this, "Advert cannot be found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailTitle.setText(advert.getTitle());
        tvDetailDateTime.setText("Posted: " + advert.getDateTime());
        tvDetailCategory.setText("Category: " + advert.getCategory());
        tvDetailLocation.setText("Location: " + advert.getLocation());
        tvDetailDescription.setText("Description: " + advert.getDescription());
        tvDetailContact.setText("Phone: " + advert.getPhone());

        if (advert.getImageUri() != null && !advert.getImageUri().isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(advert.getImageUri()))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(ivDetail);
        } else {
            ivDetail.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    // Shows a confirmation dialog before removing the advert.
    private void confirmRemoveAdvert() {
        new AlertDialog.Builder(this)
                .setTitle("Remove advert")
                .setMessage("Are you sure you want to remove this advert?")
                .setPositiveButton("Remove", (dialog, which) -> removeAdvert())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Deletes the advert from the DB and returns to the previous screen.
    private void removeAdvert() {
        boolean deleted = databaseHelper.deleteAdvert(advertId);

        if (deleted) {
            Toast.makeText(this, "Advert has been removed", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to remove advert", Toast.LENGTH_SHORT).show();
        }
    }
}
