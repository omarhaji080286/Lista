package com.winservices.wingoods.dbhelpers;

import android.content.Context;

import com.winservices.wingoods.models.Shop;

public class ShopsDataManager {

    private final static String TAG = ShopsDataManager.class.getSimpleName();
    private DataBaseHelper db;
    private Context context;

    public ShopsDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        this.context = context;
    }

    void insertShop(Shop shop) {
        db.insertShop(shop);
    }


}
