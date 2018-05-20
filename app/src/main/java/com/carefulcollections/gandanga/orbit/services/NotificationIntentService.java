package com.carefulcollections.gandanga.orbit.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.carefulcollections.gandanga.orbit.Models.Comment;
import com.carefulcollections.gandanga.orbit.Models.Shift;
import com.carefulcollections.gandanga.orbit.Models.Task;

/**
 * Created by Gandanga on 2018-01-12.
 */

public class NotificationIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

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
        //Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            String type = intent.getStringExtra("type");


        }
        catch(Exception e) {

        }
    }

    private void processDeleteNotification() {
        // Log something?
        createIntentDeleteNotification(getApplicationContext());
    }

    private void processStartNotification(Comment comment, String subject, String title) {
        // Do something. For example, fetch fresh data from backend to create a rich notification?
//        Intent intent = new Intent(getApplicationContext(), ArticleComment.class);
//        Post post = new Post(comment.question_id, "", "", "", "", "", "", "", "", 0, 0);
//        intent.putExtra("selected_post", post);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        ctx.getApplicationContext().startActivity(intent);
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentTitle("Article Comment: " + subject + " - " + title)
//                .setAutoCancel(true)
//                .setColor(getResources().getColor(R.color.colorAccent))
//                .setContentText(comment.comment_text)
//                .setSmallIcon(R.drawable.ic_notification_icon_text);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                NOTIFICATION_ID,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));
//        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void processStartNotificationPost(Shift post) {
//        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
////        Post post = new Post(comment.question_id, "", "", "", "", "", "", "", "", 0, 0);
//        intent.putExtra("selected_post", post);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        ctx.getApplicationContext().startActivity(intent);
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentTitle("New Post: " + post.subject + " - " + post.title)
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
