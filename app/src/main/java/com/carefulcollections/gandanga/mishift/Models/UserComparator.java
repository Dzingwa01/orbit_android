package com.carefulcollections.gandanga.mishift.Models;

import java.util.Comparator;

/**
 * Created by Gandanga on 2018-04-12.
 */

public class UserComparator implements Comparator<User> {
@Override
public int compare(User o1, User o2) {
        return o1.name.toLowerCase().compareTo(o2.surname.toLowerCase());
        }
}