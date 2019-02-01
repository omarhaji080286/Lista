package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.utils.Constants;

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

    public Shop getShopById(int serverShopId) {

        Cursor cursor = db.getShopById(serverShopId);
        cursor.moveToNext();
        //int serverShopId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String shopName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_NAME));
        String shopPhone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_PHONE));
        String openingTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_OPENING_TIME));
        String closingTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CLOSING_TIME));

        Log.d(TAG, Constants.TAG_LISTA + "getShopById called");
        Shop shop = new Shop();
        shop.setServerShopId(serverShopId);
        shop.setShopName(shopName);
        shop.setShopPhone(shopPhone);
        shop.setOpeningTime(openingTime);
        shop.setClosingTime(closingTime);

        return shop;

    }


}
