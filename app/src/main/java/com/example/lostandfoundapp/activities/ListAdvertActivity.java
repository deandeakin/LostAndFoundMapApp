package com.example.lostandfoundapp.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfoundapp.R;
import com.example.lostandfoundapp.adapter.AdvertAdapter;
import com.example.lostandfoundapp.database.DatabaseHelper;
import com.example.lostandfoundapp.model.Advert;

import java.util.ArrayList;

public class ListAdvertActivity extends AppCompatActivity {

    private EditText etSearch;
    private Spinner spnFilterCategory;
    private RecyclerView rvAdverts;
    private DatabaseHelper databaseHelper;
    private AdvertAdapter advertAdapter;
    private ArrayList<Advert> advertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_advert);

        databaseHelper = new DatabaseHelper(this);

        etSearch = findViewById(R.id.etSearch);
        spnFilterCategory = findViewById(R.id.spnFilterCategory);
        rvAdverts = findViewById(R.id.rvAdverts);

        setupRecyclerView();
        setupCategoryFilter();
        setupSearchBar();

        loadAdverts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdverts();
    }

    // Sets up the RecyclerView and its adapter.
    private void setupRecyclerView() {
        advertList = new ArrayList<>();
        advertAdapter = new AdvertAdapter(this, advertList);
        rvAdverts.setLayoutManager(new LinearLayoutManager(this));
        rvAdverts.setAdapter(advertAdapter);
    }

    // Loads the category filter option and refreshes the list when a category is selected.
    private void setupCategoryFilter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.advert_filter_categories, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilterCategory.setAdapter(adapter);

        spnFilterCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadAdverts();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    // Adds real-time search functionality to the search bar.
    private void setupSearchBar() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadAdverts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Loads matching adverts from the DB using the current search text and category filter.
    private void loadAdverts() {
        String searchText = etSearch.getText().toString();
        String selectedCategory = spnFilterCategory.getSelectedItem().toString();

        ArrayList<Advert> results = databaseHelper.searchAdverts(searchText, selectedCategory);
        advertAdapter.updateData(results);
    }
}