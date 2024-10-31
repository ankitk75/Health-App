package com.ankit.healthapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.VideoView;
import android.widget.MediaController;

public class HomeActivity extends AppCompatActivity {
    private ImageButton settingsButton;
    private BottomNavigationView bottomNav;
    private String userEmail;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user email from UserSession preferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userEmail = preferences.getString("USER_EMAIL", "");

        // Initialize views
        settingsButton = findViewById(R.id.settingsButton);
        bottomNav = findViewById(R.id.bottomNav);

        // Set up bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_account) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Set up settings button
        settingsButton.setOnClickListener(v -> showSettingsMenu());

        // Load user-specific data
        loadUserData(userEmail);

        // Set up click listeners
        setupClickListeners();

        // Simple image setup
        ImageView sugarImage = findViewById(R.id.sugarImage);
        sugarImage.setImageResource(R.drawable.img_sugar);

        // Set up video view
        setupVideoView();
    }

    private void setupClickListeners() {
        // BMI Calculator click listener
        findViewById(R.id.bmiCalculatorLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, BMICalculatorActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });

        // Dietary Guidelines click listener
        findViewById(R.id.dietaryGuidelinesLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, DietaryGuidelinesActivity.class);
            startActivity(intent);
        });

        // Diet Tracker click listener
        findViewById(R.id.dietTrackerLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, DietTrackerActivity.class);
            startActivity(intent);
        });

        // Add a Nicotine Tracker click listener
        findViewById(R.id.nicotineTrackerLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, NicotineTrackerActivity.class);
            startActivity(intent);
        });

        // Step Tracker click listener
        findViewById(R.id.StepTrackerlinesLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, StepTrackerActivity.class);
            startActivity(intent);
        });

        // Assessment Plan click listener
        findViewById(R.id.assessmentLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, AssessmentPlanActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    private void setupVideoView() {
        videoView = findViewById(R.id.videoView);
        
        // Set up media controller
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Set up video URI
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.vid_sugar;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // Set click listener
        videoView.setOnClickListener(v -> {
            if (!videoView.isPlaying()) {
                videoView.start();
            } else {
                videoView.pause();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    private void loadUserData(String userEmail) {
        // TODO: Load user's personal data from database
    }

    private void showSettingsMenu() {
        PopupMenu popup = new PopupMenu(this, settingsButton);
        popup.getMenuInflater().inflate(R.menu.settings_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_profile) {
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                profileIntent.putExtra("USER_EMAIL", userEmail);
                startActivity(profileIntent);
                return true;
            } else if (itemId == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
} 