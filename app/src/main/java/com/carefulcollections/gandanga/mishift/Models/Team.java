package com.carefulcollections.gandanga.mishift.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Gandanga on 2018-04-13.
 */
@Entity(tableName = "teams")
public class Team implements Serializable {
    @NonNull
    @PrimaryKey
    public int id;
    public String team_name;
    public  String city_name;
    public String team_description;

    public Team(int id,String team_name, String team_description,String city){
        this.id =id;
        this.team_name = team_name;
        this.city_name = city;
        this.team_description = team_description;
    }
    public Team(){

    }
}
