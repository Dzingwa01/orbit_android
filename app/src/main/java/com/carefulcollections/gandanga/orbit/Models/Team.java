package com.carefulcollections.gandanga.orbit.Models;

import java.io.Serializable;

/**
 * Created by Gandanga on 2018-04-13.
 */

public class Team implements Serializable {
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
}
