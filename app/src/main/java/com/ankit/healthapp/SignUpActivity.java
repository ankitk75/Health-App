package com.ankit.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {
    private ImageButton backButton;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton createAccountButton;
    private MaterialButton googleSignUpButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        createAccountButton = findViewById(R.id.createAccountButton);
        googleSignUpButton = findViewById(R.id.googleSignUpButton);

        // Set click listeners
        backButton.setOnClickListener(v -> finish());

        createAccountButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            
            if (!validateInput(email, password)) {
                return;
            }

            if (dbHelper.checkUser(email)) {
                Toast.makeText(SignUpActivity.this, 
                    "Email already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.addUser(email, password)) {
                Toast.makeText(SignUpActivity.this, 
                    "Account created successfully", Toast.LENGTH_SHORT).show();
                // Navigate to login screen
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, 
                    "Failed to create account", Toast.LENGTH_SHORT).show();
            }
        });

        googleSignUpButton.setOnClickListener(v -> {
            // TODO: Implement Google sign up
            Toast.makeText(SignUpActivity.this, "Google sign up clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }
} 