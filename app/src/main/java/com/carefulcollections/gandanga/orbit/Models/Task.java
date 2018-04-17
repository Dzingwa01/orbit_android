package com.carefulcollections.gandanga.orbit.Models;

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

    public Task(int id, String name, String description, String picture_url, Date start_date, int creator_id, Date end_date){
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture_url = picture_url;
        this.start_date = start_date;
        this.creator_id = creator_id;
        this.end_date = end_date;
    }
}
