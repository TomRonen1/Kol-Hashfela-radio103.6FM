package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FCM extends com.google.firebase.messaging.FirebaseMessagingService {

    public FCM() {
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification();
    }

    @Override
    public void onDeletedMessages() {

    }

    /**
     *This function sends basic notification about the upcoming program, type -> 0
    */
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void sendNotification() {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Notification_Manager nm = new Notification_Manager();
        ProgramDatabase programDatabase = new ProgramDatabase(this);
        String text = "בשעה";
        text = text + programDatabase.getNextProgram().getTime() + " ";
        text = text + "בקול השפלה:" + " ";
        text = text + programDatabase.getNextProgram().getName() + ". ";
        nm.addNotification(this, "קול השפלה", text, R.drawable.fmfinalicon, 0, R.drawable.pauseicon3);
    }
}