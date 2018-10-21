package com.winservices.wingoods.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


public class ListaSyncService extends Service {

    private final String LOG_TAG = ListaSyncService.class.getSimpleName();
    //FOR SYNC ADAPTER
    private static final Object sSyncAdapterLock = new Object();
    private static ListaSyncAdapter listaSyncAdapter = null;

    @Override
    public void onCreate() {
        //android.os.Debug.waitForDebugger();
        Log.d(LOG_TAG, " onCreate ");
        synchronized (sSyncAdapterLock){
            if (listaSyncAdapter==null){
                listaSyncAdapter = new ListaSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return listaSyncAdapter.getSyncAdapterBinder();
    }

}
