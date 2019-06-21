package com.example.newsapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.example.newsapp.R;
import com.example.newsapp.view.activity.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

class NotificationUtils {
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final int NOTIFICATION_ID = 1;

    static void sendNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(context.getString(R.string.app_name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,0);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.new_news_are_available))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.tap_to_see)))
                .setSmallIcon(R.drawable.ic_news)
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setVibrate(null)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
