package com.example.tom_project22;

/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * main fragment - player, social networks, up coming titles.
 */
public class Player_frag extends Fragment implements View.OnClickListener, Observer, ValueEventListener {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference databaseReference;

    public final String FACEBOOK_ID = "552170191554766";

    public ImageButton playButton;

    ImageButton buttonTwitter;
    ImageButton buttonSoundcloud;
    ImageButton buttonInstegram;
    ImageButton buttonWhatsapp;
    ImageButton buttonYoutube;
    ImageButton buttonFacebook;
    ImageButton buttonArchive;
    ImageButton buttonWebsite;

    ImageView liveBtn;

    Animation animation;

    SharedPreferences sp;

    public static RadioExoPlayer radioExoPlayer;

    public static boolean IS_FUTURE_PROGRAM = false;
    public static boolean IS_NO_MORE_PROGRAM = false;
    public static boolean IS_FIRST_TIME;

    TextView tv;

    public Activity mActivity;

    public View view;

    Handler handler=new Handler();

    ProgramDatabase programDatabase;

    public String base;

    public static ChangeButtonStatus changeButtonStatus = new ChangeButtonStatus();

    public static Notification_Manager nm;

    static PlayPauseReciever playPauseReciever = new PlayPauseReciever();


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * set the variables and set the buttons according to their last state
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().getSupportFragmentManager().popBackStack();

        view = inflater.inflate(R.layout.player_frag, container, false);

        mActivity = getActivity();

        sp = mActivity.getPreferences(Context.MODE_PRIVATE);
        IS_FIRST_TIME = sp.getBoolean(getResources().getString(R.string.is_first_time),true);

        if(IS_FIRST_TIME){
            Toast.makeText(mActivity,"Updating programs with server", Toast.LENGTH_LONG).show();
        }

        animation = AnimationUtils.loadAnimation(getActivity(),R.anim.blinker);
        liveBtn = view.findViewById(R.id.imageView3);


        if(radioExoPlayer == null){
            radioExoPlayer = new RadioExoPlayer(mActivity);
        }
        if(nm == null){
            nm = new Notification_Manager();
        }
        programDatabase = new ProgramDatabase(mActivity);

        playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(this);

