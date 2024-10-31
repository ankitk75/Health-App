package com.ankit.healthapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;

public class BMICalculatorActivity extends AppCompatActivity {
    private EditText heightInput;
    private EditText weightInput;
    private Button calculateButton;
    private TextView resultText;
    private TextView adviceText;
    private ImageButton backButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.bmiResultText);
        adviceText = findViewById(R.id.bmiAdviceText);
        backButton = findViewById(R.id.backButton);

        // Load saved profile data
        loadUserProfileData();

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Set up calculate button
        calculateButton.setOnClickListener(v -> calculateBMI());
    }

    private void loadUserProfileData() {
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        
        if (userEmail != null && !userEmail.isEmpty()) {
            Cursor cursor = dbHelper.getProfile(userEmail);
            
            if (cursor != null && cursor.moveToFirst()) {
                String height = cursor.getString(cursor.getColumnIndex("height"));
                String weight = cursor.getString(cursor.getColumnIndex("weight"));
                
                // Set these values to the input fields if they exist
                if (height != null && !height.isEmpty()) {
                    heightInput.setText(height);
                }
                if (weight != null && !weight.isEmpty()) {
                    weightInput.setText(weight);
                }
                
                cursor.close();
                
                // Calculate BMI automatically with loaded values
                if (height != null && !height.isEmpty() && weight != null && !weight.isEmpty()) {
                    calculateBMI();
                }
            }
        }
    }

    private void calculateBMI() {
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            resultText.setText("Please enter both height and weight");
            return;
        }

        float height = Float.parseFloat(heightStr) / 100; // Convert cm to meters
        float weight = Float.parseFloat(weightStr);
        float bmi = weight / (height * height);

        resultText.setText(String.format("Your BMI is %.1f", bmi));

        // Set advice based on BMI
        if (bmi < 18.5) {
            adviceText.setText("You are underweight. You should consider gaining weight.");
        } else if (bmi < 25) {
            adviceText.setText("Your weight is normal. Keep maintaining a healthy lifestyle!");
        } else if (bmi < 30) {
            adviceText.setText("You are overweight. Consider a balanced diet and regular exercise.");
        } else {
            adviceText.setText("You are obese. Please consult a healthcare provider for advice.");
        }
    }
} 