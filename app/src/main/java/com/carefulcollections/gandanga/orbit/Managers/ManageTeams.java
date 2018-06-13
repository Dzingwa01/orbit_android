package com.carefulcollections.gandanga.orbit.Managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Adapters.EmployeeAdapter;
import com.carefulcollections.gandanga.orbit.Adapters.TeamsAdapter;
import com.carefulcollections.gandanga.orbit.EmployeesManager.ViewTeams;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.Team;
import com.carefulcollections.gandanga.orbit.Models.TeamComparator;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserComparator;
import com.carefulcollections.gandanga.orbit.R;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ManageTeams extends AppCompatActivity {
    ProgressBar progressBar;
    LinearLayout team_employees;
    private RecyclerView listView;
    private EmployeeAdapter employeeAdapter;
    private ArrayList<User> users_list;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teams);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyle);
        progressBar.setIndeterminate(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                550, // Width in pixels
                LinearLayout.LayoutParams.WRAP_CONTENT // Height of progress bar
        );
        team_employees = findViewById(R.id.team_employees);
        progressBar.setLayoutParams(lp);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(params);
        team_employees.addView(progressBar);

        listView = findViewById(R.id.listView);
        users_list = new ArrayList<>();
//        progressBar = findViewById(R.id.progress);

        employeeAdapter = new EmployeeAdapter(users_list, this);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(employeeAdapter);
        Intent intent = getIntent();
        Team team = (Team) intent.getSerializableExtra("team");
        getTeamEmployees(team.id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getTeamEmployees(int team_id){
        progressBar.setVisibility(View.VISIBLE);
        try{

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            Credentials credentials = EasyPreference.with(this).getObject("server_details", Credentials.class);
            UserPref pref = EasyPreference.with(this).getObject("user_pref", UserPref.class);
            final String url = credentials.server_url;
            String URL = url+"api/get_team_employees/"+team_id;
            Log.d("Url",URL);
            JSONObject jsonBody = new JSONObject();
            JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray response_obj = response.getJSONArray("employees");
                        Log.d("employees",response_obj.toString());
                        if (response_obj.length() > 0) {
//
                            for (int i = 0; i < response_obj.length(); i++) {
                                JSONObject obj = response_obj.getJSONObject(i);
                                JsonParser parser = new JsonParser();

                                JsonElement element = parser.parse(obj.toString());
                                Gson gson = new Gson();
                                User user = gson.fromJson(element, User.class);
                                users_list.add(user);

                            }
                            if (users_list.size() > 0) {
                                Collections.sort(users_list, new UserComparator());
                                employeeAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(ManageTeams.this, "There are no team members in this team", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.d("error", error.toString());

                }
            });
            requestQueue.add(provinceRequest);
        }
        catch (Exception e){

        }
    }
}
