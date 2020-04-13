package com.winservices.wingoods.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.LauncherActivity;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.List;
import java.util.Map;

public class ListaMessagingService extends FirebaseMessagingService {

    private final static String TAG = ListaMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: " + token);
        storeToken(token);
    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = "none";
        String body = "none";
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
        //createNotificationWithImage(title, body, null, intent);
    }

/*    public void createNotificationWithImage(String title, String message, Bitmap image, Intent intent) {
        int requestID = (int) System.currentTimeMillis();
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = "my_channel_01";// The id of the channel.

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.lista_logo);

        mBuilder.setSound(soundUri);

        if (image != null) {
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(false)
                    .setLargeIcon(image)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(image).setSummaryText(message).bigLargeIcon(null))
                    .setColor(Color.GREEN)
                    .setContentIntent(pendingIntent);
        }
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            if (soundUri != null) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                notificationChannel.setSound(soundUri, audioAttributes);
            }

            assert mNotificationManager != null;
            mBuilder.setChannelId(CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(requestID, mBuilder.build());

    }*/

    private void sendNotification(String title, String body, Intent intent) {

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        //Define the intent that will fire when the user taps the notification

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int requestID = (int) System.currentTimeMillis();
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        CharSequence name = "Lista Pro FCM channel";// The user-visible name of the channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationManager.notify(requestID, notification);

    }


    private Intent getIntentNotification(Map<String, String> data) {

        Intent intent = new Intent(this, LauncherActivity.class);

        String fcm_type = data.get(Constants.FCM_TYPE);
        final String appPackageName = getPackageName();

        if (fcm_type != null) {
            switch (fcm_type) {
                case Constants.FCM_NOTIFICATION_UPDATE:
                    try {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                    } catch (ActivityNotFoundException anfe) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                    }

                    PackageManager manager = this.getPackageManager();
                    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                    if (infos.size() > 0) {
                        //Then there is an Application(s) can handle your intent
                        Log.d(TAG, "Notification intent ok");
                    } else {
                        //No Application can handle your intent
                        Log.d(TAG, "Notification intent NOT ok");
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    break;

                case Constants.FCM_NOTIFICATION_INVITATION:
                    Log.d(TAG, "fcm_type: " + fcm_type);
                    break;

                default:
                    Log.d(TAG, "fcm_type: " + fcm_type);
                    break;
            }

        }

        return intent;
    }

}
