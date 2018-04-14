package com.carefulcollections.gandanga.orbit.Models;

import java.io.Serializable;

/**
 * Created by Gandanga on 2018-04-13.
 */

public class TrainingMaterial implements Serializable {
    public String name;
    public String description;
    public String file_url;
    public String created_at;

    public TrainingMaterial(String name, String description, String file_url, String created_at){
        this.name = name;
        this.description = description;
        this.file_url = file_url;
        this.created_at = created_at;
    }
}
