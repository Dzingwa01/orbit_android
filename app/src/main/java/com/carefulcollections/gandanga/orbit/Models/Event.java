package com.carefulcollections.gandanga.orbit.Models;

/**
 * Created by Gandanga on 2018-04-14.
 */

public class Event {

    public String name;
    public String description;
    long Id;

    public Event(long Id,String name, String description){
        this.Id = Id;
        this.name = name;
        this.description = description;
    }
}
