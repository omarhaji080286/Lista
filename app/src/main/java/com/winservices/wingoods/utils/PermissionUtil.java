package com.winservices.wingoods.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.winservices.wingoods.R;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class PermissionUtil {

    private final static int REQUEST_ACCESS_FINE_LOCATION = 101;
    private final static int REQUEST_ACCESS_COARSE_LOCATION = 102;
    public final static int REQUEST_ACCESS_CAMERA = 103;

    public final static String TXT_FINE_LOCATION = "access_fine_location";
    public final static String TXT_COARSE_LOCATION = "access_coarse_location";
    public final static String TXT_CAMERA = "access_camera";

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

    public int checkPermission(String permission) {
        int status = PackageManager.PERMISSION_DENIED;
        switch (permission) {
            case TXT_CAMERA:
                status = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                break;
        }
        return status;
    }


    public void requestPermission(String permission, Activity activity) {
        switch (permission) {
            case TXT_FINE_LOCATION:
                requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                break;
            case TXT_COARSE_LOCATION:
                requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                break;
            case TXT_CAMERA:
                requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_ACCESS_CAMERA);
                break;
        }
    }

}
