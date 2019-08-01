package com.winservices.wingoods.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class EventService {

    private FirebaseAnalytics mFirebaseAnalytics;
    private final static String TAG = EventService.class.getSimpleName();


    public EventService(Context context) {
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logEvent(String eventName, Bundle eventParams){
        //TODO for prod
        //mFirebaseAnalytics.logEvent(eventName, eventParams);
        Log.d(TAG, "event Name = " + eventName);
        Log.d(TAG, "event Params = " + eventParams.toString());
    }



}
