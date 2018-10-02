package com.carefulcollections.gandanga.mishift.services;

import android.app.IntentService;
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
import com.carefulcollections.gandanga.mishift.Models.Comment;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
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
        Log.d("Receiving_Notifications", "Received");
        PendingIntent alarmIntent = getStartPendingIntent(context);
        PusherOptions options = new PusherOptions();
        options.setCluster("ap2");
        Pusher pusher = new Pusher("347d0dccd5f2a3e703a2", options);
        Channel channel = pusher.subscribe("my-channel");
        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                Log.d("Message Notifications", "message");
                Log.d("Triggered_ENtrysss", data);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(data);
                Gson gson = new Gson();
                Comment user_comment = gson.fromJson(element, Comment.class);
                final UserPref pref = EasyPreference.with(ctx).getObject("user_pref", UserPref.class);
//                        if(pref.id!=String.valueOf(user_comment.user_id)){
//                            Intent intent = new Intent(context, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
//                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
//                                    .setSmallIcon(R.drawable.ic_notification_icon)
//                                    .setContentTitle(user_comment.first_name+" "+user_comment.last_name)
//                                    .setContentText(user_comment.comment_text)
//                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                            NotificationManager mNotificationManager =
//                                    (NotificationManager) getActivity().getSystemService(getContext().NOTIFICATION_SERVICE);
//                            mBuilder.setContentIntent(pendingIntent);
//                            mNotificationManager.notify(user_comment.id, mBuilder.build());
//                        }
                Intent serviceIntent = NotificationIntentService.createIntentStartNotificationService(ctx);
                serviceIntent.putExtra("message", user_comment);
                serviceIntent.putExtra("type", "comment");
                if (serviceIntent != null) {
                    startWakefulService(ctx, serviceIntent);
                }
                return;

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
