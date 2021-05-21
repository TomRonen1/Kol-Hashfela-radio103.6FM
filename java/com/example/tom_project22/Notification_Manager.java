package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.core.app.NotificationCompat.DEFAULT_LIGHTS;
import static androidx.core.app.NotificationCompat.FLAG_AUTO_CANCEL;
import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;


public class Notification_Manager extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    NotificationManagerCompat  notificationManager;


    /**
     * sends the basic notifications according to the given arguments
     * also moving the notification to the notifyMedia function if the type == 1
    */
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void addNotification(Context context, String title, String text, int icon, int type,int button) {

        createNotificationChannel(context);
        if(type == 0){
            notificationManager = NotificationManagerCompat.from(context);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap icon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel1")
                    .setSmallIcon(R.drawable.fmfinalicon)
                    .setContentTitle(title)
                    .setLargeIcon(icon2)
                    .setColorized(true)
                    .setColor(Color.argb(100,10,31,154))
                    .setContentText(text)
                    .setSubText("קול השפלה") //need to be changed
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setVibrate(new long[]{0L})
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(100, builder.build());

        }
        else if(type == 1){
            notifiMedia(context, title, text, icon, button);
        }

    }

    /**
     *sends the media type notification
    */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifiMedia(Context context, String title, String text, int icon, int button){
        Intent notificationIntent = new Intent();
        notificationIntent.setAction("android.intent.action.STARTSTOP");
        notificationIntent.putExtra("playPause","Notice me senpai!");
        if(context == null){
            System.out.println("context is null");
        }
        Intent intent2 = new Intent(context, MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent toggleIntent = new Intent(context, PlayPauseReciever.class);
        toggleIntent.setAction("pause");
        PendingIntent pendingToggleIntent = PendingIntent.getBroadcast(context, 0, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = NotificationManagerCompat.from(context);
        Bitmap icon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel1")
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setLargeIcon(icon2)
                .setContentText(text)
                .setDefaults(DEFAULT_LIGHTS)
                .setColorized(true)
                .setColor(Color.argb(100,6,83,146))
                .addAction(button, "Pause",pendingToggleIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0))
                .setSubText("קול השפלה") //need to be changed
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pending);
        notificationManager.notify(100, builder.build());

    }

    /**
     *cancels the notification
    */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void cancelNoification(Context context){
        try{
            NotificationManager notificationManager2 = context.getSystemService(NotificationManager.class);
            notificationManager2.cancel(100);
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    /**
     *creates the channel for the notification
    */
    private void createNotificationChannel(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "radioChannel";
            String description = "103.6FM KOL HASHFELA";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            channel.setSound(null,null);
        }
    }

}