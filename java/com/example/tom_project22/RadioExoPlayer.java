package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.media2.exoplayer.external.ExoPlaybackException;
import androidx.media2.exoplayer.external.ExoPlayerFactory;
import androidx.media2.exoplayer.external.PlaybackParameters;
import androidx.media2.exoplayer.external.Player;
import androidx.media2.exoplayer.external.SimpleExoPlayer;
import androidx.media2.exoplayer.external.Timeline;
import androidx.media2.exoplayer.external.source.ExtractorMediaSource;
import androidx.media2.exoplayer.external.source.MediaSource;
import androidx.media2.exoplayer.external.source.TrackGroupArray;
import androidx.media2.exoplayer.external.trackselection.AdaptiveTrackSelection;
import androidx.media2.exoplayer.external.trackselection.DefaultTrackSelector;
import androidx.media2.exoplayer.external.trackselection.TrackSelectionArray;
import androidx.media2.exoplayer.external.trackselection.TrackSelector;
import androidx.media2.exoplayer.external.upstream.DataSource;
import androidx.media2.exoplayer.external.upstream.DefaultDataSourceFactory;
import androidx.media2.exoplayer.external.util.Util;


class RadioExoPlayer implements AudioManager.OnAudioFocusChangeListener, Player.EventListener {

    static SimpleExoPlayer player;
    Context context;
    public static boolean isOn = false;

    /**
     * the builder function
     */
    @SuppressLint("RestrictedApi")
    public RadioExoPlayer(Context context) {
        this.context = context;
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(null);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    /**
     *decides whether to start or stop the radio according to its state and isOn
    */
    public void startStop(){
        if(!isOn){
            isOn =true;
            startPlaying();
        }
        else{
            isOn = false;
            stopPlaying();
        }
    }


    /**
     *starts the player
    */
    @SuppressLint("RestrictedApi")
    public void startPlaying() {
        AudioManager am = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for playback
        int result = am.requestAudioFocus(this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            String userAgent = Util.getUserAgent(context, "103fm-android");
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent, null);
            MediaSource audioStream = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("https://radio.streamgates.net/stream/1036kh"));
            player.addListener(this);
            player.prepare(audioStream);

            player.setPlayWhenReady(true);
        }
    }

    /**
     *stops the player
    */
    @SuppressLint("RestrictedApi")
    public void stopPlaying(){
        //player.release();
        player.stop();
    }


    @Override
    public void onTimelineChanged(Timeline timeline, int reason) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
