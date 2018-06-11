package com.carefulcollections.gandanga.orbit.Managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.EmployeesManager.EmployeeScheduleFragment;
import com.carefulcollections.gandanga.orbit.EmployeesManager.ShiftDetails;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.Item;
import com.carefulcollections.gandanga.orbit.Models.Shift;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gandanga on 2018-04-13.
 */

public class ManagerShifts extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CalendarPickerController {
//AgendaCalendarView mAgendaCalendarView;
    AgendaCalendarView mAgendaCalendarView;
    private ArrayList<Shift> shift_list;
    ProgressBar progressBar;
    List<CalendarEvent> eventList;
    Calendar minDate = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();
    public ManagerShifts(){

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manager_shifts, container, false);
        mAgendaCalendarView = v.findViewById(R.id.agenda_calendar_view);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.add_shift);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getActivity(),ManagerCreateShift.class);
               startActivity(intent);
            }
        });

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        eventList = new ArrayList<>();
//
        shift_list = new ArrayList<>();

        progressBar = v.findViewById(R.id.progress);

        new GetEmployeeShifts().execute();

        return v;
    }
    public class GetEmployeeShifts extends AsyncTask<Void, Void, Boolean> {

        public GetEmployeeShifts(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
                UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
                final String url = credentials.server_url;
                String URL = url+"api/get_current_shifts_manager_all/"+pref.id;
                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("shifts");
                            Log.d("Response",response_obj.toString());
                            if (response_obj.length() > 0) {
//
                                for (int i = 0; i < response_obj.length(); i++) {
                                    JSONObject obj = response_obj.getJSONObject(i);
                                    JsonParser parser = new JsonParser();
                                    JsonElement element = parser.parse(obj.toString());
                                    Gson gson = new Gson();
                                    Shift shift = gson.fromJson(element, Shift.class);
                                    shift_list.add(shift);
                                }

                            }
                            mockList(eventList,shift_list);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();
                            showProgress(false);
                            mockList(eventList,shift_list);
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
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),ManagerActivity.class);
                startActivity(intent);
            }
            // TODO: register the new account here.
            return true;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
//            showProgress(false);


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
    private void mockList(List<CalendarEvent> eventList, List<Shift> shifts) {
        if(shifts.size()>0) {
            for (int i = 0; i < shifts.size(); i++) {
                Calendar startTime1 = Calendar.getInstance();
                startTime1.setTime(shifts.get(i).start_date);
                String time_parts[] = shifts.get(i).start_time.split(":");
                startTime1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_parts[0]));
                startTime1.set(Calendar.MINUTE, Integer.parseInt(time_parts[1]));
                Calendar endTime1 = Calendar.getInstance();
                endTime1.setTime(shifts.get(i).end_date);
                String time_parts1[] = shifts.get(i).end_time.split(":");
                endTime1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_parts1[0]));
                endTime1.set(Calendar.MINUTE, Integer.parseInt(time_parts1[1]));
                BaseCalendarEvent event1 = new BaseCalendarEvent(shifts.get(i).shift_title, shifts.get(i).shift_description, "",
                        ContextCompat.getColor(this.getContext(), R.color.orange_dark), startTime1, endTime1, false);
                eventList.add(event1);
            }
        }
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);

    }
    @Override
    public void onRefresh() {
    }

    @Override
    public void onDaySelected(DayItem dayItem) {
    }

    @Override
    public void onEventSelected(CalendarEvent event) {

        Intent intent = new Intent(getActivity(), ShiftDetails.class);
        int shift_id = Integer.valueOf(String.valueOf(event.getId()));

        for(int i=0;i<shift_list.size();i++){
            Shift shift = shift_list.get(i);
            if(shift.id == shift_id){
                Item cur = new Item(shift.id,shift.shift_title,shift.shift_description,shift.start_date, shift.end_date,shift.shift_date,"",Item.ItemType.ONE_ITEM,shift.start_time,shift.end_time);

                intent.putExtra("selected_shift",cur);
                SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd");
                String cur_day = dt1.format(cur.item_shift_date);
                intent.putExtra("event_date",cur_day);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                return;
            }
        }
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
    }
}
