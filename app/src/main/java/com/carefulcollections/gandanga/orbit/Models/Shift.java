package com.carefulcollections.gandanga.orbit.Models;

import java.util.Date;

/**
 * Created by Gandanga on 2018-04-17.
 */

public class Shift {

    public String shift_title;
    public Date start_date;
    public Date end_date;
    public int shift_duration;
    public String team_name;
    public  Shift(String shift_title,Date start_date, Date end_date,int shift_duration, String team_name){
        this.shift_title =shift_title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.shift_duration = shift_duration;
        this.team_name = team_name;
    }
}
