package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

/**
 * activity class
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    SharedPreferences sp;

    public Fragment fragment;
    ProgramDatabase programDatabase = new ProgramDatabase(this);

    /**
     * the onCreate of the activity
     * check if needs to update the database
     * set the variables, view, fragment
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*
         algorithm that checks if the app needs to update its database:
        */
        Date date = Calendar.getInstance().getTime();
        int monthNow= date.getMonth();
        sp = this.getPreferences(Context.MODE_PRIVATE);
        int month = -1;
        month = sp.getInt(getResources().getString(R.string.month),-1);

        if(month == -1){//if user enters for the first time ever to the app:
            Player_frag.IS_FIRST_TIME = true;
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getResources().getString(R.string.month),monthNow);
            editor.putBoolean(getString(R.string.is_first_time),true);
            editor.apply();
            programDatabase.deleteAllData();
            programDatabase.insertAllData();
        }
        else if(month != monthNow){//if the app needs to update data with server:
            Player_frag.IS_FIRST_TIME = true;
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getResources().getString(R.string.month),monthNow);
            editor.putBoolean(getString(R.string.is_first_time),true);
            editor.apply();
            programDatabase.deleteAllData();
            programDatabase.insertAllData();
        }
        else{
            Player_frag.IS_FIRST_TIME = false;
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.is_first_time),false);
            editor.apply();
        }



        /*
           setting up basic variables for the activity: drawer, toolbar,fragment view and more:
        */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fb, new Player_frag()).commit();

    }

    /**
     *makes sure the code knows if the program has been visited and it does'nt necessarily have to
     *update the database next time.
    */
    @Override
    protected void onDestroy() {

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.is_first_time),false);
        editor.apply();
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        Log.d("called onDestroy","the value of is first time:"+ sharedPreferences.getBoolean(getResources().getString(R.string.is_first_time),false));
        super.onDestroy();
    }


    /**
     *the functions creates the dialog if needed
    */
    public void createDialog(){
        new AlertDialog.Builder(this)
                .setTitle("103.6FM")
                .setMessage("Are you sure you want to exit?")

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.exiticon)
                .show();
    }

    /**
     * when back button is pressed the app needs to take actions:
     * first option is to close the drawer if open,
     * else open the dialog in case the user wants to exit.
    */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            createDialog();
        }
    }

    /**
     * creates the menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    /**
     * navigates between the different fragments
    */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.about){
            fragment = new about_frag();
        }
        else if(id==R.id.player_page){

            fragment = new Player_frag();
        }
         else if (id == R.id.nav_share) {
        Intent i= new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT,"come check: https://www.1036kh.com/");
        i.setType("text/plain");
        startActivity(i);
        //"https://play.google.com/store/apps/details?id=com.arbel03.a1036fm"
        }
        if(fragment!=null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fb, fragment);
            ((FragmentTransaction) ft).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {}
}
