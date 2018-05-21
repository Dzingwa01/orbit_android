package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.carefulcollections.gandanga.orbit.Helpers.AppHelper;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.orbit.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.orbit.Managers.ManagerActivity;
import com.carefulcollections.gandanga.orbit.Models.Shift;
import com.carefulcollections.gandanga.orbit.Models.ShiftSchedule;
import com.carefulcollections.gandanga.orbit.Models.User;
import com.carefulcollections.gandanga.orbit.Models.UserComparator;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.widget.LinearLayout.LayoutParams;

/**
 * Created by Gandanga on 2018-05-20.
 */

public class SwapFragment extends Fragment {
Spinner shift_to_swap, shift_to_swap_with;
    private ArrayList<ShiftSchedule> shift_list,shift_list_with;
    private ArrayList<String> shift_names,shift_names_with;
    ProgressBar progressBar;
    LinearLayout swap_shift_fragment_layout;
    int selected_shit =0;
     int swap_shift_with =0;
     EditText swap_shift_reason;
     Button send_shift_swap;
     int employee_id;

    public SwapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.swap_shift_fragment, container, false);
        shift_to_swap = v.findViewById(R.id.shift_to_swap);
        shift_to_swap_with = v.findViewById(R.id.shift_to_swap_with);
        swap_shift_fragment_layout = v.findViewById(R.id.swap_shift_fragment_layout);
        swap_shift_reason = v.findViewById(R.id.swap_shift_reason);
        shift_names = new ArrayList<String>();
        shift_list = new ArrayList<ShiftSchedule>();
        shift_names_with = new ArrayList<String>();
        shift_list_with = new ArrayList<ShiftSchedule>();
        send_shift_swap = v.findViewById(R.id.send_shift_swap);
        progressBar = new ProgressBar(getActivity(),null,android.R.attr.progressBarStyle);
        progressBar.setIndeterminate(true);
        LayoutParams lp = new LayoutParams(
                550, // Width in pixels
                LayoutParams.WRAP_CONTENT // Height of progress bar
        );
        progressBar.setLayoutParams(lp);
        LayoutParams params = (LayoutParams) progressBar.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(params);
        swap_shift_fragment_layout.addView(progressBar);

        getShifts();
        send_shift_swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeShiftSwap();
            }
        });
        shift_to_swap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ShiftSchedule shift = shift_list.get(i);
                selected_shit = shift.id;

                Toast.makeText(getActivity(),String.valueOf(shift.id) + "-" + String.valueOf(shift.employee_id),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        shift_to_swap_with.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ShiftSchedule shift = shift_list_with.get(i);
                swap_shift_with = shift.id;
                employee_id = shift.employee_id;
                Toast.makeText(getActivity(),String.valueOf(shift.id) + "-" + String.valueOf(shift.employee_id),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return v;
    }
    public void storeShiftSwap() {
//        showProgress(true);

        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/store_shift_swap/";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
//                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    String swapped = result.getString("swap_response");
                    Log.d("This_User", swapped);
                    JSONObject response_1 = result.getJSONObject("swap_response");
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
                        //Log.d("Error Result", result);
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");
                       errorMessage =message;
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
                params.put("swap_shift", String.valueOf(selected_shit));
                params.put("with_shift", String.valueOf(swap_shift_with));
                params.put("requestor_id", pref.id);
                params.put("reason",swap_shift_reason.getText().toString());
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
    public void getTeamMemberShifts(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_current_team_shifts/"+pref.id+"/"+shift_list.get(0).id;
        Log.d("Url2",URL);
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
                            shift_list_with.add(shift);
                            java.text.SimpleDateFormat dt = new java.text.SimpleDateFormat("yyyy-mm-dd");
                            shift_names_with.add(dt.format(shift.shift_date) + " : "+ shift.start_time.toString() + " - " + obj.optString("name"));
                        }
//                        shift_to_swap.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,shift_names));
                        shift_to_swap_with.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,shift_names_with));

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
        Log.d("Url1",URL);
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
                        shift_to_swap.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,shift_names));
                        getTeamMemberShifts();
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

}
