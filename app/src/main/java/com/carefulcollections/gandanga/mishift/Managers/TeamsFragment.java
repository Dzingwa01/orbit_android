package com.carefulcollections.gandanga.mishift.Managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.carefulcollections.gandanga.mishift.Adapters.TeamsAdapter;
import com.carefulcollections.gandanga.mishift.Helpers.AppDatabase;
import com.carefulcollections.gandanga.mishift.Helpers.Credentials;
import com.carefulcollections.gandanga.mishift.Models.ShiftRoom;
import com.carefulcollections.gandanga.mishift.Models.Task;
import com.carefulcollections.gandanga.mishift.Models.Team;
import com.carefulcollections.gandanga.mishift.Models.TeamComparator;
import com.carefulcollections.gandanga.mishift.R;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
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
import java.util.List;

/**
 * Created by Gandanga on 2018-04-17.
 */

public class TeamsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView listView;
    private TeamsAdapter teamAdapter;
    private ArrayList<Team> teams_list;
    ProgressBar progressBar;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeLayout;
    Merlin merlin;
    MerlinsBeard merlinsBeard;

    public TeamsFragment(){

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teams, container, false);
        listView = v.findViewById(R.id.teamsList);
        teams_list = new ArrayList<>();
        progressBar = v.findViewById(R.id.teams_progress);
        mSwipeLayout = v.findViewById(R.id.swipeRefreshLayout);

        merlin = new Merlin.Builder().withConnectableCallbacks().build(getContext());
        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                // Do something you haz internet!
                new GetTeams().execute();
            }
        });
        merlinsBeard = MerlinsBeard.from(getContext());
        if (merlinsBeard.isConnected()) {
            // Connected, do something!
            new GetTeams().execute();
        } else {
            // Disconnected, do something!
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                         List<Team> teams =   readTeamRoom(AppDatabase.getAppDatabase(getContext()));
                            if (teams.size() > 0) {
                                Collections.sort(teams, new TeamComparator());
//                                    teamAdapter.notifyDataSetChanged();
                                for(int i=0;i<teams.size();i++){
                                    teams_list.add(teams.get(i));
                                }
                                setupAdapter();
                            }else{
                                Toast.makeText(getContext(),"Offline mode: No team information available as yet.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }, 100);

        }
        return v;

    }

    public  List<Team> readTeamRoom(final AppDatabase db){
        List<Team> teams = db.mishiftDao().getAllTeams();
        return teams;
    }
    public class GetTeams extends AsyncTask<Void, Void, Boolean> {
        GetTeams() {
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
                String URL = url+"api/get_teams/"+pref.id;

                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("teams");
                            Log.d("Response",response_obj.toString());
                            if (response_obj.length() > 0) {
//
                                for (int i = 0; i < response_obj.length(); i++) {
                                    JSONObject obj = response_obj.getJSONObject(i);
                                    JsonParser parser = new JsonParser();

                                    JsonElement element = parser.parse(obj.toString());
                                    Gson gson = new Gson();
                                    Team team = gson.fromJson(element, Team.class);
                                    teams_list.add(team);

                                }
                                if (teams_list.size() > 0) {
                                    Log.d("Check","Check me");
                                    addTeamRoom(AppDatabase.getAppDatabase(getContext()),teams_list);
                                    Collections.sort(teams_list, new TeamComparator());
//                                    teamAdapter.notifyDataSetChanged();
                                    setupAdapter();
                                }

                            } else {
                                Toast.makeText(getActivity(), "There are no teams available yet", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();
                            showProgress(false);
                            Intent intent = new Intent(getActivity(),ManagerActivity.class);
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
            } catch (Exception e) {
                showProgress(false);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),ManagerActivity.class);
                startActivity(intent);
            }
            // TODO: register the new account here.
            return true;
        }
        private List<Team> addTeamRoom(final AppDatabase db, List<Team> teamList){
            db.mishiftDao().insertAllTeams(teamList);
            return teamList;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
        }
    }
    public void setupAdapter() {
        teamAdapter = new TeamsAdapter(teams_list, getActivity());
        mLayoutManager = new LinearLayoutManager( getActivity());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(teamAdapter);
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
    public void onRefresh() {
        new RefreshTeams().execute();
    }

    class RefreshTeams extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            mSwipeLayout.setRefreshing(true);
//
            new GetTeamsWithoutProgress().execute();
            return null;
        }

        @Override
        protected void onPostExecute(Void newlist) {
            super.onPostExecute(newlist);
            mSwipeLayout.setRefreshing(false);
        }
    }

    public class GetTeamsWithoutProgress extends AsyncTask<Void, Void, Boolean> {
        GetTeamsWithoutProgress() {
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
                String URL = url+"api/get_teams/"+pref.id;

                JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray response_obj = response.getJSONArray("teams");
                            Log.d("Response",response_obj.toString());
                            if (response_obj.length() > 0) {
//
                                for (int i = 0; i < response_obj.length(); i++) {
                                    JSONObject obj = response_obj.getJSONObject(i);
                                    JsonParser parser = new JsonParser();

                                    JsonElement element = parser.parse(obj.toString());
                                    Gson gson = new Gson();
                                    Team team = gson.fromJson(element, Team.class);
                                    teams_list.add(team);

                                }
                                if (teams_list.size() > 0) {
                                    Log.d("Check","Check me");
                                    Collections.sort(teams_list, new TeamComparator());
//                                    teamAdapter.notifyDataSetChanged();
                                    setupAdapter();
                                }

                            } else {
                                Toast.makeText(getActivity(), "There are no teams available yet", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();
//                            showProgress(false);
                            Intent intent = new Intent(getActivity(),ManagerActivity.class);
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
            } catch (Exception e) {
//                showProgress(false);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),ManagerActivity.class);
                startActivity(intent);
            }
            // TODO: register the new account here.
            return true;
        }

        public void setupAdapter() {
            teamAdapter = new TeamsAdapter(teams_list, getActivity());
            mLayoutManager = new LinearLayoutManager( getActivity());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());
            listView.setAdapter(teamAdapter);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSwipeLayout.setRefreshing(false);
        }

        @Override
        protected void onCancelled() {
            mSwipeLayout.setRefreshing(false);
        }
    }
}
