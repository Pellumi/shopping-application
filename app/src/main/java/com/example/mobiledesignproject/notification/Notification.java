package com.example.mobiledesignproject.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mobiledesignproject.MainActivity;
import com.example.mobiledesignproject.R;

public class Notification {
    Context context;

    public Notification(Context context){
        this.context = context;
    }

    public void showNotification(String title, String message){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("1", "First Notification Channel", NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("This Notification is to display a notification");
            notificationManager.createNotificationChannel(channel);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1");
            builder.setContentTitle(title)
                    .setContentText(message);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.baseline_person_24)
                    .setAutoCancel(true)
                    .setOngoing(false);

            notificationManager.notify(1, builder.build());
        }
    }
}
