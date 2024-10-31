package com.ankit.healthapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import android.content.SharedPreferences;
import android.content.Intent;

public class NicotineTrackerActivity extends AppCompatActivity {
    private Spinner intakeTypeSpinner;
    private TextInputEditText quantityInput;
    private Button saveButton;
    private ImageButton backButton;
    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nicotine_tracker);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        userEmail = preferences.getString("USER_EMAIL", "");

        initializeViews();
        setupSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        intakeTypeSpinner = findViewById(R.id.intakeTypeSpinner);
        quantityInput = findViewById(R.id.quantityInput);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupSpinner() {
        String[] intakeTypes = {"Cigarettes", "Vape Puffs", "Other Nicotine Products"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            intakeTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intakeTypeSpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveNicotineEntry());
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveNicotineEntry() {
        String intakeType = intakeTypeSpinner.getSelectedItem().toString();
        String quantityStr = quantityInput.getText().toString();

        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
        }
    }
} 