package com.winservices.wingoods.models;

import android.content.Context;

import com.winservices.wingoods.utils.SharedPrefManager;

public class RemoteConfigParams {

    private int googlePlayVersion;
    private String welcomeMessage;

    public RemoteConfigParams(Context context) {
        SharedPrefManager sp = SharedPrefManager.getInstance(context);
        this.googlePlayVersion = sp.getGooglePlayVersion();
        this.welcomeMessage = sp.getWelcomeMessage();
    }

    public int getGooglePlayVersion() {
        return googlePlayVersion;
    }

    public void setGooglePlayVersion(int googlePlayVersion) {
        this.googlePlayVersion = googlePlayVersion;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
}
