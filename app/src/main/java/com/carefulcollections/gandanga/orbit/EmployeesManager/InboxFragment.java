package com.carefulcollections.gandanga.orbit.EmployeesManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.carefulcollections.gandanga.orbit.Adapters.ItemArrayAdapter;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Models.Comment;
import com.carefulcollections.gandanga.orbit.Models.InboxItem;
import com.carefulcollections.gandanga.orbit.Models.ShiftOffer;
import com.carefulcollections.gandanga.orbit.Models.SwapShift;
import com.carefulcollections.gandanga.orbit.Models.Task;
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
import java.util.Date;

/**
 * Created by Gandanga on 2018-03-07.
 */

public class InboxFragment extends ListFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
   ArrayList<SwapShift> swap_posts;
   ArrayList<ShiftOffer> offer_posts;
    RecyclerView listView;
    ArrayList<InboxItem> inboxItems;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout inbox_view;

    public InboxFragment(){

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
        View v = inflater.inflate(R.layout.fragment_inbox, container, false);
        swap_posts = new ArrayList<>();
        offer_posts = new ArrayList<>();
        listView = v.findViewById(R.id.inbox_list);
        inboxItems = new ArrayList<>();
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        inbox_view = v.findViewById(R.id.inbox_view);
        final FragmentActivity activity = getActivity();
        getSwapRequests();
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
                SwapShift swapShift = gson.fromJson(element, SwapShift.class);
                if (!swap_posts.contains(swapShift)) {
                    Log.d("Contained","Yes not contained");
                    swap_posts.add(0, swapShift);
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
        if(swap_posts.size()>0){
            for(int i = 0;i<swap_posts.size();i++){
                SwapShift swap = swap_posts.get(i);
                InboxItem cur = new InboxItem(swap.id,swap.swap_shift,swap.with_shift,0,swap.employee_id,swap.requestor_id,0,swap.reason,swap.approval,swap.name,swap.surname,swap.created_at,swap.shift_date,InboxItem.ItemType.ONE_ITEM);
                inboxItems.add(cur);
            }

        }else{
            try{
                InboxItem cur = new InboxItem(0,0,"",0,0,0,0,"No Available Swap Offer",0,"No Available Shift Swap","","","", InboxItem.ItemType.THREE_ITEM);
                inboxItems.add(cur);
            }catch(Exception e){
                Log.d("Error",e.getMessage().toString());
            }

        }
        if(offer_posts.size()>0){
            for(int i=0;i<offer_posts.size();i++){
                ShiftOffer offer = offer_posts.get(i);
                InboxItem cur = new InboxItem(offer.id,0,"",offer.offer_shift,offer.employee_id,0,offer.team_member,offer.reason,offer.approval,offer.name,offer.surname,offer.created_at,offer.shift_date,InboxItem.ItemType.TWO_ITEM);
                inboxItems.add(cur);
            }
        }
        else{
            try{
                InboxItem cur = new InboxItem(0,0,"",0,0,0,0,"No Available Shift Offer",0,"No Available Shift Offers","","","", InboxItem.ItemType.THREE_ITEM);
                inboxItems.add(cur);
            }catch(Exception e){
                Log.d("Error",e.getMessage().toString());
            }

        }
        EmployeeInboxAdapter itemArrayAdapter = new EmployeeInboxAdapter(inboxItems,getActivity());

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(itemArrayAdapter);
    }

    public void getSwapRequests(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_swap_requests/"+pref.id;
        swap_posts.clear();
        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("swap_requests");
                    Log.d("Response",response_obj.toString());
                    if (response_obj.length() > 0) {
//
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            SwapShift swap = gson.fromJson(element, SwapShift.class);
                            if(!swap_posts.contains(swap)){
                                swap_posts.add(swap);
                            }

                        }
                    }else{

                    }

                    getOffRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Data error, please try again", Toast.LENGTH_LONG).show();
//                    showProgress(false);
//                            Intent intent = new Intent(getActivity(),MainActivity.class);
//                            startActivity(intent);
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

    public void getOffRequests(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Credentials credentials = EasyPreference.with(getActivity()).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(getActivity()).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_off_requests/"+pref.id;
        offer_posts.clear();
        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("off_requests");
                    Log.d("Response",response_obj.toString());
                    if (response_obj.length() > 0) {
//
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();

                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            ShiftOffer offer = gson.fromJson(element, ShiftOffer.class);
                           if(!offer_posts.contains(offer)){
                               offer_posts.add(offer);
                           }
                        }
//
                    }
                    setupAdapter();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
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
    }

    @Override
    public void onRefresh() {
        inboxItems.clear();
        swipeRefreshLayout.setRefreshing(true);
        Log.d("Refreshing","refresging");
        getSwapRequests();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
