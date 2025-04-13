package com.example.eventtrackerfinalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract data from the intent
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return; // Exit if the bundle is null
        }

        String title = bundle.getString(Constants.EXTRA_EVENT);
        String description = bundle.getString(Constants.EXTRA_DESCRIPTION);

        if (title == null || description == null) {
            return; // Exit if required data is missing
        }

        // Use NotificationHelper to show the notification
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.showNotification(title, description);
    }
}
