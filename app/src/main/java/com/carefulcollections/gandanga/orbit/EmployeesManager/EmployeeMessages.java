package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Adapters.CommentAdapter;
import com.carefulcollections.gandanga.orbit.Adapters.MessageAdapter;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.Comment;
import com.carefulcollections.gandanga.orbit.Models.Message;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class EmployeeMessages extends AppCompatActivity {

    public ArrayList<Message> message_list;
    ProgressBar comments_progress;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView recyclerView;
    CoordinatorLayout messages_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        message_list = new ArrayList<>();
        comments_progress = findViewById(R.id.comments_progress);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager((EmployeeMessages.this));
        messages_view = findViewById(R.id.messages_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent = new Intent(EmployeeMessages.this,EmployeeDirectMessage.class);
             startActivity(intent);
            }
        });
        getEmployeeMessages();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getEmployeeMessages(){
        RequestQueue requestQueue = Volley.newRequestQueue(EmployeeMessages.this);
        Credentials credentials = EasyPreference.with(EmployeeMessages.this).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(EmployeeMessages.this).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_user_messages/"+pref.id;
        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("messages");
                    Log.d("messages",response_obj.toString());
                    if (response_obj.length() > 0) {
//
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();

                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            Message post = gson.fromJson(element, Message.class);
                            message_list.add(post);
//                                    //Log.d("Subject",post.comment_text);
                        }
                        if (message_list.size() > 0) {
                            setupAdapter();
                        } else {
//                                    Toast.makeText(getActivity(), "There are no messages as yet", Toast.LENGTH_SHORT).show();
                            Snackbar.make(messages_view, "There are no messages as yet", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    } else {
                        Snackbar.make(messages_view, "There are no messages as yet", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
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
    }

    public void setupAdapter() {
        MessageAdapter adapter = new MessageAdapter(message_list, EmployeeMessages.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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
            comments_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            comments_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    comments_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            comments_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            comments_progress.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
