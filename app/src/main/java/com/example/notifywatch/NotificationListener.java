package com.example.notifywatch;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class NotificationListener extends NotificationListenerService {
    private String keyWord = "shift";
    //TODO save it after the app closes
    public static final String CHANNEL_ID= "Notification_Channel";

    @Override
    public void onListenerConnected () {
        super.onListenerConnected();
        createNotificationChannel();

        StatusBarNotification[] activeNotifications = getActiveNotifications();
        for (StatusBarNotification sbn : activeNotifications) {
            onNotificationPosted(sbn);
        }

    }

    @Override
    public void onListenerDisconnected () {
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        //Package name processing
        //TODO Keep an eye out for this
        //https://stackoverflow.com/questions/64622288/get-name-of-app-which-created-the-notification
        String appLocation = sbn.getPackageName();
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(appLocation, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ?
                pm.getApplicationLabel(ai) : "(unknown)");

        //Time processing
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sbn.getPostTime());
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();

        //Get the details
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        String title = extras.getString(Notification.EXTRA_TITLE);
        String mainText = extras.getString(Notification.EXTRA_TEXT);
        String subText = extras.getString(Notification.EXTRA_SUB_TEXT);
        String bigText = extras.getString(Notification.EXTRA_BIG_TEXT);

        if (title == null) title = "";
        if (mainText == null) mainText = "";
        if (subText == null) subText = "";
        if (bigText == null) bigText = "";

        mainText = mainText.toLowerCase();
        subText = mainText.toLowerCase();
        bigText = mainText.toLowerCase();

        if (mainText.contains(this.keyWord) || subText.contains(this.keyWord) || bigText.contains(this.keyWord)) {
            //Send Notification Logic Here
            //Also Show notifications in your app. As in create a bubble for it.
            if (!appLocation.equals(getPackageName())) {
                buildNotification(title, mainText, subText, bigText, applicationName, date);
            }
        }

    }

    public void buildNotification(String title, String mainText, String subText, String bigText, String applicationName, String date) {

//        Intent intent = new Intent(this, MainActivity.class); //This changes to another screen for example AlertDetails.
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //Text Processing
        StringBuilder stringBuilder = new StringBuilder();
        LinkedHashSet<String> message = new LinkedHashSet<String>();
        if (mainText != null && !mainText.isEmpty()) message.add(mainText);
        if (subText != null && !subText.isEmpty()) message.add(subText);
        if (bigText != null && !bigText.isEmpty()) message.add(bigText);

        for (String text : message) {
            stringBuilder.append(text).append("\n");
        }
        String finalString = stringBuilder.toString().trim();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.alert) //Icon
                .setContentTitle(applicationName)
                .setContentText(finalString + "\n[Posted at: " + date + "]")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that fires when the user taps the notification.
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Check if notifications are still allowed to be posted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1001, builder.build());
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
