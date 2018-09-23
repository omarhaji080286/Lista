package com.winservices.wingoods.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class Controlers {

    public static boolean inputOk(String s) {
        boolean inputOk;
        if (TextUtils.isEmpty(s.trim())) {
            inputOk = false;
        } else {
            inputOk = true;
        }
        return inputOk;
    }

    public static boolean whatsappInstalled(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
