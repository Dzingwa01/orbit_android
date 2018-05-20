package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Adapters.EmployeeAdapter;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Managers.EmployeeProfile;
import com.carefulcollections.gandanga.orbit.Models.Item;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserComparator;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ShiftDetails extends AppCompatActivity {
Item shift;
TextView shift_title, shift_description, start_date,end_date,shift_date;
    private RecyclerView listView;
    private EmployeeAdapter employeeAdapter;
    private ArrayList<User> users_list;
    ProgressBar progressBar;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        shift_title = findViewById(R.id.shift_title);
        shift_description = findViewById(R.id.shift_description);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        shift_date = findViewById(R.id.shift_date);
        shift = (Item)intent.getSerializableExtra("selected_shift");
        shift_description.setText(shift.item_description);
        shift_title.setText(shift.item_name);
        start_date.setText(shift.start_time.toString());
        end_date.setText(shift.end_time.toString());

        shift_date.setText(intent.getStringExtra("event_date"));
        listView = findViewById(R.id.listView);
        users_list = new ArrayList<>();
        progressBar = findViewById(R.id.progress);

        employeeAdapter = new EmployeeAdapter(users_list, ShiftDetails.this);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(employeeAdapter);
        new GetUsers().execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class GetUsers extends AsyncTask<Void, Void, Boolean> {
        GetUsers() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            post_list = new ArrayList<Post>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                showProgress(true);
                RequestQueue requestQueue = Volley.newRequestQueue(ShiftDetails.this);
                Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
                UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
                final String url = credentials.server_url;
                String URL = url+"api/get_shift_employees/"+shift.id;
                JSONObject jsonBody = new JSONObject();
//                jsonBody.put("creator_id", pref.id);
                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("employees");
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
                                Toast.makeText(ShiftDetails.this, "There are no team members on this shifts", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgress(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("error", error.toString());

                    }
                });
                requestQueue.add(provinceRequest);
            } catch (Exception e) {
                showProgress(false);
                Toast.makeText(ShiftDetails.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
        }
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

}
