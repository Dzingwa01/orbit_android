package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.carefulcollections.gandanga.orbit.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gandanga on 2018-05-21.
 */

public class RequestTimeOff extends Fragment  implements DatePickerDialog.OnDateSetListener {
    Button start_date,start_time;
    Button end_date,end_time;
    String start_date_string,end_date_string,start_time_string,end_time_string;
    Boolean start_clicked = false;
    CheckBox all_day_check;
    int mHour,mMinute;
    public RequestTimeOff() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.request_timeoff_fragment, container, false);
        start_date = v.findViewById(R.id.start_date);
        end_date = v.findViewById(R.id.end_date);
        start_time = v.findViewById(R.id.start_time);
        end_time = v.findViewById(R.id.end_time);
        final Calendar c = Calendar.getInstance();
        all_day_check = v.findViewById(R.id.all_day_check);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), this, year, month, day);
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_clicked = true;
                datePickerDialog.show();
            }
        });
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_clicked = false;
                datePickerDialog.show();
            }
        });
        start_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        start_time.setText(i +":"+i1);
                        start_time_string = i +":"+i1;
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        end_time.setText(i +":"+i1);
                        end_time_string = i +":"+i1;
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        all_day_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    start_time.setVisibility(View.GONE);
                    end_time.setVisibility(View.GONE);
                    start_time.setText("Select Time");
                    end_time.setText("Select Time");
//                    Toast.makeText(getActivity(),"checked",Toast.LENGTH_LONG).show();
                }else{
                    start_time.setVisibility(View.VISIBLE);
                    end_time.setVisibility(View.VISIBLE);
//                    Toast.makeText(getActivity(),"un checked",Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if(start_clicked){
            start_date.setText(i2+"/"+(i1+1)+"/"+i);
            start_date_string = i2+"/"+(i1+1)+"/"+i;
        }else{
            end_date.setText(i2+"/"+(i1+1)+"/"+i);
            end_date_string = i+"/"+(i1+1)+"/"+i2;
        }

    }
}
