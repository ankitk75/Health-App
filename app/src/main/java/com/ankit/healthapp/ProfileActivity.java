package com.ankit.healthapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import java.util.Calendar;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton backButton;
    private EditText nameInput;
    private EditText ageInput;
    private Spinner sexSpinner;
    private EditText heightInput;
    private EditText weightInput;
    private EditText birthdayInput;
    private Button saveButton;
    private SharedPreferences preferences;
    private ImageView profileImage;
    private static final int PICK_IMAGE = 100;
    private DatabaseHelper dbHelper;
    private ImageButton callEmergencyButton;
    private static final int CALL_PERMISSION_REQUEST = 1;
    private static final String EMERGENCY_NUMBER = "9999999999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize SharedPreferences
        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Initialize views
        initializeViews();
        setupGenderSpinner();
        setupDatePicker();
        setupSaveButton(userEmail);

        // Load user profile data
        loadUserProfile(userEmail);

        // Setup profile image
        setupProfileImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            profileImage.setImageURI(data.getData());
            // TODO: Save the image URI to SharedPreferences
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        sexSpinner = findViewById(R.id.sexSpinner);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        birthdayInput = findViewById(R.id.birthdayInput);
        saveButton = findViewById(R.id.saveButton);
        profileImage = findViewById(R.id.profileImage);
        callEmergencyButton = findViewById(R.id.callEmergencyButton);
        setupCallButton();

        backButton.setOnClickListener(v -> finish());
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(adapter);
    }

    private void setupDatePicker() {
        birthdayInput.setFocusable(false);
        birthdayInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        String date = day + "/" + (month + 1) + "/" + year;
                        birthdayInput.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupSaveButton(String userEmail) {
        saveButton.setOnClickListener(v -> {
            // Validate inputs
            if (!validateInputs()) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create ContentValues for database
            ContentValues values = new ContentValues();
            values.put("email", userEmail);
            values.put("name", nameInput.getText().toString());
            values.put("age", ageInput.getText().toString());
            values.put("gender", sexSpinner.getSelectedItem().toString());
            values.put("height", heightInput.getText().toString());
            values.put("weight", weightInput.getText().toString());
            values.put("birthday", birthdayInput.getText().toString());

            // Save to database
            long result = dbHelper.insertOrUpdateProfile(values);

            if (result != -1) {
                Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        if (nameInput.getText().toString().isEmpty()) return false;
        if (ageInput.getText().toString().isEmpty()) return false;
        
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();
        
        if (heightStr.isEmpty() || weightStr.isEmpty()) return false;
        
        int height = Integer.parseInt(heightStr);
        int weight = Integer.parseInt(weightStr);
        
        if (height < 120 || height > 220) {
            heightInput.setError("Height must be between 120 and 220 cm");
            return false;
        }
        
        if (weight < 30 || weight > 150) {
            weightInput.setError("Weight must be between 30 and 150 kg");
            return false;
        }
        
        return true;
    }

    private void loadUserProfile(String userEmail) {
        // Load from database
        Cursor cursor = dbHelper.getProfile(userEmail);
        
        if (cursor != null && cursor.moveToFirst()) {
            nameInput.setText(cursor.getString(cursor.getColumnIndex("name")));
            ageInput.setText(cursor.getString(cursor.getColumnIndex("age")));
            
            String savedGender = cursor.getString(cursor.getColumnIndex("gender"));
            if (savedGender != null && !savedGender.isEmpty()) {
                int position = ((ArrayAdapter) sexSpinner.getAdapter()).getPosition(savedGender);
                sexSpinner.setSelection(position);
            }
            
            heightInput.setText(cursor.getString(cursor.getColumnIndex("height")));
            weightInput.setText(cursor.getString(cursor.getColumnIndex("weight")));
            birthdayInput.setText(cursor.getString(cursor.getColumnIndex("birthday")));
            
            cursor.close();
        }
    }

    private void setupProfileImage() {
        profileImage.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        });
    }

    private void setupCallButton() {
        callEmergencyButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_PERMISSION_REQUEST);
            } else {
                makePhoneCall();
            }
        });
    }

    private void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER));
        try {
            startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Call permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ... rest of the existing methods ...
}