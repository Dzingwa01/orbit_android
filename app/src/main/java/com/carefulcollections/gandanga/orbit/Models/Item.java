package com.carefulcollections.gandanga.orbit.Models;

import android.text.format.Time;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Gandanga on 2018-04-19.
 */

public class Item implements Serializable {
    public enum ItemType {
        ONE_ITEM, TWO_ITEM;
    }

    public int id;
    public String item_name;
    public String item_description;
    public Date item_start_date;
    public Date item_end_date;
    public String item_picture_url;
    public ItemType type;
    public String start_time;
    public String end_time;
    public Date item_shift_date;

    public Item(int id,String item_name, String item_description, Date item_start_date, Date item_end_date,Date item_shift_date, String item_picture_url, ItemType type,String start_time, String end_time) {
        this.id = id;
        this.item_name = item_name;
        this.item_description = item_description;
        this.item_start_date = item_start_date;
        this.item_end_date = item_end_date;
        this.type = type;
        this.item_picture_url = item_picture_url;
        this.start_time = start_time;
        this.end_time = end_time;
        this.item_shift_date =item_shift_date;
    }

 public ItemType getType(){
 return this.type;
}

}
