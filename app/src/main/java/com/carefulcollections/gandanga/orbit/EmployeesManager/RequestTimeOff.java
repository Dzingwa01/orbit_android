package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.orbit.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gandanga on 2018-05-21.
 */

public class RequestTimeOff extends Fragment  implements DatePickerDialog.OnDateSetListener {
    Button start_date,start_time;
    Button end_date,end_time,submit;
    String start_date_string,end_date_string,start_time_string,end_time_string;
    Boolean start_clicked = false;
    CheckBox all_day_check;
    EditText leave_reason;
    int mHour,mMinute;
    Spinner leave_type;
    String selected_leave;
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
        leave_type =v.findViewById(R.id.leave_type);
        leave_reason = v.findViewById(R.id.time_off_reason);
       submit = v.findViewById(R.id.send_time_off);

        final Calendar c = Calendar.getInstance();
        all_day_check = v.findViewById(R.id.all_day_check);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);


        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), this, year, month, day);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeOffRequest();
            }
        });
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
            start_date.setText(i+"/"+(i1+1)+"/"+i2);
            start_date_string = i+"/"+(i1+1)+"/"+i2;
        }else{
            end_date.setText(i+"/"+(i1+1)+"/"+i2);
            end_date_string = i+"/"+(i1+1)+"/"+i2;
        }

    }

    public void storeOffRequest() {
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/store_off_request";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
//                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    String swapped = result.getString("request_response");
                    Log.d("This_User", swapped);
                    JSONObject response_1 = result.getJSONObject("request_response");
//                    JsonParser parser = new JsonParser();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout, Please try again later";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        Log.d("Error Result", result);
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");
                        errorMessage = message;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
                Map<String, String> params = new HashMap<>();
                params.put("off_start_date", String.valueOf(start_date_string));
                params.put("off_end_date", String.valueOf(end_date_string));
                 params.put("leave_type",leave_type.getSelectedItem().toString());

              if(!all_day_check.isChecked()){
                  params.put("off_category","day_segment");
                  params.put("off_start_time", String.valueOf(start_time_string));
                  params.put("off_end_time", String.valueOf(end_time_string));
              }else{
                  params.put("off_category","all_day");
              }
                params.put("employee_id", String.valueOf(pref.id));
                params.put("reason",leave_reason.getText().toString());
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest);
    }
}
