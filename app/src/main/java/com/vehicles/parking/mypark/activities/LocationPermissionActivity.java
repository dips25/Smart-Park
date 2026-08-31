package com.vehicles.parking.mypark.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.vehicles.parking.mypark.MainActivity;
import com.vehicles.parking.mypark.R;

public class LocationPermissionActivity extends AppCompatActivity {

    private static final String LOCATION_PERMISSION =
            Manifest.permission.ACCESS_FINE_LOCATION;

    private SharedPreferences prefs;

    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        prefs = getSharedPreferences(
                "permission_prefs",
                MODE_PRIVATE
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {

                    if (isGranted) {

                        Toast.makeText(
                                this,
                                "Location permission granted",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Continue app flow
                        //finish();

                        //goToMainActivity

                    } else {

                        handlePermissionDenied();
                    }
                }
        );

        Button btnAllowLocation =
                findViewById(R.id.btnAllowLocation);

        btnAllowLocation.setOnClickListener(v -> {
            checkLocationPermission();
        });
    }

    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                LOCATION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED) {

            // Permission already granted
            //finish();

        } else if (
                shouldShowRequestPermissionRationale(
                        LOCATION_PERMISSION
                )
        ) {

            // User denied before
            showRationaleDialog();

        } else {

            boolean askedBefore = prefs.getBoolean(
                    "asked_location_permission",
                    false
            );

            // First time ask
            if (!askedBefore) {

                prefs.edit()
                        .putBoolean(
                                "asked_location_permission",
                                true
                        )
                        .apply();

                permissionLauncher.launch(
                        LOCATION_PERMISSION
                );

            } else {

                // Probably "Don't Ask Again"
                showSettingsDialog();
            }
        }
    }

    private void showRationaleDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Location Permission Required")
                .setMessage(
                        "Location access helps us show nearby services and accurate delivery updates."
                )
                .setPositiveButton(
                        "Allow",
                        (dialog, which) -> permissionLauncher.launch(
                                LOCATION_PERMISSION
                        )
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handlePermissionDenied() {

        if (shouldShowRequestPermissionRationale(
                LOCATION_PERMISSION
        )) {

            showRationaleDialog();

        } else {

            showSettingsDialog();
        }
    }

    private void showSettingsDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Enable Permission from Settings")
                .setMessage(
                        "Location permission has been permanently denied. Please enable it from app settings."
                )
                .setPositiveButton(
                        "Open Settings",
                        (dialog, which) -> openAppSettings()
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openAppSettings() {

        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts(
                        "package",
                        getPackageName(),
                        null
                )
        );

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(LocationPermissionActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(LocationPermissionActivity.this , MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}