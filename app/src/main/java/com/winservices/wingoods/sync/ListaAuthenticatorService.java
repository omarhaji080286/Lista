package com.winservices.wingoods.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class ListaAuthenticatorService extends Service {

    private ListaAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new ListaAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
