package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.orbit.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.orbit.Models.Shift;
import com.carefulcollections.gandanga.orbit.Models.ShiftSchedule;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gandanga on 2018-05-20.
 */

public class OfferShiftFragment extends Fragment {
Spinner shift_to_offer,employee_to_offer;
LinearLayout offer_shift_layout;
ArrayList<String> shift_names;
    ArrayList<ShiftSchedule> shift_list;
    ArrayList<String> employee_names;
    ArrayList<User> availableEmployees;
    int employee_id =0;
    int offer_shift =0;
    EditText shift_offer_reason;
    Button send_shift_offer;
    CheckBox all_day_check;

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
        send_shift_offer = v.findViewById(R.id.send_shift_offer);
        shift_offer_reason = v.findViewById(R.id.shift_offer_reason);
        shift_names = new ArrayList<String>();
        shift_list = new ArrayList<ShiftSchedule>();
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
        shift_to_offer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ShiftSchedule shift = shift_list.get(i);
                offer_shift = shift.id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        employee_to_offer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                User user = availableEmployees.get(i);
                employee_id = user.id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        send_shift_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeShiftOffer();
            }
        });
        return v;
    }
    public void storeShiftOffer() {
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/store_shift_offer";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
//                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    String offered = result.getString("offer_response");
                    Log.d("This_User", offered);
                    JSONObject response_1 = result.getJSONObject("offer_response");
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
                params.put("offer_shift", String.valueOf(offer_shift));
                params.put("employee_id", pref.id);
                params.put("team_member", String.valueOf(employee_id));
                params.put("reason",shift_offer_reason.getText().toString());
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
                            ShiftSchedule shift = gson.fromJson(element, ShiftSchedule.class);
                            shift_list.add(shift);
                            java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("yyyy-mm-dd");
                            shift_names.add(dt.format(shift.shift_date) + " : "+ shift.start_time.toString() + " - " + shift.end_time.toString());
                        }
                        shift_to_offer.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,shift_names));
                        send_shift_offer.setEnabled(true);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error2",e.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if(shift_list.size()>0){
                    getAvailableMembers();
                }else{
                    send_shift_offer.setEnabled(false);
                    try{
                        Snackbar.make(getView(), "You currently do not have any available shifts to offer", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }catch (Exception e){

                    }

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

}
