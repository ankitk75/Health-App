package com.ankit.healthapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class AssessmentPlanActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private RadioGroup fitnessLevelGroup;
    private Spinner planDurationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_plan);

        createNotificationChannel();
        initializeViews();
        setupSpinner();
        setupSaveButton();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "assessment_channel",
                "Assessment Notifications",
                NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for assessment notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void initializeViews() {
        fitnessLevelGroup = findViewById(R.id.fitnessLevelGroup);
        planDurationSpinner = findViewById(R.id.planDurationSpinner);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item,
            new String[]{"3 months", "9 months", "12 months"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planDurationSpinner.setAdapter(adapter);
    }

    private void setupSaveButton() {
        findViewById(R.id.saveButton).setOnClickListener(v -> checkNotificationPermission());
    }

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                showSuccessNotification();
            }
        } else {
            showSuccessNotification();
        }
    }

    private void showSuccessNotification() {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "assessment_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Congratulations!")
                .setContentText("You have started your healthy journey from today")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build());
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not show notification", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSuccessNotification();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
} 