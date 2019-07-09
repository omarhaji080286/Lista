package com.winservices.wingoods.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SharedPrefManager {


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
        editor.putString(Shop.PREFIX_SHOP + serverShopId, path);
        editor.apply();
    }

    public void storeUserImagePath(int serverUserId, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.PREFIX_USER + serverUserId, path);
        editor.apply();
    }

    public String getShopImagePath(int serverShopId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Shop.PREFIX_SHOP + serverShopId, null);
    }

    public void storeImageToFile(final String encodedImage, final String imageType, final String prefix, final int sharedPrefKey) {
        final String file_path = context.getFilesDir().getPath() + "/" + imageType;
        Thread thread = new Thread(){
            public void run() {
                File dir = new File(file_path);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, prefix + sharedPrefKey + "."+imageType);
                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(file);
                    Bitmap bitmap = UtilsFunctions.stringToBitmap(context, encodedImage);
                    Bitmap.CompressFormat compressFormat;
                    switch (imageType){
                        case "png":
                            compressFormat = Bitmap.CompressFormat.PNG;
                            break;
                        case "jpg":
                            compressFormat = Bitmap.CompressFormat.JPEG;
                            break;
                        default:
                            compressFormat = Bitmap.CompressFormat.JPEG;
                    }
                    bitmap.compress(compressFormat, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                storeImagePath(prefix+sharedPrefKey,file.getAbsolutePath());
            }
        };
        thread.run();
    }

    private void storeImagePath(String key, String path) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, path);
        editor.apply();
    }

    public String getImagePath(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public String getUserImagePath(int serverUserId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.PREFIX_USER + serverUserId, null);
    }

    public String getShopTypeImagePath(int shopTypeId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ShopType.PREFIX_SHOP_TYPE + shopTypeId, null);
    }

}
