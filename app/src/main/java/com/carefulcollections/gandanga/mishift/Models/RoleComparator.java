package com.carefulcollections.gandanga.mishift.Models;

import java.util.Comparator;

/**
 * Created by Gandanga on 2018-04-16.
 */

public class RoleComparator implements Comparator<Role> {
    @Override
    public int compare(Role o1, Role o2) {
        return o1.role_display_name.toLowerCase().compareTo(o2.role_display_name.toLowerCase());
    }
}
