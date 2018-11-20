package com.winservices.wingoods.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String PREFIX_SHOP = "shop_";
    private static final String SHARED_PREF_NAME = "listaSharedPreferences";
    private static final String KEY_ACCESS_TOKEN = "token";
    private static SharedPrefManager instance;
    private Context context;

    private SharedPrefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void storeToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void storeShopImagePath(int serverShopId, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFIX_SHOP + serverShopId, path);
        editor.apply();
    }

    public String getShopImagePath(int serverShopId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREFIX_SHOP + serverShopId, null);
    }

}
