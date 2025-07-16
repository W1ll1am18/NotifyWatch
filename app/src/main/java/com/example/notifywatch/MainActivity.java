package com.example.notifywatch;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    EditText inputText;
    TextView inputWord;

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

        //Checks if notification listener is not enabled.
        //https://stackoverflow.com/questions/47673127/how-to-check-for-enabled-notification-listeners-programmatically
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners"
        );

        if (enabledListeners == null || !enabledListeners.contains(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        //Permission Section. Must be in Activity not a service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // There is no specific app permission yet.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
        } else if (!notificationManager.areNotificationsEnabled()) {
            // Notifications are disabled globally for the app.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title);

            builder.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            builder.show();
        }

        //TODO Add keyword customizability, 5. ringtone customizability

        //TODO 2. Add User Friendly entry (Add a delay with a message before redirecting the user)

        //TODO 4. Work on UI. (Add clear button, scroll, etc)

        //TODO 3. Ensure custom notifications are posted in on the app. User proper formatting. Get the app icon Reference Notisave

        //TODO 1. Create a settings menu.

//        EditText text = (EditText)findViewById(R.id.EditText01);
//        String str = text.getText().toString();

        inputWord = (TextView) findViewById(R.id.inputWord);
        inputText = (EditText) findViewById(R.id.inputText);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted then ask user to enable notification listener access.
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_NTLDENIED)
                        .setTitle(R.string.dialog_NTLDENIED_TITLE);

                builder.setNeutralButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                builder.show();
            }
        }
    }

    private void postTestNotification() {
        String CHANNEL_ID = "Notification_Channel";

        // Create channel if needed (only on API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.channel_description));
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.alert)  // Use your icon here
                .setContentTitle("Test Notification")
                .setContentText("This is a test notification from MainActivity")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(2001, builder.build());
        }
    }

    //Buttons
    public void updateKeyword(View v) {
        inputWord.setText("Your keyword is " + inputText.getText());
    }

    public void testPost(View v) {
        postTestNotification();
    }
}