package com.winservices.wingoods.models;

import android.content.Context;

import com.winservices.wingoods.utils.SharedPrefManager;

public class RemoteConfigParams {

    private int googlePlayVersion;
    private String appMessages;

    public RemoteConfigParams(Context context) {
        SharedPrefManager sp = SharedPrefManager.getInstance(context);
        this.googlePlayVersion = sp.getGooglePlayVersion();
        this.appMessages = sp.getAppMessages();
    }

    public int getGooglePlayVersion() {
        return googlePlayVersion;
    }

    public String getAppMessages() {
        return appMessages;
    }

}
