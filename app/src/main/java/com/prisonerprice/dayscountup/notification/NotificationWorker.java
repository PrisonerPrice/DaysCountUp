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

import com.maltaisn.icondialog.pack.IconPack;
import com.prisonerprice.dayscountup.App;
import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.view.MainActivity;

public class NotificationWorker extends Worker {

    private final static String CHANNEL_ID = "CHANNEL_ID";
    private final static int notificationID = 1105;
    private Context context;
    private IconPack iconPack;

    private String notificationTitle;
    private String notificationText;
    private int drawableID;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.notificationTitle = workerParams.getInputData().getString("NOTIFICATION_TITLE");
        this.notificationText = workerParams.getInputData().getString("NOTIFICATION_TEXT");
        this.drawableID = workerParams.getInputData().getInt("NOTIFICATION_DRAWABLE", -1);
        iconPack = ((App) getApplicationContext()).getIconPack();
    }

    @Override
    public Result doWork() {
        //createNotificationChannel(context);

        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent tapPendingIntent = PendingIntent.getActivity(context, 0, tapIntent, 0);

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(drawableID)
//                .setContentTitle(notificationTitle)
//                .setContentText(notificationText)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(tapPendingIntent)
//                .setAutoCancel(true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(tapPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationID, builder.build());

        return Result.success();
    }
}
