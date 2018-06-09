package com.carefulcollections.gandanga.orbit.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.carefulcollections.gandanga.orbit.EmployeesManager.MainActivity;
import com.carefulcollections.gandanga.orbit.EmployeesManager.ShiftDetails;
import com.carefulcollections.gandanga.orbit.Helpers.Credentials;
import com.carefulcollections.gandanga.orbit.Helpers.VolleyMultipartRequest;
import com.carefulcollections.gandanga.orbit.Helpers.VolleySingleton;
import com.carefulcollections.gandanga.orbit.Models.Comment;
import com.carefulcollections.gandanga.orbit.Models.InboxItem;
import com.carefulcollections.gandanga.orbit.Models.Item;
import com.carefulcollections.gandanga.orbit.Models.Message;
import com.carefulcollections.gandanga.orbit.Models.SwapShift;
import com.carefulcollections.gandanga.orbit.Models.UserPref;
import com.carefulcollections.gandanga.orbit.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gandanga on 2018-05-29.
 */

public class EmployeeInboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    private static final int TYPE_THREE = 3;
    Context ctx;
    private ArrayList<InboxItem> itemList;

    // Constructor of the class
    public EmployeeInboxAdapter(ArrayList<InboxItem> itemList, Context ctx) {
        this.itemList = itemList;
        this.ctx = ctx;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    // determine which layout to use for the row
    @Override
    public int getItemViewType(int position) {
        InboxItem item = itemList.get(position);
        if (item.getType() == InboxItem.ItemType.ONE_ITEM) {
            return TYPE_ONE;
        } else if (item.getType() == InboxItem.ItemType.TWO_ITEM) {
            return TYPE_TWO;
        }
        else if (item.getType() == InboxItem.ItemType.THREE_ITEM) {
            return TYPE_THREE;
        }
        else {
            return -1;
        }
    }


    // specify the row layout file and click for each row
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.swap_requests_layout, parent, false);
            return new ViewHolderOne(view);
        } else if (viewType == TYPE_TWO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.off_request_layout, parent, false);
            return new ViewHolderTwo(view);
        }
        else if (viewType == TYPE_THREE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.no_offers_swaps, parent, false);
            return new ViewHolderThree(view);
        }
        else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int listPosition) {
        switch (holder.getItemViewType()) {
            case TYPE_ONE:
                initLayoutOne((ViewHolderOne) holder, listPosition);
                break;
            case TYPE_TWO:
                initLayoutTwo((ViewHolderTwo) holder, listPosition);
                break;
            case TYPE_THREE:
                initLayoutThree((ViewHolderThree)holder, listPosition);
                break;
            default:
                break;
        }
    }

    private void initLayoutOne(final ViewHolderOne holder, int pos) {
        final InboxItem shift = itemList.get(pos);
        if (shift != null) {
            holder.shift_date.setText(shift.shift_date);
            holder.requestor_name.setText(shift.name + " - " + shift.surname);
            holder.swap_shift_date.setText(shift.with_shift);
            }
            holder.swap_accept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    sendSwapAccept(shift,holder);
                }
            });
        holder.more_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSwapConfirmDialog("Shift Swap Details",shift);
            }
        });
    }
    public void showSwapConfirmDialog(String title,final InboxItem shift) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        final View view = inflater.inflate(R.layout.more_details_layout, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(title);
//        final EditText captionText = view.findViewById(R.id.caption_text);
        TextView shift_date = view.findViewById(R.id.shift_date);
        TextView requestor_name = view.findViewById(R.id.requestor_name);
        TextView swap_shift_date = view.findViewById(R.id.swap_shift_date);
        TextView swap_reason = view.findViewById(R.id.swap_reason);
        shift_date.setText(shift.shift_date);
        requestor_name.setText(shift.name + " - " + shift.surname);
        swap_shift_date.setText(shift.with_shift);
        swap_reason.setText(shift.reason);
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
                        ViewHolderOne holderOne =new ViewHolderOne(view);
                        sendSwapAcceptDialog(shift);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showOfferConfirmDialog(String title,final InboxItem shift) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        final View view = inflater.inflate(R.layout.offer_more_details, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(title);
//        final EditText captionText = view.findViewById(R.id.caption_text);
        TextView shift_date = view.findViewById(R.id.shift_date);
        TextView employee_name = view.findViewById(R.id.employee_name);
        TextView swap_reason = view.findViewById(R.id.offer_reason);
        shift_date.setText(shift.shift_date);
        employee_name.setText(shift.name + " - " + shift.surname);
        swap_reason.setText(shift.reason);
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
                    ViewHolderTwo holderOne =new ViewHolderTwo(view);
                        sendOfferAcceptDialog(shift);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

    private void sendSwapAccept(final InboxItem shift,final ViewHolderOne holderOne){
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept_shift_swap/"+shift.id;

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
                    holderOne.swap_accept.setEnabled(false);
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

    private void sendSwapAcceptDialog(final InboxItem shift){
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept_shift_swap/"+shift.id;

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
//                    holderOne.swap_accept.setEnabled(false);
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

    private void initLayoutTwo(final ViewHolderTwo holder, int pos) {
        final InboxItem shift = itemList.get(pos);
        if(shift.id!=0){
            if (shift != null) {
                holder.shift_date.setText(shift.shift_date);
                holder.employee_name.setText(shift.name + " - " + shift.surname);
//            holder.swap_shift_date.setText(shift.shift_date);
                holder.accept_offer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendOfferAccept(shift, holder);
                    }
                });
                holder.more_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showOfferConfirmDialog("Shift Offer Details",shift);
                    }
                });
            }
        }else{
            holder.shift_date.setText(shift.shift_date);
        }

    }

    private void initLayoutThree(final ViewHolderThree holder, int pos) {
        final InboxItem shift = itemList.get(pos);
        if(shift.id==0){
            if (shift != null) {
                holder.title.setText(shift.reason);
            }
        }

    }

    private void sendOfferAccept(final InboxItem shift,final ViewHolderTwo viewHolderTwo){
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept_shift_offer/"+shift.id;

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
                    viewHolderTwo.accept_offer.setEnabled(false);
//                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                    startActivity(intent);
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
                params.put("shift_offer", String.valueOf(shift.id));
                return params;
            }

        };
        VolleySingleton.getInstance(ctx).addToRequestQueue(multipartRequest);
    }

    private void sendOfferAcceptDialog(final InboxItem shift){
        Credentials credentials = EasyPreference.with(ctx).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept_shift_offer/"+shift.id;

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
//                    viewHolderTwo.accept_offer.setEnabled(false);
//                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                    startActivity(intent);
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
                params.put("shift_offer", String.valueOf(shift.id));
                return params;
            }

        };
        VolleySingleton.getInstance(ctx).addToRequestQueue(multipartRequest);
    }

    // Static inner class to initialize the views of rows
    static class ViewHolderOne extends RecyclerView.ViewHolder {
        private TextView shift_date;
        private TextView requestor_name;
        private TextView swap_shift_date;
        private Button swap_accept;
        private Button more_details;
        public ViewHolderOne(View v) {
            super(v);
            shift_date = v.findViewById(R.id.shift_date);
            requestor_name = v.findViewById(R.id.requestor_name);
            swap_shift_date = v.findViewById(R.id.swap_shift_date);
            swap_accept = v.findViewById(R.id.accept);
            more_details = v.findViewById(R.id.more_details);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {
        private TextView shift_date;
        private TextView employee_name;
        private Button accept_offer;
        private Button more_details;

        public ViewHolderTwo(View v) {
            super(v);
            shift_date = v.findViewById(R.id.shift_date);
            employee_name = v.findViewById(R.id.employee_name);
            accept_offer = v.findViewById(R.id.accept);
            more_details = v.findViewById(R.id.more_details);
//            task_description = v.findViewById(R.id.task_description);
        }
    }
    static class ViewHolderThree extends RecyclerView.ViewHolder {
        private TextView title;

        public ViewHolderThree(View v) {
            super(v);
            title = v.findViewById(R.id.title);

        }
    }
}