package com.winservices.wingoods.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.dbhelpers.Synchronizer;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.sync.ListaSyncAdapter;

import java.util.List;

public class NetworkMonitor extends BroadcastReceiver {
    //check the connection status
    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        if (checkNetworkConnection(context)) {

            //SyncHelper.sync(context);
        }

    }


}
