package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;


public class PlayPauseReciever extends BroadcastReceiver {
    /**
     *turns on or off the radio and wakes the observer
    */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        Player_frag.changeButtonStatus.setPlay(true);
        Player_frag.radioExoPlayer.startStop();
    }
}

