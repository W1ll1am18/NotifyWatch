package com.example.notifywatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
            themeTV.setText("Dark");
            parentView.setBackgroundColor(black);
            themeSwitch.setChecked(true);
        }
        else {
            titleTV.setTextColor(black);
            themeTV.setTextColor(black);
            themeTV.setText("White");
            parentView.setBackgroundColor(white);
            themeSwitch.setChecked(false);
        }
    }

}