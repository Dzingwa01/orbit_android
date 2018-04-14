package com.carefulcollections.gandanga.orbit.Models;

import java.util.Comparator;

/**
 * Created by Gandanga on 2018-04-13.
 */

public class MaterialComparator implements Comparator<TrainingMaterial> {
    @Override
    public int compare(TrainingMaterial o1, TrainingMaterial o2) {
        return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
    }
}
