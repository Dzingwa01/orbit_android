package com.carefulcollections.gandanga.mishift.Managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.mishift.EmployeesManager.MainActivity;
import com.carefulcollections.gandanga.mishift.Helpers.Credentials;
import com.carefulcollections.gandanga.mishift.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.mishift.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.mishift.Models.Team;
import com.carefulcollections.gandanga.mishift.Models.TeamComparator;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
import com.carefulcollections.gandanga.mishift.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ManagerCreateShift extends AppCompatActivity implements DatePickerDialog.OnDateSetListener  {
    Button start_date,start_time;
    Button end_date,end_time,submit;
    String start_date_string,end_date_string,start_time_string,end_time_string;
    Boolean start_clicked =false;
    int mHour,mMinute;
    ArrayList<Team> teams_list;
    ArrayList<String> team_names;
    Spinner teams;
    EditText shift_title, shift_description;
    int creator_id, team_id;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_create_shift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        teams_list = new ArrayList<Team>();
        team_names = new ArrayList<>();
        teams = findViewById(R.id.teams);
        shift_title = findViewById(R.id.shift_title);
        shift_description = findViewById(R.id.shift_description);
        progressBar = findViewById(R.id.progress);
        team_id = 0;
        submit = findViewById(R.id.create_shift);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        teams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                team_id = teams_list.get(i).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                team_id = teams_list.get(0).id;
            }
        });

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                ManagerCreateShift.this, this, year, month, day);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createShift();
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManagerCreateShift.this, new TimePickerDialog.OnTimeSetListener() {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(ManagerCreateShift.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        end_time.setText(i +":"+i1);
                        end_time_string = i +":"+i1;
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        getTeams();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void createShift() {
        showProgress(true);
        Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/store_shift";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
//                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    JSONObject response_1 = result.getJSONObject("shift");
                    JSONArray response_2 = result.getJSONArray("employee_schedules");
                    Log.d("Schedules",response_1.toString());
                    Log.d("shift",response_2.toString());
                    showProgress(false);

                    Toast.makeText(ManagerCreateShift.this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ManagerCreateShift.this, ManagerActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    showProgress(false);
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
                showProgress(false);
                error.printStackTrace();
                Toast.makeText(ManagerCreateShift.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                UserPref pref = EasyPreference.with(ManagerCreateShift.this).getObject("user_pref", UserPref.class);
                Map<String, String> params = new HashMap<>();
                params.put("shift_title", shift_title.getText().toString());
                params.put("shift_description", shift_description.getText().toString());
                params.put("team_id", String.valueOf(team_id));
                params.put("creator_id", String.valueOf(pref.id));
                params.put("start_date",start_date_string);
                params.put("end_date",end_date_string);
                params.put("start_time",start_time_string);
                params.put("end_time",end_time_string);
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    public void getTeams(){
        showProgress(true);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
            UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
            final String url = credentials.server_url;
            String URL = url+"api/get_teams/"+pref.id;

            JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray response_obj = response.getJSONArray("teams");
                        Log.d("Response",response_obj.toString());
                        if (response_obj.length() > 0) {
//
                            for (int i = 0; i < response_obj.length(); i++) {
                                JSONObject obj = response_obj.getJSONObject(i);
                                JsonParser parser = new JsonParser();

                                JsonElement element = parser.parse(obj.toString());
                                Gson gson = new Gson();
                                Team team = gson.fromJson(element, Team.class);
                                teams_list.add(team);
                                team_names.add(team.team_name);
                            }
                            teams.setAdapter(new ArrayAdapter<String>(ManagerCreateShift.this,android.R.layout.simple_spinner_dropdown_item,team_names));
                            team_id = teams_list.get(0).id;
                        } else {
                            Toast.makeText(ManagerCreateShift.this, "There are no teams available yet", Toast.LENGTH_LONG).show();
                        }

                        showProgress(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ManagerCreateShift.this, "Data error, please try again", Toast.LENGTH_LONG).show();
                        showProgress(false);
                        Intent intent = new Intent(ManagerCreateShift.this,ManagerActivity.class);
                        startActivity(intent);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.d("error", error.toString());

                }
            });
            requestQueue.add(provinceRequest);
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
}
