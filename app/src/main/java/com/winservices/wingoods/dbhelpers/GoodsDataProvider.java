package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class GoodsDataProvider {

    private final static String TAG = "GoodsDataProvider";
    private DataBaseHelper db;

    public GoodsDataProvider(Context context) {
        this.db = new DataBaseHelper(context);
        //this.db = DataBaseHelper.getInstance(context);
        Log.d(TAG, Constants.TAG_LISTA+"DB opened");
    }

    public void closeDB(){
        Log.d(TAG, Constants.TAG_LISTA+"DB closed");
        db.close();
    }

    public String getMessageToSend() {

        StringBuilder sb = new StringBuilder();

        Cursor categories = db.getPurchasableCategories();

        while (categories.moveToNext()) {

            int categoryId = categories.getInt(categories.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = categories.getString(categories.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));

            sb.append(categoryName);
            sb.append(" :\n");
            Cursor goods = db.getGoodsByCategory(categoryId, "");

            while (goods.moveToNext()) {
                String goodName = goods.getString(goods.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
                String goodDesc = goods.getString(goods.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
                boolean isToBuy = (goods.getInt(goods.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY))==1);

                if (isToBuy) {
                    sb.append("- ");
                    sb.append(goodName);
                    sb.append("(");
                    sb.append(goodDesc);
                    sb.append(")");
                    sb.append("\n");
                }
            }
            goods.close();
        }
        categories.close();
        Log.d(TAG, Constants.TAG_LISTA+"getMessageToSend called");

        return sb.toString();
    }


    List<Good> getNotSyncGoods(Context context) {
        List<Good> list = new ArrayList<>();
        Cursor cursor = db.getNotSyncGoods();
        while (cursor.moveToNext()) {
            int goodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String goodName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
            String goodDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ID));
            int quantityLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
            boolean isToBuy = (cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY)) == 1);
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverGoodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GOOD_ID));
            int isOrdered = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_ORDERED));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);
            good.setServerCategoryId(serverCategoryId);
            good.setIsOrdered(isOrdered);

            list.add(good);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getNotSyncGoods called");

        return list;
    }

    List<Good> getExcludedGoodsFromSync() {

        List<Good> list = new ArrayList<>();
        Cursor cursor = db.getExcludedGoodsFromSync();
        while (cursor.moveToNext()) {
            int goodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String goodName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
            String goodDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ID));
            int quantityLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
            boolean isToBuy = (cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY)) == 1);
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverGoodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GOOD_ID));
            int isOrdered = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_ORDERED));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);
            good.setIsOrdered(isOrdered);

            list.add(good);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getExcludedGoodsFromSync called");

        return list;
    }


    public Good getGoodById(int goodId) {
        Cursor cursor = db.getGoodById(goodId);

        cursor.moveToNext();

        //int goodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String goodName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
        String goodDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ID));
        int quantityLevelId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
        boolean isToBuy = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY))==1;
        int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
        int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
        int serverGoodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GOOD_ID));
        int isOrdered = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_ORDERED));

        Good good = new Good( goodId,  goodName,  categoryId,  quantityLevelId,
                isToBuy,  sync, email, crud,  serverGoodId);
        good.setGoodDesc(goodDesc);
        good.setIsOrdered(isOrdered);

        Log.d(TAG, Constants.TAG_LISTA+"getGoodById called");

        return good;
    }


    Good getGoodByServerGoodIdAndUserId(int serverGoodId, int userId) {

        Cursor cursor = db.getGoodByServerGoodId(serverGoodId, userId);

        cursor.moveToNext();

        int goodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String goodName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
        String goodDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ID));
        int quantityLevelId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
        boolean isToBuy = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY))==1;
        int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
        int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
        //int serverGoodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GOOD_ID));
        int isOrdered = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_ORDERED));

        Good good = new Good( goodId,  goodName,  categoryId,  quantityLevelId,
                isToBuy,  sync, email, crud,  serverGoodId);
        good.setGoodDesc(goodDesc);
        good.setIsOrdered(isOrdered);

        Log.d(TAG, Constants.TAG_LISTA+"getGoodByServerGoodIdAndUserId called");

        return good;
    }



    List<Good> getUpdatedGoods(Context context) {

        List<Good> list = new ArrayList<>();
        Cursor cursor = db.getUpdatedGoods();
        while (cursor.moveToNext()) {
            int goodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String goodName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
            String goodDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ID));
            int quantityLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
            boolean isToBuy = (cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY)) == 1);
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverGoodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GOOD_ID));
            int isOrdered = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_ORDERED));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setServerCategoryId(serverCategoryId);
            good.setGoodDesc(goodDesc);
            good.setIsOrdered(isOrdered);

            list.add(good);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getUpdatedGoods called");

        return list;
    }

    List<Good> getGoodsByCategoryId(Context context, int categoryId, String searchGoodName) {

        List<Good> list = new ArrayList<>();
        Cursor cursor = db.getGoodsByCategory(categoryId, searchGoodName);
        while (cursor.moveToNext()) {
            int goodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String goodName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
            String goodDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
            //int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ID));
            int quantityLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
            boolean isToBuy = (cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY)) == 1);
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverGoodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GOOD_ID));
            int isOrdered = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_ORDERED));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setServerCategoryId(serverCategoryId);
            good.setGoodDesc(goodDesc);
            good.setIsOrdered(isOrdered);

            list.add(good);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getGoodsByCategoryId called");

        return list;
    }


    public List<Good> getGoodsToOrderByServerCategoryId(int serverCategoryIdToOrder) {
        List<Good> list = new ArrayList<>();
        Cursor cursor = db.getGoodsToOrderByServerCategory(serverCategoryIdToOrder);
        while (cursor.moveToNext()) {
            int goodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String goodName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
            String goodDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ID));
            int quantityLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
            boolean isToBuy = (cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY)) == 1);
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverGoodId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GOOD_ID));
            int isOrdered = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_ORDERED));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);
            good.setServerCategoryId(serverCategoryId);
            good.setIsOrdered(isOrdered);

            list.add(good);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getGoodsToOrderByServerCategoryId called");

        return list;

    }

}
