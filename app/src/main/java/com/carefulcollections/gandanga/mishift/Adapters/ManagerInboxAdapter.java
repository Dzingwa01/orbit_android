package com.carefulcollections.gandanga.mishift.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.carefulcollections.gandanga.mishift.EmployeesManager.TeamMemberDetails;
import com.carefulcollections.gandanga.mishift.Helpers.Credentials;
import com.carefulcollections.gandanga.mishift.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.mishift.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.mishift.Models.InboxItem;
import com.carefulcollections.gandanga.mishift.Models.Item;
import com.carefulcollections.gandanga.mishift.Models.LeaveRequest;
import com.carefulcollections.gandanga.mishift.Models.Message;
import com.carefulcollections.gandanga.mishift.Models.User;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
import com.carefulcollections.gandanga.mishift.R;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gandanga on 2018-06-11.
 */

public class ManagerInboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    private ArrayList<LeaveRequest> itemList;
    Context ctx;

    public ManagerInboxAdapter(ArrayList<LeaveRequest> itemList,Context ctx){
        this.itemList = itemList;
        this.ctx = ctx;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_ONE) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.off_request_inbox, parent, false);
            return new MyViewHolder(itemView);
        } else if (viewType == TYPE_TWO) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.off_requests_empty, parent, false);
            return new MyEmptyViewHolder(itemView);
        }
        return null;
    }
    // determine which layout to use for the row
    @Override
    public int getItemViewType(int position) {
        LeaveRequest item = itemList.get(position);
        if (item.getType() == LeaveRequest.ItemType.ONE_ITEM) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }

    }
    static class MyEmptyViewHolder extends RecyclerView.ViewHolder {
        private TextView empty;

        public MyEmptyViewHolder(View v) {
            super(v);
           empty = v.findViewById(R.id.empty);
        }
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView shift_date;
        private TextView requestor_name;
        private TextView off_start_date,off_end_date;
        private Button off_accept;
        private Button more_details;
        public MyViewHolder(View v) {
            super(v);
            requestor_name = v.findViewById(R.id.requestor_name);
            off_start_date = v.findViewById(R.id.off_start_date);
            off_end_date = v.findViewById(R.id.off_end_date);
            off_accept = v.findViewById(R.id.accept);
            more_details = v.findViewById(R.id.more_details);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_ONE:
                initLayoutOne((MyViewHolder) holder, position);
                break;
            case TYPE_TWO:
                initLayoutTwo((MyEmptyViewHolder) holder, position);
                break;
           default:
                break;
        }

    }
    private void initLayoutOne(final MyViewHolder holder, int pos) {
        final LeaveRequest request = itemList.get(pos);

        if (request != null) {
            holder.requestor_name.setText(WordUtils.capitalizeFully(request.name )+ " " + WordUtils.capitalizeFully(request.surname));
            holder.off_start_date.setText(request.off_start_date + " - " + request.off_start_time);
            holder.off_end_date.setText(request.off_end_date + " - "+ request.off_end_time);
            holder.off_accept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    sendRequestAccept(request,holder);
                }
            });
            holder.more_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRequestDetailsDialog("Leave Details",request);
                }
            });
        }
    }

    private void initLayoutTwo(final MyEmptyViewHolder holder, int pos) {
        final LeaveRequest request = itemList.get(pos);

        if (request != null) {
            holder.empty.setText(request.reason);
        }
    }

    @Override
    public int getItemCount() {

        return itemList == null ? 0 : itemList.size();
    }
    private void pushMessage(Message message){
        final Message push_message = message;
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        final String url = credentials.server_url+"chat_server.php";
        RequestQueue queue = Volley.newRequestQueue(ctx);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response32", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
//                        Log.d("Error.Response", error.getMessage());
//                        Toast.makeText(getActivity(), "An error occured while trying to send your message, please try again", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("event_type","Send_Message");
                params.put("id",String.valueOf(push_message.id));
                params.put("first_name", push_message.first_name);
                params.put("last_name", push_message.last_name);
                params.put("user_picture_url",push_message.user_picture_url);
                params.put("message_picture_url",push_message.message_picture_url);
                params.put("message_text",push_message.message_text);

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void sendOffRequestAccept(final LeaveRequest shift,final MyViewHolder holderOne){
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept_leave_request/"+shift.id+"/"+pref.id;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
//                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    JSONObject push_msg = result.getJSONObject("push_msg");

                    JsonParser parser = new JsonParser();
                    JsonElement msg = parser.parse(push_msg.toString());
                    Gson gson = new Gson();
                    Message push_message = gson.fromJson(msg,Message.class);
                    pushMessage(push_message);
                    Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                    holderOne.off_accept.setEnabled(false);
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
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("swap_shift", String.valueOf(shift.id));
                return params;
            }

        };
        VolleySingleton.getInstance(ctx).addToRequestQueue(multipartRequest);
    }
    public void showRequestDetailsDialog(String title,final LeaveRequest request) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        final View view = inflater.inflate(R.layout.request_more_details, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(title);
//        final EditText captionText = view.findViewById(R.id.caption_text);
        TextView off_start_date = view.findViewById(R.id.off_start_date);
        TextView off_end_date = view.findViewById(R.id.off_end_date);
        TextView requestor_name = view.findViewById(R.id.requestor_name);
        TextView reason = view.findViewById(R.id.reason);
       TextView leave_type = view.findViewById(R.id.leave_type);
       TextView leave_category = view.findViewById(R.id.leave_category);

        requestor_name.setText(WordUtils.capitalizeFully(request.name )+ " " + WordUtils.capitalizeFully(request.surname));
        off_start_date.setText(request.off_start_date + " - " + request.off_start_time);
        off_end_date.setText(request.off_end_date + " - "+ request.off_end_time);
        reason.setText(request.reason);
        leave_type.setText(request.leave_type);
        if (request.off_category == "all_day") {
            leave_category.setText("Yes");
        }else{
            leave_category.setText("No");
        }
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                    }
                });
            }
        });
        alertDialogBuilder.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        MyViewHolder holderOne =new MyViewHolder(view);
                        sendRequestAcceptDialog(request);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void sendRequestAcceptDialog(final LeaveRequest shift){
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept_leave_request/"+shift.id+"/"+pref.id;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
//                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    JSONObject push_msg = result.getJSONObject("push_msg");

                    JsonParser parser = new JsonParser();
                    JsonElement msg = parser.parse(push_msg.toString());
                    Gson gson = new Gson();
                    Message push_message = gson.fromJson(msg,Message.class);
                    pushMessage(push_message);
                    Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
//                    holderOne.off_accept.setEnabled(false);
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
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("swap_shift", String.valueOf(shift.id));
                return params;
            }

        };
        VolleySingleton.getInstance(ctx).addToRequestQueue(multipartRequest);
    }
    private void sendRequestAccept(final LeaveRequest shift,final MyViewHolder holderOne){
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept_leave_request/"+shift.id+"/"+pref.id;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
//                    showProgress(false);
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                    JSONObject push_msg = result.getJSONObject("push_msg");

                    JsonParser parser = new JsonParser();
                    JsonElement msg = parser.parse(push_msg.toString());
                    Gson gson = new Gson();
                    Message push_message = gson.fromJson(msg,Message.class);
                    pushMessage(push_message);
                    Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
                    holderOne.off_accept.setEnabled(false);
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
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("swap_shift", String.valueOf(shift.id));
                return params;
            }

        };
        VolleySingleton.getInstance(ctx).addToRequestQueue(multipartRequest);
    }
}
