package com.example.lostandfoundapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfoundapp.R;

public class MainActivity extends AppCompatActivity {

    private Button btnCreateAdvert, btnShowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateAdvert = findViewById(R.id.btnCreateAdvert);
        btnShowItems = findViewById(R.id.btnShowItems);

        btnCreateAdvert.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddAdvertActivity.class);
            startActivity(intent);
        });

        btnShowItems.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListAdvertActivity.class);
            startActivity(intent);
        });
    }
}