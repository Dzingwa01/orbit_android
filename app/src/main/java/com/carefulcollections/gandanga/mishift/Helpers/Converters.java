package com.carefulcollections.gandanga.mishift.Helpers;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Gandanga on 2018-09-07.
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
