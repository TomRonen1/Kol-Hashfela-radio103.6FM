package com.example.tom_project22;
/**
 * Tom Ronen
 * final project -- Radio Kol Hashfela -- 2021
 */
import java.util.HashMap;
import java.util.Map;

/**
 * the radio-program class
 * includes basic set & get functions, toString
 */
public class ProgramInfo {
    private String day;
    private double time;
    private String author;
    private String name;
    private int id;

    public ProgramInfo(int id,String day, double time, String author, String name){
        this.id = id;
        this.day = day;
        this.time = time;
        this.author = author;
        this.name = name;
    }

    public ProgramInfo(){

    }

    @Override
    public String toString() {
        return "ProgramInfo{" +
                "id=" + id + '\''+
                ", day='" + day + '\''  +
                ", time=" + time + '\''+
              ", author='" + author + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId(){
        return this.id;
    }

    public void setDay(String day){
        this.day = day;
    }
    public void setTime(double time){
        this.time = time;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public void setName(String name){
        this.name = name;
    }

    public double getTime(){
        return this.time;
    }

    public String getDay(){
        return this.day;
    }
    public String getAuthor(){
        return this.author;
    }
    public String getName(){
        return this.name;
    }
    public String getHourOfStream(){
       String time = (""+ this.time);
       String [] hours = time.split("\\.");
        return hours[0];
    }
    public String getMinutesOfStream(){
        String time = (""+ this.time);
        String [] hours = time.split("\\.");
        return hours[1];
    }


}
