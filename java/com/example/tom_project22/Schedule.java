package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Schedule {
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference databaseReference;


    public Schedule(){

    }

    /**
     * non used function, here in order to show how to write correctly to the server
     * @param programInfo
     */
    public static void writeToDay(ProgramInfo programInfo){
        String path = programInfo.getId()+ "_key";
        databaseReference = database.getReference().child(programInfo.getDay());
        databaseReference.child(path).setValue(programInfo);

    }

    /**
     *returns the correct string according to the day: int --> string
    */
    public static String getDayInWeek(int day){
        switch (day){
            case 1:
                return "sunday";
            case 2:
                return "monday";
            case 3:
                return "tuesday";
            case 4:
                return "wednesday";
            case 5:
                return "thursday";
            case 6:
                return "friday";
            case 7:
                return "saturday";
        }
        return "0";
    }

}
