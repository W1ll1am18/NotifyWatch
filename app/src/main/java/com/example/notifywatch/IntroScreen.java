package com.example.notifywatch;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IntroScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkNotificationAccess();
    }

    //https://developer.android.com/guide/components/activities/activity-lifecycle
    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationAccess();
    }

    private void checkNotificationAccess() {
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners"
        );

        if (enabledListeners != null && enabledListeners.contains(getPackageName())) {
            Intent intent = new Intent(IntroScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //Buttons
    public void goToSettings(View v) {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }
}