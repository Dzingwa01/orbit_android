package com.carefulcollections.gandanga.mishift.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Gandanga on 2018-05-02.
 */
@Entity(tableName = "comments")
public class Comment implements Serializable {
    @NonNull
    @PrimaryKey
    public int id;
    public int user_id;
    public int team_id;
    public String comment_text;
    public String first_name;
    public String last_name;
    public String created_at;
    public String user_picture_url;
    public String picture_url;
    public int likes;

    public Comment(int id, int user_id, int team_id, String comment_text, String first_name, String last_name, String created_at, String picture_url, String user_picture_url ){
        this.id=id;
        this.user_id = user_id;
        this.team_id = team_id;
        this.comment_text = comment_text;
        this.first_name = first_name;
        this.last_name = last_name;
        this.created_at = created_at;
        this.picture_url = picture_url;
        this.user_picture_url = user_picture_url;
    }

}
