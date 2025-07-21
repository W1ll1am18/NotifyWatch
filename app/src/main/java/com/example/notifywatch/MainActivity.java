package com.example.notifywatch;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean cNA = checkNotificationAccess();
        boolean cPP = checkPostPermission();

        if (!cNA || !cPP) {
            showAlert(cNA, cPP);
            //start activity(screen)
        }


        //TODO Add keyword customizability, 5. ringtone customizability

        //TODO 4. Work on UI. (Add clear button, scroll, etc)

        //TODO 3. Ensure custom notifications are posted in on the app. User proper formatting. Get the app icon Reference Notisave

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAlert(boolean cNA, boolean cPP) {
        StringBuilder message = new StringBuilder("Please enable the following permissions:\n\n");
        if (!cNA) message.append("- Notification Listening Access\n");
        if (!cPP) message.append("- Notifications Permission\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permissions Required").setMessage(message.toString());
        builder.setPositiveButton("Go to Settings", (dialog, id) -> {
            if (!cNA) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            } else if (!cPP) {
                startActivity(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName()));
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted then ask user to enable notification listener access.
                checkNotificationAccess();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_NTLDENIED)
                        .setTitle(R.string.dialog_NTLDENIED_TITLE);

                builder.setPositiveButton(R.string.toSettings, (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                });
                builder.create().show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean checkPostPermission(){
        //Permission Section. Must be in Activity not a service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // There is no specific app permission yet.
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
            return false;
        } else if (!notificationManager.areNotificationsEnabled()) {
//            // Notifications are disabled globally for the app.
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Please enable notifications in settings")
//                    .setTitle("Notifications are disabled");
//
//            builder.setPositiveButton(R.string.toSettings, (dialog, id) -> {
//                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                startActivity(intent);
//            });
//            builder.create().show();
            return false;
        }

        return true;
    }

    private boolean checkNotificationAccess() {
        //Checks if notification listener is not enabled.
        //https://stackoverflow.com/questions/47673127/how-to-check-for-enabled-notification-listeners-programmatically
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners"
        );

        if (enabledListeners == null || !enabledListeners.contains(getPackageName())) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("It seems notification listening access has been disabled. Please go to settings and enable it")
//                    .setTitle("Uh-Oh!");
//
//            builder.setPositiveButton(R.string.toSettings, (dialog, id) -> {
//                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//                startActivity(intent);
//            });
//            builder.create().show();
            return false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    protected void onResume() {
        super.onResume();

        boolean cNA = checkNotificationAccess();
        boolean cPP = checkPostPermission();

        if (!cNA || !cPP) {
            showAlert(cNA, cPP);
        }
    }

    public void settings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    //Buttons
    public void goToSettings(View v) {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }
}