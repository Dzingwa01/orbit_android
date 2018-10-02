package com.carefulcollections.gandanga.mishift.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.carefulcollections.gandanga.mishift.EmployeesManager.MainActivity;
import com.carefulcollections.gandanga.mishift.Models.Comment;
import com.carefulcollections.gandanga.mishift.Models.Shift;
import com.carefulcollections.gandanga.mishift.Models.Task;
import com.carefulcollections.gandanga.mishift.Models.UserPref;
import com.carefulcollections.gandanga.mishift.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by Gandanga on 2018-01-12.
 */

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    String CHANNEL_ID = "MS1235";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("HelloPano", "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            String type = intent.getStringExtra("type");
            if (type.equals("comment")) {
                Comment comment = (Comment) intent.getSerializableExtra("message");
                if (ACTION_START.equals(action)) {
                    processStartNotificationComment(comment);
                }
                if (ACTION_DELETE.equals(action)) {
                    processDeleteNotification();
                }
            }
        }
        catch(Exception e) {

        }
    }

    private void processDeleteNotification() {
        // Log something?
        createIntentDeleteNotification(getApplicationContext());
    }


    private void processStartNotificationComment(Comment user_comment) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(user_comment.first_name+" "+user_comment.last_name)
                .setContentText(user_comment.comment_text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(user_comment.id, mBuilder.build());
    }

    private void processStartNotificationQuestion(Task post) {
//        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
////        Post post = new Post(comment.question_id, "", "", "", "", "", "", "", "", 0, 0);
//        intent.putExtra("selected_post", post);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        ctx.getApplicationContext().startActivity(intent);
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentTitle("New Question: " + post.subject + " - " + post.title)
//                .setAutoCancel(true)
//                .setColor(getResources().getColor(R.color.colorAccent))
//                .setContentText(post.post_content)
//                .setSmallIcon(R.drawable.ic_notification_icon_text);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                NOTIFICATION_ID,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));
//        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
