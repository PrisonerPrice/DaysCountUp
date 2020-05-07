package com.prisonerprice.dayscountup.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.prisonerprice.dayscountup.view.MainActivity;

public class NotificationWorker extends Worker {

    private final static String CHANNEL_ID = "CHANNEL_ID";
    private Context context;
    private int notificationID;
    private String notificationTitle;
    private String notificationText;
    private int drawableID;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams, int notificationID, String notificationTitle, String notificationText, int drawableID) {
        super(context, workerParams);
        this.context = context;
        this.notificationID = notificationID;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.drawableID = drawableID;
    }

    @Override
    public Result doWork() {
        createNotificationChannel(context);

        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent tapPendingIntent = PendingIntent.getActivity(context, 0, tapIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(drawableID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(tapPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationID, builder.build());

        return Result.success();
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name_2";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
