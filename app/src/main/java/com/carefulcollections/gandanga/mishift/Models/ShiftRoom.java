package com.carefulcollections.gandanga.mishift.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by Gandanga on 2018-09-07.
 */
@Entity(tableName = "shifts")
public class ShiftRoom {
    @NonNull
    @PrimaryKey
    public int id;
    public String shift_title;
    public Date start_date;
    public Date end_date;
    public int shift_duration;
    public String team_name;
    public String start_time;
    public String end_time;
    public String shift_description;
    public Date shift_date;

    public  ShiftRoom(int id,String shift_title,Date start_date, Date end_date,Date shift_date,int shift_duration, String team_name,String start_time, String end_time,String shift_description){
        this.shift_title =shift_title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.shift_duration = shift_duration;
        this.team_name = team_name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.shift_description = shift_description;
        this.shift_date = shift_date;
        this.id = id;
    }

    public int getShiftId(){return id;}
    public String getShiftTitle(){
        return shift_title;
    };
    public Date getEndDate(){return end_date;};
    public int getShiftDuration(){return shift_duration;};
    public String getTeamName(){return team_name;};
    public String getStartTime(){return start_time;};
    public String getEndTime(){return shift_description;};
    public String getShiftDescription(){return start_time;};
    public Date getShiftDate(){return shift_date;};
}
