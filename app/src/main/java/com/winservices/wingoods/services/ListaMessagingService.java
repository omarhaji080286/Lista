package com.winservices.wingoods.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.LauncherActivity;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.SharedPrefManager;

import java.util.Map;

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

        String title="none";
        String body="none";
        Intent intent = new Intent(this, LauncherActivity.class);

        String token = SharedPrefManager.getInstance(getApplicationContext()).getToken();
        Log.d(TAG, "token: " + token);

        SyncHelper.sync(getApplicationContext());

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            intent = getIntentNotification(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(title, body, intent);
    }




    private void sendNotification(String title, String body, Intent intent) {

        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "notif channel";// The user-visible name of the channel.
        int importance = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        //Define the intent that will fire when the user taps the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create a notification and set the notification channel.
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.lista_logo)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(notifyID , notification);

    }


    private Intent getIntentNotification(Map<String,String> data){

        Intent intent =  new Intent(this, LauncherActivity.class);

        String data_value_1 = data.get(Constants.FCM_DATA_KEY_1);

        if( data_value_1 != null){

            if (data_value_1.equals(Constants.FCM_NOTIFICATION_UPDATE)){

                final String appPackageName = getPackageName();
                try {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                } catch (ActivityNotFoundException anfe) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }

        }

        return intent;
    }

}