        try{
            if(radioExoPlayer.isOn){
                playButton.setImageResource(R.drawable.pause);
                liveBtn.startAnimation(animation);
            }
            else{
                playButton.setImageResource(R.drawable.play);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        tv = view.findViewById(R.id.tv);
        tv.setText(getResources().getString(R.string.moving_text2));
        tv.setVisibility(View.VISIBLE);
        initializeTV();
        getNextProgram();

        getActivity().startService(new Intent(getActivity(), ListenerService.class));

        TextView timePresenter = (TextView)view.findViewById(R.id.time_presenter);
        timePresenter.setVisibility(View.INVISIBLE);
        timePresenter.setText(returnTimeTitle());
        timePresenter.setVisibility(View.VISIBLE);

        buttonFacebook = view.findViewById(R.id.facebook_button);
        buttonFacebook.setOnClickListener(this);

        buttonTwitter = view.findViewById(R.id.twitter_button);
        buttonTwitter.setOnClickListener(this);

        buttonInstegram = view.findViewById(R.id.gmail_button);
        buttonInstegram.setOnClickListener(this);

        buttonWhatsapp = view.findViewById(R.id.whatsapp_button);
        buttonWhatsapp.setOnClickListener(this);

        buttonYoutube = view.findViewById(R.id.youtube_button);
        buttonYoutube.setOnClickListener(this);

        buttonSoundcloud = view.findViewById(R.id.soundcloud_button);
        buttonSoundcloud.setOnClickListener(this);

        buttonArchive = view.findViewById(R.id.archive);
        buttonArchive.setOnClickListener(this);

        buttonWebsite = view.findViewById(R.id.website);
        buttonWebsite.setOnClickListener(this);

        observe(changeButtonStatus);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.tom_project22");

        getActivity().registerReceiver(playPauseReciever, filter);

        return view;
    }

    /**
     *initialize the text which presents the next or current program
    */
    private void initializeTV(){
        tv.setSelected(true);  // Set focus to the textview
        tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    /**
     *returns the correct string depends on the time
    */
    private String returnTimeTitle(){
        Date date = new Date();
        int hour = date.getHours();
        if(hour >= 21 && hour <= 23 ||  hour >= 0 && hour < 5){
            return("לילה טוב");
        }
        else if(hour >= 5 && hour < 12){
            return ("בוקר טוב");
        }
        else if(hour >= 12 && hour < 15){
            return ("צהריים טובים");
        }
        else if(hour >= 15 && hour < 18){
            return ("אחר הצהריים נעימים");
        }
        else  if(hour >= 18 && hour <21){
            return ("ערב טוב");
        }
        else {
            return "";
        }
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    /**
     * determines the string of the "next program" text view
    */
    public boolean getNextProgram(){
        Log.d("................","is first time........."+IS_FIRST_TIME);
        if(IS_FIRST_TIME){
            SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.is_first_time),false);
            editor.apply();
            Date date = Calendar.getInstance().getTime();
            int day = date.getDay();
            day += 1;
            databaseReference = database.getReference().child(Schedule.getDayInWeek(day));
            databaseReference.addValueEventListener(this);//changed

            Log.d(".......","reached the finish of ISFIRSTTIME........."+IS_FIRST_TIME);
            IS_FIRST_TIME = false;
            return true;
        }
        else{
            base= mActivity.getResources().getString(R.string.moving_text2)+"  ";
            try{
                ProgramInfo newProgram = programDatabase.getNextProgram();

                Log.d("................","entered the try........."+IS_NO_MORE_PROGRAM);
                if(IS_NO_MORE_PROGRAM){
                    IS_NO_MORE_PROGRAM = false;
                    tv.setText(base);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(base);
                    initializeTV();
                    return false;
                }
                if(IS_FUTURE_PROGRAM){
                    Log.d("................","found future program!........."+newProgram.getHourOfStream());
                    base = mActivity.getResources().getString(R.string.moving_text2)+"  ";
                    base  =  ""+base+mActivity.getResources().getString(R.string.moving_text3next)+" ";
                    base = ""+base+newProgram.getName()+", ";
                    base  = ""+base+mActivity.getResources().getString(R.string.moving_text5)+" ";
                    String realTime = newProgram.getHourOfStream()+":"+newProgram.getMinutesOfStream();
                    base = ""+base+realTime+". ";
                    IS_FUTURE_PROGRAM = false;
                }
                else {
                    Log.d("................","found current program!........."+newProgram.getHourOfStream());
                    base = mActivity.getResources().getString(R.string.moving_text2)+"  ";
                    base  =  ""+base+mActivity.getResources().getString(R.string.moving_text3)+" ";
                    base = ""+base+newProgram.getName()+", ";
                    String str = newProgram.getAuthor();
                    if(!str.equals("") && !str.equals(" ") && !str.equals("  ")){
                        base  = ""+base+mActivity.getResources().getString(R.string.moving_text4)+" ";
                        base  = ""+base+newProgram.getAuthor()+". ";
                    }
                }
                tv.setText(base);
                tv.setVisibility(View.VISIBLE);
                tv.setText(base);
                initializeTV();
                return true;
            }catch (Exception e){
                Log.d("fell!..........","fell when retrieving data!.........");
                e.printStackTrace();
                base = mActivity.getResources().getString(R.string.moving_text2);
                Toast.makeText(mActivity," Code fell error 1.",Toast.LENGTH_LONG).show();

            }finally {
                tv.setText(base);
                tv.setVisibility(View.VISIBLE);
                tv.setText(base);
                initializeTV();
                return false;
            }

        }
    }

    /**
     * delay of 1 minute in order to updates the "tv" every 1 minute
    */
    Runnable updateTextRunnable=new Runnable(){
        public void run() {
            getNextProgram();
            handler.postDelayed(this, 60000);
        }
    };


    /**
     * when searching the firebase the results are here
    */
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Date date = Calendar.getInstance().getTime();
        int time2 = date.getHours();
        base = mActivity.getResources().getString(R.string.moving_text2)+"  ";
        try{
            ProgramInfo infoMarker = new ProgramInfo(0,null,23.59, null,null);
            boolean done = false;
            for(DataSnapshot snapshot1 :snapshot.getChildren()){
                Log.d("................","enterd........."+time2);
                ProgramInfo newProgram = snapshot1.getValue(ProgramInfo.class);
                Log.d("................","not yet........."+newProgram.getHourOfStream());
                if(newProgram.getHourOfStream().equals(String.valueOf(time2))){
                    base  =  ""+base+mActivity.getResources().getString(R.string.moving_text3)+" ";
                    base = ""+base+newProgram.getName()+", ";
                    String str = newProgram.getAuthor();
                    if(!str.equals("") && !str.equals(" ") && !str.equals("  ")){
                        base  = ""+base+mActivity.getResources().getString(R.string.moving_text4)+" ";
                        base  = ""+base+newProgram.getAuthor()+". ";
                    }
                    tv.setText(base);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText(base);
                    initializeTV();
                    done = true;
                    IS_NO_MORE_PROGRAM = false;
                }
            }
            for(DataSnapshot snapshot1 :snapshot.getChildren()){
                ProgramInfo newProgram = snapshot1.getValue(ProgramInfo.class);
                if(Integer.parseInt(newProgram.getHourOfStream())> time2)
                {
                    done = true;
                    if(newProgram.getTime() < infoMarker.getTime()){
                        infoMarker  = newProgram;
                    }
                }
            }
            if(done){
                base  =  ""+base+mActivity.getResources().getString(R.string.moving_text3next)+" ";
                base = ""+base+infoMarker.getName()+", ";
                base  = ""+base+mActivity.getResources().getString(R.string.moving_text5)+" ";
                String realTime = infoMarker.getHourOfStream()+":"+infoMarker.getMinutesOfStream();
                base = ""+base+realTime+". ";
                tv.setText(base);
                tv.setVisibility(View.VISIBLE);
                tv.setText(base);
                tv.setSelected(true);  // Set focus to the textview
                tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                initializeTV();
                done = true;
                IS_NO_MORE_PROGRAM = false;
            }
            else{
                tv.setText(mActivity.getResources().getString(R.string.moving_text2));
                tv.setVisibility(View.VISIBLE);
                initializeTV();
            }
            handler.post(updateTextRunnable);
        }catch (Exception e){
            Toast.makeText(mActivity," Code fell error 9.",Toast.LENGTH_LONG).show();
        }finally {
            tv.setText(" אתם מאזינים לרדיו קול השפלה בתדר 103.6FM.");
            tv.setVisibility(View.VISIBLE);
            initializeTV();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {}

    /**
    * the observer when in action calls this
    * function which changes the buttons on screen correctly
    */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void update(Observable observable, Object o) {

        if (!radioExoPlayer.isOn) {
            liveBtn.startAnimation(animation);
            playButton.setImageResource(R.drawable.pause);
            nm.addNotification(mActivity, "103.6FM","103.6FM is now playing", R.drawable.fmfinalicon ,1,R.drawable.pauseicon3);
        } else {
            liveBtn.clearAnimation();
            playButton.setImageResource(R.drawable.play);
            nm.addNotification(mActivity, "103.6FM","103.6FM is paused", R.drawable.fmfinalicon ,1,R.drawable.pkayicon3);
        }

    }

    /**
     * adds the observer to this fragment
    */
    public void observe(Observable observable){
        observable.addObserver(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity =(Activity) context;
        }
    }


    /**
     * play or pause the radio, also changes the screen and the notification
     * according to its status
    */
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void playPause(){

        try{
            if (!radioExoPlayer.isOn) {
                playButton.setImageResource(R.drawable.pause);
                nm.addNotification(mActivity, "103.6FM","103.6FM is now playing", R.drawable.fmfinalicon ,1,R.drawable.pauseicon3);
                radioExoPlayer.startStop();
                liveBtn.startAnimation(animation);
            } else {
                liveBtn.clearAnimation();
                playButton.setImageResource(R.drawable.play);
                nm.addNotification(mActivity, "103.6FM","103.6FM is paused", R.drawable.fmfinalicon ,1,R.drawable.pkayicon3);
                radioExoPlayer.startStop();
            }
        }
        catch (Exception e){
            Toast.makeText(mActivity," Code fell error 2.",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * opens the app from the given url
    */
    public void openApp(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * when button is clicked this function is called -
     * decides what to do according to the pressed button
    */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View view) {

        if(view == buttonTwitter){
            openApp("https://twitter.com/1036fm");
        }
        else if(view == buttonFacebook){
            openApp("https://www.facebook.com/"+FACEBOOK_ID);
        }
        else if(view == buttonSoundcloud){
            openApp("https://www.instagram.com/1036kh/");
        }
        else if(view == buttonInstegram){
            openApp("mailto://1036kh@gmail.com");
        }
        else if(view == buttonWhatsapp){
            openApp("https://api.whatsapp.com/send?phone=972585851036");
        }
        else if(view == buttonYoutube){
            openApp("https://www.youtube.com/channel/UCsayw00nuP5AGE4AdQyHYtw");
        }
        else if(view == buttonArchive){
            openApp("https://www.1036kh.com/%D7%94%D7%90%D7%A8%D7%9B%D7%99%D7%95%D7%9F");

        }
        else if(view == buttonWebsite){
            openApp("https://www.1036kh.com/");
        }
        else {
            if (view.getId() == R.id.play_button) {
                playPause();
            }
        }

    }

}
