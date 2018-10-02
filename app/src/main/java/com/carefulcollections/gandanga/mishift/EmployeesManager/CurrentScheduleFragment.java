package com.carefulcollections.gandanga.mishift.EmployeesManager;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
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
import com.carefulcollections.gandanga.mishift.Adapters.*;
import com.carefulcollections.gandanga.mishift.Adapters.TeamsAdapter;
import com.carefulcollections.gandanga.mishift.Helpers.AppDatabase;
import com.carefulcollections.gandanga.mishift.Helpers.Credentials;
import com.carefulcollections.gandanga.mishift.Managers.ManagerActivity;
import com.carefulcollections.gandanga.mishift.Managers.ManagerScheduleFragment;
import com.carefulcollections.gandanga.mishift.Managers.TeamsFragment;
import com.carefulcollections.gandanga.mishift.Models.Comment;
import com.carefulcollections.gandanga.mishift.Models.Item;
import com.carefulcollections.gandanga.mishift.Models.Shift;
import com.carefulcollections.gandanga.mishift.Models.ShiftRoom;
import com.carefulcollections.gandanga.mishift.Models.Task;
import com.carefulcollections.gandanga.mishift.Models.TaskRoom;
import com.carefulcollections.gandanga.mishift.Models.Team;
import com.carefulcollections.gandanga.mishift.Models.TeamComparator;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
import com.carefulcollections.gandanga.mishift.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.registerable.connection.Connectable;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentScheduleFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView listView;
    private CurrentShiftAdapter teamAdapter;
    private ArrayList<Shift> shift_list;
    ProgressBar progressBar;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeLayout;
    public ArrayList<Task> task_list;
    public TasksAdapter tasksAdapter;
    public ArrayList<Item> items_list;
    FloatingActionButton swap_shift;
    Merlin merlin;
    MerlinsBeard merlinsBeard;
    List<ShiftRoom> shifts;
    List<TaskRoom> task_room;
    String CHANNEL_ID = "MS1235";

    public CurrentScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_current_schedule, container, false);
        listView = v.findViewById(R.id.current_shift);
        shift_list = new ArrayList<>();
        shifts = new ArrayList<>();
        task_list = new ArrayList<>();
        task_room = new ArrayList<>();

        progressBar = v.findViewById(R.id.progress);
        items_list = new ArrayList<>();
        mSwipeLayout = v.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        swap_shift = v.findViewById(R.id.swap_shift);
        swap_shift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SwapOfferActivity.class);
                getActivity().startActivity(intent);
            }
        });
        merlin = new Merlin.Builder().withConnectableCallbacks().build(getContext());
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                // Do something you haz internet!
                new GetEmployeeShifts().execute();
            }
        });
        merlinsBeard = MerlinsBeard.from(getContext());
        if (merlinsBeard.isConnected()) {
            // Connected, do something!
            new GetEmployeeShifts().execute();
            PusherOptions options = new PusherOptions();
            options.setCluster("ap2");
            Pusher pusher = new Pusher("347d0dccd5f2a3e703a2", options);
            Channel channel = pusher.subscribe("my-channel");
            channel.bind("my-shift-event", new SubscriptionEventListener() {
                @Override
                public void onEvent(String channelName, String eventName, final String data) {
                    //Log.d("Message", "message");
//                Log.d("Triggered", data);
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(data);
                    Gson gson = new Gson();
                    Shift shift = gson.fromJson(element, Shift.class);

                    if (!shift_list.contains(shift)) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification_icon)
                                .setContentTitle("New Shift " +shift.shift_title)
                                .setContentText(shift.shift_description)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManager mNotificationManager =
                                (NotificationManager) getActivity().getSystemService(getContext().NOTIFICATION_SERVICE);
                        mBuilder.setContentIntent(pendingIntent);
                        mNotificationManager.notify(shift.id, mBuilder.build());
                        shift_list.add(0, shift);
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                setupAdapter();
//                            }
//                        });
                    }

                }
            });
            pusher.connect();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          new  getShiftsRoom().execute();
                            setupAdapter();
                        }
                    });

                }
            }, 100);
            Toast.makeText(getContext(),"Offline mode: No active internet connection",Toast.LENGTH_LONG).show();
        }
        return v;
    }
    public class getShiftsRoom extends AsyncTask<Void, Void, Boolean> {
        public getShiftsRoom(){

        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            List<TaskRoom> taskRooms = readTasksRoom(AppDatabase.getAppDatabase(getContext()));
            List<Shift> shiftRooms = readShiftsRoom(AppDatabase.getAppDatabase(getContext()));
            task_list = new ArrayList<>();
            shift_list = new ArrayList<>();
            for(int i=0;i<taskRooms.size();i++){
                TaskRoom tr = taskRooms.get(i);
                Task cur = new Task(tr.id,tr.name,tr.description,tr.picture_url,tr.start_date,tr.shift_date,tr.creator_id,tr.end_date,tr.start_time,tr.end_time);
                task_list.add(cur);
            }

            for(int i=0;i<shiftRooms.size();i++){
                Shift tr = shiftRooms.get(i);
                shift_list.add(tr);
            }
            setupAdapter();
            return null;
        }
    }

    @Override
    public void onRefresh() {
        shift_list.clear();
        task_list.clear();
        items_list.clear();
        mSwipeLayout.setRefreshing(true);
        new GetEmployeeShifts().execute();
    }

    public  List<TaskRoom> readTasksRoom(final AppDatabase db){
        List<TaskRoom> tasks = db.mishiftDao().getAllTasks();
        return tasks;
    }
    public  List<Shift> readShiftsRoom(final AppDatabase db){
        List<Shift> shifts = db.mishiftDao().getAllCurrent();
        return shifts;
    }
    public  List<TaskRoom> addTaskRoom(final AppDatabase db, List<TaskRoom> tasks){
        db.mishiftDao().insertAllTasks(tasks);
        return tasks;
    }
    private  List<Shift> addShiftRoom(final AppDatabase db, List<Shift> shifts){
        db.mishiftDao().insertAllCurrentShifts(shifts);
        return shifts;
    }
    public class GetEmployeeShifts extends AsyncTask<Void, Void, Boolean> {

      public GetEmployeeShifts(){

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
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
                UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
                final String url = credentials.server_url;
                String URL = url+"api/get_current_shift/"+pref.id;
                shift_list.clear();
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
//                                swap_shift.setVisibility(View.VISIBLE);

                            } else {
                                Shift shift = new Shift(0,"No shifts upcoming for today",new Date(),new Date(),new Date(),0,"none", "","","");
//                                Toast.makeText(getActivity(), "There are no shifts available yet", Toast.LENGTH_LONG).show();
                                ShiftRoom shiftRoom = new ShiftRoom(0,"No shifts upcoming for today",new Date(),new Date(),new Date(),0,"none", "","","");
                                shift_list.add(shift);
                            }

                            addShiftRoom(AppDatabase.getAppDatabase(getContext()),shift_list);
                            new GetEmployeeTasks().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();
//                            showProgress(false);
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

    public class GetEmployeeTasks extends AsyncTask<Void, Void, Boolean> {

        public GetEmployeeTasks(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgress(true);
//            post_list = new ArrayList<Post>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
                UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
                final String url = credentials.server_url;
                String URL = url+"api/get_current_tasks/"+pref.id;
                task_list.clear();
                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("tasks");
                            Log.d("Response",response_obj.toString());
                            if (response_obj.length() > 0) {
//
                                for (int i = 0; i < response_obj.length(); i++) {
                                    JSONObject obj = response_obj.getJSONObject(i);
                                    JsonParser parser = new JsonParser();

                                    JsonElement element = parser.parse(obj.toString());
                                    Gson gson = new Gson();
                                    Task task = gson.fromJson(element, Task.class);
                                    TaskRoom taskRoom = gson.fromJson(element, TaskRoom.class);
                                    task_list.add(task);
                                    task_room.add(taskRoom);

                                }

                            } else {
                                Task task = new Task(0,"No tasks for today","No tasks for today","",new Date(),new Date(),0,new Date(), "","");
//                                Toast.makeText(getActivity(), "There are no tasks available yet", Toast.LENGTH_LONG).show();
                                TaskRoom taskRoom = new TaskRoom(0,"No tasks for today","No tasks for today","",new Date(),new Date(),0,new Date(), "","");
                                task_list.add(task);
                                task_room.add(taskRoom);
//                                Log.d("TaskList32",String.valueOf(task_list.size()));
//                                setupTaskAdapter();
                            }
                            addTaskRoom(AppDatabase.getAppDatabase(getContext()),task_room);
                            setupAdapter();
                            mSwipeLayout.setRefreshing(false);
//                            new GetEmployeeShifts().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mSwipeLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();

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
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
            // TODO: register the new account here.
            return true;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            Log.d("TaskList",String.valueOf(task_list.size()));

        }

        @Override
        protected void onCancelled() {
        }
    }

    public void setupAdapter() {
        for(int i=0;i<shift_list.size();i++){
            Shift shift = shift_list.get(i);
            if (shift.team_name == "none") {
                Item cur = new Item(shift.id,shift.shift_title,"none",shift.start_date, shift.end_date,shift.shift_date,"",Item.ItemType.ONE_ITEM,shift.start_time,shift.end_time);
                items_list.add(cur);
            }else{
                Item cur = new Item(shift.id,shift.shift_title,shift.shift_description,shift.start_date, shift.end_date,shift.shift_date,"",Item.ItemType.ONE_ITEM,shift.start_time,shift.end_time);
                items_list.add(cur);
            }

        }
        for(int i=0;i<task_list.size();i++){
            Task task = task_list.get(i);
            if(task.id==0){
                Item cur = new Item(task.id,task.name,"none",task.start_date,task.end_date,task.shift_date,task.picture_url,Item.ItemType.TWO_ITEM,task.start_time,task.end_time);
                items_list.add(cur);
            }
            else{
                Item cur = new Item(task.id,task.name,task.description,task.start_date,task.end_date,task.shift_date,task.picture_url,Item.ItemType.TWO_ITEM,task.start_time,task.end_time);
                items_list.add(cur);
            }

        }
        Log.d("List_size",String.valueOf(items_list.size()));
        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(items_list,getActivity());
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(itemArrayAdapter);
    }
    public void setupTaskAdapter() {
        Log.d("Output","setting tasks");
        tasksAdapter = new TasksAdapter(task_list, getActivity());
        mLayoutManager = new LinearLayoutManager( getActivity());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(tasksAdapter);
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
