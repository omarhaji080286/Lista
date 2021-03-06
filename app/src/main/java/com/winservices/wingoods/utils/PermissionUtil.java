package com.winservices.wingoods.utils;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.winservices.wingoods.R;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class PermissionUtil {

    private final static int REQUEST_ACCESS_FINE_LOCATION = 101;
    private final static int REQUEST_ACCESS_COARSE_LOCATION = 102;
    public final static int REQUEST_ACCESS_CAMERA = 103;
    public final static int REQUEST_ACCESS_NOTIFICATION= 104;

    public final static String TXT_FINE_LOCATION = "access_fine_location";
    public final static String TXT_COARSE_LOCATION = "access_coarse_location";
    public final static String TXT_CAMERA = "access_camera";
    public final static String TXT_NOTIFICATION = "access_notification";


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
            case TXT_FINE_LOCATION:
                status = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case TXT_COARSE_LOCATION:
                status = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                break;
            case TXT_CAMERA:
                status = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                break;
            case TXT_NOTIFICATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    status = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY);
                }
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
            case TXT_NOTIFICATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(activity, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_ACCESS_NOTIFICATION);
                }
                break;
        }
    }

    public void showPermissionExplanation(final String permission, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (permission.equals(TXT_FINE_LOCATION)) {
            builder.setMessage("This app needs to access your location. Please allow.");
            builder.setTitle("Location permission needed..");
        } else if (permission.equals(TXT_COARSE_LOCATION)) {
            builder.setMessage("This app needs to access your location. Please allow.");
            builder.setTitle("Location permission needed..");
        } else if (permission.equals(TXT_CAMERA)) {
            builder.setMessage("This app needs to access your camera. Please allow.");
            builder.setTitle("Camera permission needed..");
        }

        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermission(permission, activity);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void goToAppSettings() {
        Toast.makeText(context, "Please allow Camera/Location permissions in your app settings", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
