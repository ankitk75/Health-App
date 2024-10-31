package com.ankit.healthapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import android.content.SharedPreferences;
import android.content.Intent;

public class DietTrackerActivity extends AppCompatActivity {
    private Spinner mealTypeSpinner;
    private TextInputEditText caloriesInput;
    private Button saveButton;
    private ImageButton backButton;
    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_tracker);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        userEmail = preferences.getString("USER_EMAIL", "");

        mealTypeSpinner = findViewById(R.id.mealTypeSpinner);
        caloriesInput = findViewById(R.id.caloriesInput);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton1);

        setupMealTypeSpinner();

        saveButton.setOnClickListener(v -> saveDietEntry());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupMealTypeSpinner() {
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snacks"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            mealTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);
    }

    private void saveDietEntry() {
        String mealType = mealTypeSpinner.getSelectedItem().toString();
        String caloriesStr = caloriesInput.getText().toString();

        if (caloriesStr.isEmpty()) {
            Toast.makeText(this, "Please enter calories", Toast.LENGTH_SHORT).show();
            return;
        }

        int calories = Integer.parseInt(caloriesStr);
        long result = dbHelper.addDietEntry(userEmail, mealType, calories);

        if (result != -1) {
            Toast.makeText(this, "Diet entry saved successfully", Toast.LENGTH_SHORT).show();
            caloriesInput.setText("");
            mealTypeSpinner.setSelection(0);
        } else {
            Toast.makeText(this, "Error saving diet entry", Toast.LENGTH_SHORT).show();
        }
    }
} 