package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carefulcollections.gandanga.orbit.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShiftDetailsFragment extends Fragment {

    public ShiftDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shift_details, container, false);
    }
}
