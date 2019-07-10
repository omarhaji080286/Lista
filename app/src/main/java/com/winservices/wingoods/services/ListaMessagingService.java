package com.winservices.wingoods.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.utils.SharedPrefManager;

public class ListaMessagingService extends FirebaseMessagingService {

    private final static String TAG = ListaMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: "+ token);
        storeToken(token);
    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String token = SharedPrefManager.getInstance(getApplicationContext()).getToken();
        Log.d(TAG, "token: " + token);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        SyncHelper.sync(getApplicationContext());

    }

    /*private void sendNotification(String body, String title) {

        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        } catch (android.content.ActivityNotFoundException anfe) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.lista_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        Log.d(TAG, "sendNotification: ");
        
    }*/


}
