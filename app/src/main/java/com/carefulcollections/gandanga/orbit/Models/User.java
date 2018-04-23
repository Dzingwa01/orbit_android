package com.carefulcollections.gandanga.orbit.Models;

import java.io.Serializable;

/**
 * Created by Gandanga on 2018-04-12.
 */

public class User implements Serializable {
    public String name;
    public String surname;
    public String email;
    public String contact_number;
    public String gender;
    public String picture_url;
    public String creator_id;
    public String company_name;
    public int id;

    public User(int id, String name, String surname, String email, String contact_number, String gender, String picture_url, String creator_id, String company_name){
        this.name = name;
        this.id = id;
        this.surname = surname;
        this.email =email;
        this.contact_number = contact_number;
        this.gender = gender;
        this.picture_url = picture_url;
        this.creator_id = creator_id;
        this.company_name = company_name;
    }
}
