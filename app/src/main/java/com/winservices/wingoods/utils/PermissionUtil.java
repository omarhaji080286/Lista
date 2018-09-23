package com.winservices.wingoods.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.winservices.wingoods.R;

public class PermissionUtil {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PermissionUtil(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preference), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void updatePermissionPreference(String permission){

        switch (permission){
            case "access_fine_location" :
                editor.putBoolean(context.getString(R.string.permission_access_fine_location), true);
                editor.commit();
                break;
            case "access_coarse_location" :
                editor.putBoolean(context.getString(R.string.permission_access_coarse_location), true);
                editor.commit();
                break;
        }

    }

    public boolean checkPermissionPreference(String permission){

        boolean isShown = false;
        switch (permission){
            case "access_fine_location" :
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_access_fine_location), false);
                break;
            case "access_coarse_location" :
                isShown = sharedPreferences.getBoolean(context.getString(R.string.permission_access_coarse_location), false);
                break;
        }
        return isShown;

    }

}
