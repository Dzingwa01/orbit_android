package com.carefulcollections.gandanga.mishift.Models;

import android.text.format.Time;

import java.util.Date;

/**
 * Created by Gandanga on 2018-04-17.
 */

public class Task {
    public int id;
    public String name;
    public String description;
    public String picture_url;
    public Date start_date;
    public int creator_id;
    public Date end_date;
    public String start_time;
    public String end_time;
    public Date shift_date;

    public Task(int id, String name, String description, String picture_url, Date start_date, Date shift_date, int creator_id, Date end_date,String start_time, String end_time){
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture_url = picture_url;
        this.start_date = start_date;
        this.creator_id = creator_id;
        this.end_date = end_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.shift_date = shift_date;
    }
}
