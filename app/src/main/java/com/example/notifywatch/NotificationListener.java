package com.example.notifywatch;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.format.DateFormat;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;

public class NotificationListener extends NotificationListenerService {
    private String keyWord = "shift";
    //TODO save it after the app closes
    public static final String CHANNEL_ID= "Notification_Channel";

    @Override
    public void onListenerConnected () {
        super.onListenerConnected();
        createNotificationChannel();

        StatusBarNotification[] activeNotifications = getActiveNotifications();
        if (activeNotifications != null) {
            for (StatusBarNotification sbn : activeNotifications) {
                if (sbn != null && sbn.getNotification() != null) {
                    onNotificationPosted(sbn);
                }
            }
        }
    }

    @Override
    public void onListenerDisconnected () {
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        //TODO May need to add extra logic to prevent spamming of notifications
        //TODO Get all previous instances of keyWord from the same messenger app. not the latest one

        //Package name processing
        //https://stackoverflow.com/questions/64622288/get-name-of-app-which-created-the-notification
        String appLocation = sbn.getPackageName();
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(appLocation, PackageManager.GET_META_DATA);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ?
                pm.getApplicationLabel(ai) : appLocation);

        //Time processing
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sbn.getPostTime());
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();

        SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timePosted = time.format(new Date(sbn.getPostTime()));

        //Get the details
        Notification notification = sbn.getNotification();

        if (notification == null) {return;}
        Bundle extras = notification.extras != null ? notification.extras : new Bundle();

        String title = extras.getString(Notification.EXTRA_TITLE);
        String mainText = extras.getString(Notification.EXTRA_TEXT);
        String subText = extras.getString(Notification.EXTRA_SUB_TEXT);
        String bigText = extras.getString(Notification.EXTRA_BIG_TEXT);

        title = title != null ? title : "";
        mainText = mainText != null ? mainText : "";
        subText = subText != null ? subText : "";
        bigText = bigText != null ? bigText : "";

        if (title.isEmpty() && mainText.isEmpty()) {
            title = "No Title";
            mainText = "No message content";
        }

        this.keyWord = keyWord.toLowerCase();
        if (mainText.toLowerCase().contains(this.keyWord) || subText.toLowerCase().contains(this.keyWord) || bigText.toLowerCase().contains(this.keyWord)) {
            //Send Notification Logic Here
            //Also Show notifications in your app. As in create a bubble for it.
            if (!appLocation.equals(getPackageName())) {
                buildNotification(sbn, title, mainText, subText, bigText, applicationName, appLocation, date, timePosted);
            }
        }

    }

    public void buildNotification(StatusBarNotification sbn, String title, String mainText, String subText, String bigText, String applicationName, String appLocation, String date, String timePosted) {

        //https://stackoverflow.com/questions/3872063/how-to-launch-an-activity-from-another-application-in-android
        int REQUEST_CODE = Math.abs(appLocation.hashCode()) + Math.abs(sbn.getId());
        Intent launchApp = getPackageManager().getLaunchIntentForPackage(appLocation);
        if (launchApp == null) {
            launchApp = new Intent(this, MainActivity.class);
        }
        launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                REQUEST_CODE,
                launchApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        //Text Processing
        StringBuilder stringBuilder = new StringBuilder();
        LinkedHashSet<String> message = new LinkedHashSet<>();
        if (mainText != null && !mainText.isEmpty()) message.add(mainText);
        if (subText != null && !subText.isEmpty()) message.add(subText);
        if (bigText != null && !bigText.isEmpty()) message.add(bigText);

        for (String text : message) {
            stringBuilder.append(text).append("\n");
        }
        String finalString = stringBuilder.toString().trim();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.alert) // Icon
                .setContentTitle(applicationName)
                .setContentText(title + ": " + finalString)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title + ":\n" + finalString + "\n[Posted at: " + date + " | " + timePosted + "]")) //Expansion
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Check if notifications are still allowed to be posted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            //Generates unique IDS
            int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            //Can manually set if user does not want spam
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(notificationId, builder.build());
            //TODO Call Dismiss notification Optional HERE. Do this in settings
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
