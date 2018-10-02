package com.carefulcollections.gandanga.mishift.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Gandanga on 2018-01-16.
 */

public class NotificationServiceStarterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.receiveEvents(context);
    }
}
