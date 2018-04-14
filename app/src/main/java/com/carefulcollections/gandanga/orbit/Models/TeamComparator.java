package com.carefulcollections.gandanga.orbit.Models;

import java.util.Comparator;

/**
 * Created by Gandanga on 2018-04-13.
 */

public class TeamComparator implements Comparator<Team> {
    @Override
    public int compare(Team o1, Team o2) {
        return o1.team_name.toLowerCase().compareTo(o2.team_name.toLowerCase());
    }

}
