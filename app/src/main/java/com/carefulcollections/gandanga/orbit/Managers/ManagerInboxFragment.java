package com.carefulcollections.gandanga.orbit.Managers;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carefulcollections.gandanga.orbit.Adapters.EmployeeInboxAdapter;
import com.carefulcollections.gandanga.orbit.Adapters.ManagerInboxAdapter;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.InboxItem;
import com.carefulcollections.gandanga.orbit.Models.LeaveRequest;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gandanga on 2018-06-11.
 */

public class ManagerInboxFragment extends ListFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    ArrayList<LeaveRequest> shift_offers;

    RecyclerView listView;
    ArrayList<InboxItem> inboxItems;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout inbox_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inbox, container, false);
        shift_offers = new ArrayList<>();
        listView = v.findViewById(R.id.inbox_list);
        inboxItems = new ArrayList<>();
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        inbox_view = v.findViewById(R.id.inbox_view);
        final FragmentActivity activity = getActivity();
        getOffRequests();
        PusherOptions options = new PusherOptions();
        options.setCluster("ap2");
        Pusher pusher = new Pusher("347d0dccd5f2a3e703a2", options);
        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-swap-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                //Log.d("Message", "message");
//                Log.d("Triggered", data);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(data);
                Gson gson = new Gson();
                LeaveRequest shiftOffer = gson.fromJson(element, LeaveRequest.class);
                if (!shift_offers.contains(shiftOffer)) {
                    Log.d("Contained","Yes not contained");
                    shift_offers.add(0, shiftOffer);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupAdapter();
                        }
                    });
                }
            }
        });
        pusher.connect();
        return v;

    }
    public void setupAdapter() {
        ManagerInboxAdapter itemArrayAdapter = new ManagerInboxAdapter(shift_offers,getActivity());
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(itemArrayAdapter);

    }

    public void getOffRequests(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_leave_requests/"+pref.id;
        shift_offers.clear();
        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("leave_requests");
                    Log.d("Response_Leave",response_obj.toString());
                    if (response_obj.length() > 0) {
//
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            LeaveRequest swap = gson.fromJson(element, LeaveRequest.class);
                            if(!shift_offers.contains(swap)){
                                swap.type = LeaveRequest.ItemType.ONE_ITEM;
                                shift_offers.add(swap);
                            }
                        }

                    }else{
                        LeaveRequest request = new LeaveRequest(0,"","","","","","","No Available Leave Requests",0,"","");
                        request.type = LeaveRequest.ItemType.TWO_ITEM;
                        shift_offers.add(request);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    setupAdapter();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
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



    @Override
    public void onRefresh() {
        inboxItems.clear();
        swipeRefreshLayout.setRefreshing(true);
        Log.d("Refreshing","refresging");
        getOffRequests();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}
