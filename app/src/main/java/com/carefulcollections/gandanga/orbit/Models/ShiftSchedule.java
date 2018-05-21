package com.carefulcollections.gandanga.orbit.Models;

import java.util.Date;

/**
 * Created by Gandanga on 2018-05-21.
 */

public class ShiftSchedule {
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
    public int employee_id;

    public  ShiftSchedule(int id,int employee_id,String shift_title,Date start_date, Date end_date,Date shift_date,int shift_duration, String team_name,String start_time, String end_time,String shift_description){
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
        this.employee_id = employee_id;
    }
}
