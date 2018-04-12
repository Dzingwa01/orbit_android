package com.carefulcollections.gandanga.orbit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Adapters.EmployeeAdapter;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserComparator;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class EmployeeProfile extends AppCompatActivity {

    private RecyclerView listView;
    private EmployeeAdapter employeeAdapter;
    private ArrayList<User> users_list;
    ProgressBar progressBar;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        users_list = new ArrayList<>();
        progressBar = findViewById(R.id.comments_progress);

        employeeAdapter = new EmployeeAdapter(users_list, EmployeeProfile.this);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(employeeAdapter);
        new GetUsers().execute();
    }

    public class GetUsers extends AsyncTask<Void, Void, Boolean> {
        GetUsers() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
//            post_list = new ArrayList<Post>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(EmployeeProfile.this);
                Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
                UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
                final String url = credentials.server_url;
                String URL = url+"api/get_employees/"+pref.id;
                JSONObject jsonBody = new JSONObject();
//                jsonBody.put("creator_id", pref.id);
                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("users");
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
                                Toast.makeText(EmployeeProfile.this, "There are no users available as yet", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
                Toast.makeText(EmployeeProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //*** setOnQueryTextFocusChangeListener ***
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                //Log.d("Lister","Hittted");
                //Log.d("Query",query);
                employeeAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                //Log.d("ListerChanged","Hittted");
                //Log.d("Query",query);
                employeeAdapter.getFilter().filter(query);
                return false;
            }
        });

        return true;
    }
}
