package com.carefulcollections.gandanga.mishift.EmployeesManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.mishift.Adapters.TrainingMaterialAdapter;
import com.carefulcollections.gandanga.mishift.Helpers.Credentials;
import com.carefulcollections.gandanga.mishift.Managers.ManagerActivity;
import com.carefulcollections.gandanga.mishift.Managers.TrainingMaterials;
import com.carefulcollections.gandanga.mishift.Models.MaterialComparator;
import com.carefulcollections.gandanga.mishift.Models.TrainingMaterial;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
import com.carefulcollections.gandanga.mishift.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.registerable.connection.Connectable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class EmployeeTrainingMaterial extends AppCompatActivity {
    private RecyclerView listView;
    private TrainingMaterialAdapter materialAdapter;
    private ArrayList<TrainingMaterial> materials;
    ProgressBar progressBar;
    RecyclerView.LayoutManager mLayoutManager;
    Merlin merlin;
    MerlinsBeard merlinsBeard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_training_material);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.listView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        materials = new ArrayList<>();
        progressBar = findViewById(R.id.progress);


        merlin = new Merlin.Builder().withConnectableCallbacks().build(getApplicationContext());
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                // Do something you haz internet!
                materialAdapter = new TrainingMaterialAdapter(materials, EmployeeTrainingMaterial.this);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                listView.setLayoutManager(mLayoutManager);
                listView.setItemAnimator(new DefaultItemAnimator());
                listView.setAdapter(materialAdapter);
                getTrainingMaterial();
            }
        });
        merlinsBeard = MerlinsBeard.from(this);

        if (merlinsBeard.isConnected()) {
            // Connected, do something!
            materialAdapter = new TrainingMaterialAdapter(materials, EmployeeTrainingMaterial.this);
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());
            listView.setAdapter(materialAdapter);
            getTrainingMaterial();
        } else {
            // Disconnected, do something!
            showProgress(false);
            Toast.makeText(getApplicationContext(),"Offline mode: No active internet connection. Can't get latest training material",Toast.LENGTH_LONG).show();
        }
    }

    public void getTrainingMaterial(){
        showProgress(true);
        RequestQueue requestQueue = Volley.newRequestQueue(EmployeeTrainingMaterial.this);
        Credentials credentials = EasyPreference.with(getApplicationContext()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getApplicationContext()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_employee_materials/"+pref.id;

        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("materials");
                    if (response_obj.length() > 0) {
//
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            TrainingMaterial material = gson.fromJson(element, TrainingMaterial.class);
                            materials.add(material);

                        }
                        if (materials.size() > 0) {
                            Collections.sort(materials, new MaterialComparator());
                            materialAdapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(EmployeeTrainingMaterial.this, "There are no training material available as yet", Toast.LENGTH_LONG).show();
                    }
                    showProgress(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EmployeeTrainingMaterial.this, "Data error, please try again", Toast.LENGTH_LONG).show();
                    showProgress(false);
                    Intent intent = new Intent(EmployeeTrainingMaterial.this,ManagerActivity.class);
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
