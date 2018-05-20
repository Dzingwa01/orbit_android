package com.carefulcollections.gandanga.orbit.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by Gandanga on 2018-01-12.
 */

public class NotificationEventReceiver extends BroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
    private static final int NOTIFICATIONS_INTERVAL_IN_HOURS = 2;

    static Context ctx;

    public static void receiveEvents(final Context context) {

        ctx = context;
        //Log.d("Notification_Pencil_ch", "Received");
        PendingIntent alarmIntent = getStartPendingIntent(context);
        PusherOptions options = new PusherOptions();
        options.setCluster("ap2");
        Pusher pusher = new Pusher("347d0dccd5f2a3e703a2", options);
        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                //Log.d("Message", "message");
                //Log.d("Triggered_ENtry", data);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(data);
                Gson gson = new Gson();
//                Comment user_comment = gson.fromJson(element, Comment.class);
//                final UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
//                if (!pref._id.equals(user_comment.author_id)) {
//                    checkQuestion(user_comment);
//                } else {
//                    //Log.d("Own_Event", "Can not receive own event");
//                }
            }
        });
        channel.bind("my-event-post", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                //Log.d("Message", "message");
                //Log.d("Triggered_ENtry_Post", data);

            }
        });
        channel.bind("my-event-question", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                //Log.d("Message", "message");

            }
        });
        pusher.connect();
    }


    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        receiveEvents(context);
    }

}
