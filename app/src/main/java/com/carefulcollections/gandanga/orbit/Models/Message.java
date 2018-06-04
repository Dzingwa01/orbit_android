package com.carefulcollections.gandanga.orbit.Models;

/**
 * Created by Gandanga on 2018-06-04.
 */

public class Message {
    public int id;
    public String last_name, first_name,message_text,message_picture_url,created_at,user_picture_url;

    public Message(int id,String first_name,String last_name,String message_text,String message_picture_url,String user_picture_url,String created_at){
        this.id = id;
        this.last_name = last_name;
        this.first_name = first_name;
        this.message_text = message_text;
        this.message_picture_url = message_picture_url;
        this.user_picture_url = user_picture_url;
        this.created_at = created_at;
    }
}
