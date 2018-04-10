package com.carefulcollections.gandanga.orbit;

import java.util.ArrayList;

/**
 * Created by Gandanga on 2018-04-09.
 */

public class UserPref {
    public String first_name;
    public String last_name;
    public String email;
    public String picture_url;
    public String _id;
    public String phone_number;
    public String gender;
    public String city;
    public int role_id;

    public UserPref(String _id, int role_id, String first_name, String last_name, String email, String phone_number, String gender, String picture_url, String city) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.picture_url = picture_url;
        this._id = _id;
        this.phone_number = phone_number;
        this.gender = gender;
        this.city = city;
        this.role_id =role_id;
    }
}
