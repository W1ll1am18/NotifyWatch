package com.example.notifywatch;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private final ArrayList<TextView> textViews = new ArrayList<>();
    private final ArrayList<LinearLayout> LL = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private View parentView;
    private TextView textView6;
    private SwitchMaterial themeSwitch, dismissSwitch;
    private SettingsMenu settings;
    private AppCompatButton confirmBtn, testNoti;
    private ImageButton backIcon, uploadIcon;
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

        inputWord = findViewById(R.id.textView1);
        inputText = findViewById(R.id.inputText);
    }

    private void initialiseWidgets() {
        updateTextViewArrayList();
        backIcon = findViewById(R.id.back_button);
        uploadIcon = findViewById(R.id.upload_mp3);
        confirmBtn = findViewById(R.id.confirm_button);
        testNoti = findViewById(R.id.testNoti);
        themeSwitch = findViewById(R.id.themeSwitch);
        parentView = findViewById(R.id.parentView);
        inputText = findViewById(R.id.inputText);
        textView6 = findViewById(R.id.textView6);
        constraintLayout = findViewById(R.id.constraintLayout);
        dismissSwitch = findViewById(R.id.dismissSwitch);
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
        //LightMode
        final int lightBackground = ContextCompat.getColor(this, R.color.lightBackground);
        final int lightBtn = ContextCompat.getColor(this, R.color.lightBtn);
        final int lightBox = ContextCompat.getColor(this, R.color.lightBox);
        final int lightText = ContextCompat.getColor(this, R.color.lightText);

        //DarkMode
        final int darkBackground = ContextCompat.getColor(this, R.color.darkBackground);
        final int darkBtn = ContextCompat.getColor(this, R.color.darkBtn);
        final int darkBox = ContextCompat.getColor(this, R.color.darkBox);
        final int darkText = ContextCompat.getColor(this, R.color.darkText);

        int color;
        int boxColor;

        //Dark Mode
        if(settings.getCustomTheme().equals(SettingsMenu.DARK_THEME)) {
            confirmBtn.setTextColor(darkBtn);
            GradientDrawable drawable = (GradientDrawable) confirmBtn.getBackground();
            drawable.setStroke(3, darkBtn);

            testNoti.setTextColor(darkBtn);
            GradientDrawable drawable1 = (GradientDrawable) testNoti.getBackground();
            drawable1.setStroke(3, darkBtn);

            GradientDrawable drawable2 = (GradientDrawable) constraintLayout.getBackground();
            drawable2.setColor(darkBox);

            ColorStateList thumbTint = ColorStateList.valueOf(darkText);
            ColorStateList trackTint = ColorStateList.valueOf(darkBtn);

            themeSwitch.setThumbTintList(thumbTint);
            themeSwitch.setTrackTintList(trackTint);
            dismissSwitch.setThumbTintList(thumbTint);
            dismissSwitch.setTrackTintList(trackTint);

            textView6.setText("Dark Mode"); //Theme text
            uploadIcon.setColorFilter(darkText);
            backIcon.setColorFilter(darkText);
            parentView.setBackgroundColor(darkBackground);
            inputText.setHintTextColor(darkText);
            themeSwitch.setChecked(true);
            color = darkText;
            boxColor = darkBox;
        }
        //Light Mode
        else {
            confirmBtn.setTextColor(lightBtn);
            GradientDrawable drawable = (GradientDrawable) confirmBtn.getBackground();
            drawable.setStroke(3, lightBtn);

            testNoti.setTextColor(lightBtn);
            GradientDrawable drawable1 = (GradientDrawable) testNoti.getBackground();
            drawable1.setStroke(3, lightBtn);

            GradientDrawable drawable2 = (GradientDrawable) constraintLayout.getBackground();
            drawable2.setColor(lightBox);

            ColorStateList thumbTint = ColorStateList.valueOf(lightText);
            ColorStateList trackTint = ColorStateList.valueOf(lightBtn);

            themeSwitch.setThumbTintList(thumbTint);
            themeSwitch.setTrackTintList(trackTint);
            dismissSwitch.setThumbTintList(thumbTint);
            dismissSwitch.setTrackTintList(trackTint);

            textView6.setText("Light Mode"); //Theme text
            uploadIcon.setColorFilter(lightText);
            backIcon.setColorFilter(lightText);
            parentView.setBackgroundColor(lightBackground);
            inputText.setHintTextColor(lightText);
            themeSwitch.setChecked(false);
            color = lightText;
            boxColor = lightBox;
        }

        for (TextView tV : textViews) {
            tV.setTextColor(color);
        }

        for (LinearLayout lly : LL) {
            GradientDrawable drawable3 = (GradientDrawable) lly.getBackground();
            drawable3.setColor(boxColor);
        }
    }

    private void updateTextViewArrayList() {
        this.textViews.add(findViewById(R.id.textView1));
        this.textViews.add(findViewById(R.id.textView2));
        this.textViews.add(findViewById(R.id.textView3));
        this.textViews.add(findViewById(R.id.textView4));
        this.textViews.add(findViewById(R.id.textView5));
        this.textViews.add(findViewById(R.id.textView6));

        this.LL.add(findViewById(R.id.linearLayout1));
        this.LL.add(findViewById(R.id.linearLayout2));
        this.LL.add(findViewById(R.id.linearLayout3));
    }

    public void back(View v) {
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
                .setContentText("This is a test notification. This means your notifications work!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(2001, builder.build());
        }
    }
}