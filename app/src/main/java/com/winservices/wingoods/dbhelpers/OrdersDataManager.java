package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<Order> getOrders(int groupedStatus) {
        Cursor cursor = db.getOrders(groupedStatus);
        List<Order> orders = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int serverOrderId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                int serverUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_USER_ID));
                int serverShopId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_SHOP_ID));
                Date creationDate = UtilsFunctions.stringToDate(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CREATION_DATE)));
                int statusId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_ORDER_STATUS_ID));
                String statusName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_ORDER_STATUS_NAME));
                int orderedGoodsNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_ORDERED_GOODS_NUMBER));
                String shopName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_NAME));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_END_TIME));
                int serverShopTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_SHOP_TYPE_ID));
                String shopTypeName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_TYPE_NAME));

                Order order = new Order();
                order.setServerOrderId(serverOrderId);

                User user = new User();
                user.setServerUserId(serverUserId);
                order.setUser(user);

                Shop shop = new Shop();
                shop.setServerShopId(serverShopId);
                shop.setShopName(shopName);
                order.setShop(shop);

                ShopType shopType = new ShopType(serverShopTypeId, shopTypeName);
                shop.setShopType(shopType);

                order.setCreationDate(creationDate);
                order.setStatusId(statusId);
                order.setStatusName(statusName);
                order.setOrderedGoodsNumber(orderedGoodsNumber);
                order.setStartTime(startTime);
                order.setEndTime(endTime);

                orders.add(order);
            }
            cursor.close();
        }
        return orders;
    }
}
