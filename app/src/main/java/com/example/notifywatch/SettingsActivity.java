package com.example.notifywatch;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private View parentView;
    private SwitchMaterial themeSwitch;
    private TextView themeTV, titleTV;
    private SettingsMenu settings;
    EditText inputText;
    TextView inputWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parentView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialiseWidgets();
        settings = (SettingsMenu) getApplication();
        loadSharedPreferences();
        updateView();
        initialSwitchListener();

        inputWord = findViewById(R.id.inputWord);
        inputText = findViewById(R.id.inputText);
    }

    private void initialiseWidgets() {
        themeTV = findViewById(R.id.themeTV);
        titleTV = findViewById(R.id.titleTV);
        themeSwitch = findViewById(R.id.themeSwitch);
        parentView = findViewById(R.id.parentView);
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SettingsMenu.PREFERENCES, MODE_PRIVATE);
        String theme = sharedPreferences.getString(SettingsMenu.CUSTOM_THEME, SettingsMenu.LIGHT_THEME);
        settings.setCustomTheme(theme);
    }

    //https://www.youtube.com/watch?v=-u63b5X2NqE&ab_channel=CodeWithCal
    private void initialSwitchListener() {
        themeSwitch.setOnCheckedChangeListener((buttonView, checked) -> {
            if(checked) {
                settings.setCustomTheme(SettingsMenu.DARK_THEME);
            }
            else {
                settings.setCustomTheme(SettingsMenu.LIGHT_THEME);
            }
            SharedPreferences.Editor editor = getSharedPreferences(SettingsMenu.PREFERENCES, MODE_PRIVATE).edit();
            editor.putString(SettingsMenu.CUSTOM_THEME, settings.getCustomTheme());
            editor.apply();
            updateView();
        });
    }

    private void updateView() {
        final int black = ContextCompat.getColor(this, R.color.black);
        final int white = ContextCompat.getColor(this, R.color.white);

        if(settings.getCustomTheme().equals(SettingsMenu.DARK_THEME)) {
            titleTV.setTextColor(white);
            themeTV.setTextColor(white);
            themeTV.setText("Dark Mode");
            parentView.setBackgroundColor(black);
            themeSwitch.setChecked(true);
        }
        else {
            titleTV.setTextColor(black);
            themeTV.setTextColor(black);
            themeTV.setText("Light Mode");
            parentView.setBackgroundColor(white);
            themeSwitch.setChecked(false);
        }
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void updateKeyword(View v) {
        inputWord.setText("Your keyword is " + inputText.getText());
    }

    public void postTestNotification(View v) {
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
}