package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.Shift;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gandanga on 2018-05-20.
 */

public class OfferShiftFragment extends Fragment {
Spinner shift_to_offer,employee_to_offer;
LinearLayout offer_shift_layout;
ArrayList<String> shift_names;
    ArrayList<Shift> shift_list;
    ArrayList<String> employee_names;
    ArrayList<User> availableEmployees;
ProgressBar progressBar;

    public OfferShiftFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.offer_shift_fragment, container, false);
        shift_to_offer = v.findViewById(R.id.shift_offered);
        employee_to_offer = v.findViewById(R.id.team_member);
        offer_shift_layout = v.findViewById(R.id.offer_shift_layout);

        shift_names = new ArrayList<String>();
        shift_list = new ArrayList<Shift>();
        employee_names = new ArrayList<String>();
        availableEmployees = new ArrayList<User>();
        progressBar = new ProgressBar(getActivity(),null,android.R.attr.progressBarStyle);
        progressBar.setIndeterminate(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                550, // Width in pixels
                LinearLayout.LayoutParams.WRAP_CONTENT // Height of progress bar
        );
        progressBar.setLayoutParams(lp);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(params);
        offer_shift_layout.addView(progressBar);

        getShifts();
        return v;
    }

    public void getAvailableMembers(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_available_members/"+pref.id+"/"+shift_list.get(0).id;
        Log.d("Url2",URL);
        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("employees");
                    Log.d("Response23",response_obj.toString());
                    if (response_obj.length() > 0) {
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            User user = gson.fromJson(element, User.class);
                            availableEmployees.add(user);
                            java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("yyyy-mm-dd");
                            employee_names.add(user.name + " " +user.surname);
                        }
//                        shift_to_swap.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,shift_names));
                        employee_to_offer.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,employee_names));

                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error2",e.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
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

    public void getShifts(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_current_shifts/"+pref.id;

        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("shifts");
                    Log.d("Response2",response_obj.toString());
                    if (response_obj.length() > 0) {
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            Shift shift = gson.fromJson(element, Shift.class);
                            shift_list.add(shift);
                            java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("yyyy-mm-dd");
                            shift_names.add(dt.format(shift.shift_date) + " : "+ shift.start_time.toString() + " - " + shift.end_time.toString());
                        }
                        shift_to_offer.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,shift_names));
//                        employee_to_offer.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,shift_names));

                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error2",e.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                }
                getAvailableMembers();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("error", error.toString());

            }
        });
        requestQueue.add(provinceRequest);
    }

}
