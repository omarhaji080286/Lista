package com.winservices.wingoods.models;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Good {

    private int goodId = 0;
    private String goodName;
    private String goodDesc;
    private int categoryId;
    private int quantityLevelId;
    private boolean isToBuy;
    private int sync;
    private String email;
    private int crudStatus;
    private int serverGoodId;
    private int serverCategoryId;
    private int isOrdered;

    public void setServerCategoryId(int serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
    }

    public Good(String goodName, int categoryId, int quantityLevelId, boolean isToBuy, int sync, String email) {
        this.goodName = goodName;
        this.categoryId = categoryId;
        this.quantityLevelId = quantityLevelId;
        this.isToBuy = isToBuy;
        this.sync = sync;
        this.email = email;
    }

    public Good(String goodName, int categoryId, int quantityLevelId, boolean isToBuy,
                int sync, String email, int serverGoodId, int serverCategoryID) {
        this.goodName = goodName;
        this.categoryId = categoryId;
        this.quantityLevelId = quantityLevelId;
        this.isToBuy = isToBuy;
        this.sync = sync;
        this.email = email;
        this.serverGoodId = serverGoodId;
        this.serverCategoryId = serverCategoryID;
    }

    public Good(int goodId, String goodName, int categoryId, int quantityLevelId,
                boolean isToBuy, int sync, String email, int crud, int serverGoodId) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.categoryId = categoryId;
        this.quantityLevelId = quantityLevelId;
        this.isToBuy = isToBuy;
        this.sync = sync;
        this.email = email;
        this.serverGoodId = serverGoodId;
        this.crudStatus = crud;
    }

    public Good(int goodId, String goodName, int categoryId, int quantityLevelId, boolean isToBuy, int sync, int crud, String email) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.categoryId = categoryId;
        this.quantityLevelId = quantityLevelId;
        this.isToBuy = isToBuy;
        this.sync = sync;
        this.email = email;
        this.crudStatus = crud;
    }

    public Good(int goodId, String goodName, int categoryId, int quantityLevelId, boolean isToBuy) {
        this.goodId = goodId;
        this.goodName = goodName;
        this.categoryId = categoryId;
        this.quantityLevelId = quantityLevelId;
        this.isToBuy = isToBuy;
    }

    public Good(String goodName, int categoryId, int quantityLevelId, boolean isToBuy, int sync) {
        this.goodName = goodName;
        this.categoryId = categoryId;
        this.quantityLevelId = quantityLevelId;
        this.isToBuy = isToBuy;
        this.sync = sync;
    }

    public String getGoodDesc() {
        return goodDesc;
    }

    public void setGoodDesc(String goodDesc) {
        this.goodDesc = goodDesc;
    }

    public int getCrudStatus() {
        return crudStatus;
    }

    public void setCrudStatus(int crudStatus) {
        this.crudStatus = crudStatus;
    }

    public int getServerGoodId() {
        return serverGoodId;
    }

    public void setServerGoodId(int serverGoodId) {
        this.serverGoodId = serverGoodId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isToBuy() {
        return isToBuy;
    }

    public void setToBuy(boolean toBuy) {
        isToBuy = toBuy;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantityLevelId() {
        return quantityLevelId;
    }

    public void setQuantityLevelId(int quantityLevelId) {
        this.quantityLevelId = quantityLevelId;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public int getIsOrdered() {
        return isOrdered;
    }

    public void setIsOrdered(int isOrdered) {
        this.isOrdered = isOrdered;
    }

    public JSONObject toJSONObject(){

        JSONObject JSONGood = new JSONObject();

        try {
            JSONGood.put("goodId", this.goodId);
            JSONGood.put("goodName", this.goodName);
            JSONGood.put("goodDesc", this.goodDesc);
            JSONGood.put("categoryId", this.categoryId);
            JSONGood.put("quantityLevelId", this.quantityLevelId);
            int isToBuy = 0;
            if (this.isToBuy()) { isToBuy = 1;}
            JSONGood.put("isToBuy", isToBuy);
            JSONGood.put("sync", this.sync);
            JSONGood.put("email", this.email);
            JSONGood.put("crud_status", this.crudStatus);
            JSONGood.put("serverGoodId", this.serverGoodId);
            JSONGood.put("serverCategoryId", this.serverCategoryId);
            JSONGood.put("isOrdered", this.isOrdered);

            return JSONGood;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray listToJSONArray(List<Good> goods){
        JSONArray jsonGoods = new JSONArray();
        for (int i = 0; i < goods.size(); i++) {
            JSONObject JSONGood = goods.get(i).toJSONObject();
            jsonGoods.put(JSONGood);
        }
        return jsonGoods;
    }


    // for sync adapter purpose
    public static final String CONTENT_AUTHORITY = "com.winservices.wingoods";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_GOODS = "goods-path";

    public static class GoodEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GOODS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOODS;

        public static final String TABLE_NAME = "goods";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_GOOD_NAME = "good_name";
        public static final String COLUMN_QUANTITY_LEVEL = "quantity_level";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_IS_TO_BUY = "is_to_buy";
        public static final String COLUMN_SYNC_STATUS = "sync_status";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_CRUD_STATUS = "crud_status";
        public static final String COLUMN_SERVER_GOOD_ID= "server_good_id";
        public static final String COLUMN_IS_ORDERED = "is_ordered";


    }





}
