package com.winservices.wingoods.dbhelpers;

import android.content.Context;

import com.winservices.wingoods.models.Order;

public class OrdersDataManager {

    private final static String TAG = OrdersDataManager.class.getSimpleName();
    private DataBaseHelper db;
    private Context context;

    public OrdersDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        this.context = context;
    }

    void insertOrder(Order order) {
            db.insertOrder(order);
    }
}
