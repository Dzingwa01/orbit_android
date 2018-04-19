package com.carefulcollections.gandanga.orbit.Models;

import java.util.Date;

/**
 * Created by Gandanga on 2018-04-19.
 */

public class Item {
    public enum ItemType {
        ONE_ITEM, TWO_ITEM;
    }


    public String item_name;
    public String item_description;
    public Date item_start_date;
    public Date item_end_date;
    public String item_picture_url;
    public ItemType type;

    public Item(String item_name, String item_description, Date item_start_date, Date item_end_date, String item_picture_url, ItemType type) {
        this.item_name = item_name;
        this.item_description = item_description;
        this.item_start_date = item_start_date;
        this.item_end_date = item_end_date;
        this.type = type;
        this.item_picture_url = item_picture_url;
    }

 public ItemType getType(){
 return this.type;
}

}
