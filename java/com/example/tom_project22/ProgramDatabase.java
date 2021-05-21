package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;

public class ProgramDatabase  extends SQLiteOpenHelper implements ValueEventListener {
    static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference databaseReference;
    public Context context;
    public static int amountOfRows;
    public static final String DATABASE_NAME = "ProgramInformation.db";
    public static final String TABLE_NAME = "Program_Data";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "AUTHOR";
    public static final String COL_4 = "TIMEHOURS";
    public static final String COL_5 = "TIMEMINUTES";
    public static final String COL_6 = "DAY";


    public ProgramDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    /**
     * inserts all of the data from the server to the table
    */
    public void insertAllData(){
        for(int i = 1 ; i < 8 ; i++ ){
            databaseReference = database.getReference().child(Schedule.getDayInWeek(i));
            databaseReference.addValueEventListener(this);
            //Log.d("Skiped day number.....",""+i+"......day");
        }

    }

    /**
     *returns the next program according to the day
    */
    public ProgramInfo getNextProgram(){
        Date date = Calendar.getInstance().getTime();
        int day = date.getDay();
        day += 1;
        //Log.d("entered.....","dabase get next program..........day"+day);
        return getDataForDay(String.valueOf(day));
    }

    /**
     *when the answer for the firebase query is ready the results are shown up here
    */
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for(DataSnapshot snapshot1 :snapshot.getChildren()){
                try{
                    //Log.d("enterd onDataChanged"," entered write mode");
                    ProgramInfo newProgram = snapshot1.getValue(ProgramInfo.class);
                    insertData(String.valueOf(newProgram.getId()),newProgram.getName(),newProgram.getAuthor(),newProgram.getHourOfStream(),newProgram.getMinutesOfStream(),newProgram.getDay());
                }catch (Exception e){
                    //Log.d("fell in dataChanged"," the code fell, reason"+ e.getMessage());
                    Toast.makeText(context," Code fell error 3.",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }


    public ProgramDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID TEXT,NAME TEXT,AUTHOR TEXT,TIMEHOURS TEXT,TIMEMINUTES TEXT,DAY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String name, String author, String hourOfStream, String minutesOfStream, String day) {
        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" WHERE "+COL_1+"=?", new String[]{id});
            if(cursor.getCount()>0){
                //Log.d(" inserter base","wont insert data - already exist for id "+id);
                return false;
            }
            else{
                db = getReadableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(COL_1,id);
                contentValues.put(COL_2,name);
                contentValues.put(COL_3,author);
                contentValues.put(COL_4,hourOfStream);
                contentValues.put(COL_5,minutesOfStream);
                contentValues.put(COL_6,day);

                amountOfRows++;

                long result = db.insert(TABLE_NAME,null ,contentValues);
                if(result == -1)
                    return false;
                else
                    return true;
            }
        }catch (Exception e){
            Toast.makeText(context," Code fell error 5.",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     *the algorithm that decides what program to return
    */
    private ProgramInfo getDataForDay(String day){
        Date date = Calendar.getInstance().getTime();
        int time = date.getHours();
        ProgramInfo infoMarker = new ProgramInfo(0,null,23.59, null,null);
        //Log.d("entered.....","get data for day......time-"+time+"  the day is-"+day);
        try {
            String[] columns = {COL_1, COL_2, COL_3, COL_4, COL_5, COL_6};
            SQLiteDatabase db = getReadableDatabase();
            String selection = COL_6 + "=?";
            String[] selectionArgs = {Schedule.getDayInWeek(Integer.parseInt(day))};
            //Log.d("reached for","........ line 145....");
            Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, COL_2);
            //Log.d("reached for","........ line 147....");
            if(!(cursor.getCount()>0)){
                Log.d("reached for","..cursor is empty");
            }
            if (cursor.moveToFirst()){
                //Log.d("reached for","........ line 149....");
                while(!cursor.isAfterLast()){
                    //Log.d("178....","entered the while loop");
                    //Log.d("179:...","the time looked for: "+Integer.parseInt(cursor.getString(3)));
                    //Log.d("180:...","the time now: "+time);
                    if (Integer.parseInt(cursor.getString(3)) >= time) {
                        if (Integer.parseInt(cursor.getString(3)) == time) {
                            Player_frag.IS_FUTURE_PROGRAM = false;
                            Player_frag.IS_NO_MORE_PROGRAM = false;
                            String timestr = cursor.getString(3) + "." + cursor.getString(4);
                            double timedb = Double.parseDouble(timestr);
                            ProgramInfo pr = new ProgramInfo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_1))), cursor.getString(cursor.getColumnIndex(COL_6)),
                                    timedb, cursor.getString(cursor.getColumnIndex(COL_3)), cursor.getString(cursor.getColumnIndex(COL_2)));
                            Log.d("!!!Found program!!!", "the program that found.....:" + pr.toString());
                            return (pr);
                        }
                        String timestr = cursor.getString(3) + "." + cursor.getString(4);
                        double timedb = Double.parseDouble(timestr);
                        ProgramInfo pr = new ProgramInfo(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL_1))), cursor.getString(cursor.getColumnIndex(COL_6)),
                                timedb, cursor.getString(cursor.getColumnIndex(COL_3)), cursor.getString(cursor.getColumnIndex(COL_2)));
                        //Log.d("ProgramInfo has created", "The object:.....(196)........:" + pr.toString());
                        //Log.d("didnt enter yet IF", "the time that was saved already (infoMarker) " + infoMarker.getTime());
                        if(pr.getTime() < infoMarker.getTime()){
                            infoMarker = pr;
                            //Log.d("entered if statement", " the new object time: " + pr.getTime());
                        }
                    }
                    cursor.moveToNext();
                }
            }
            if(infoMarker.getId() != 0){
                Player_frag.IS_NO_MORE_PROGRAM = false;
                Player_frag.IS_FUTURE_PROGRAM = true;
                Log.d("Found program", "the program that found - but not now - future one.....:" + infoMarker.toString());
                return infoMarker;
            }
            else {
                Player_frag.IS_NO_MORE_PROGRAM = true;
            }

        }catch (Exception e){
            //Log.d("the finder....", "fell down during looking for.....:");
            Player_frag.IS_NO_MORE_PROGRAM = true;
        }
        Player_frag.IS_NO_MORE_PROGRAM = true;
        Log.d("the not finder.", "not found any data when looking for.....:");
        return new ProgramInfo(0,null,0, null,null);
    }



    public Cursor getData(String id){
        String[] columns = { COL_1,COL_2,COL_3,COL_4,COL_5,COL_6};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_1 + "=?";
        String[] selectionArgs = {id};
        Cursor res = db.query(TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        return res;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id, String name, String author, String hours, String minutes, String day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,author);
        contentValues.put(COL_4,hours);
        contentValues.put(COL_5,minutes);
        contentValues.put(COL_6,day);
        db.update(TABLE_NAME, contentValues, COL_1+" = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        amountOfRows--;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    /**
     *deletes all of the data on order to update from the server
    */
    public void deleteAllData () {
        amountOfRows--;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
}
