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
        this.db = DataBaseHelper.getInstance(context);
    }


    public List<Good> getNotSyncGoods(Context context) {
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
            int usesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USES_NUMBER));
            int priceId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_PRICE_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);
            good.setServerCategoryId(serverCategoryId);
            good.setIsOrdered(isOrdered);
            good.setUsesNumber(usesNumber);
            good.setPriceId(priceId);

            list.add(good);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getNotSyncGoods called");

        return list;
    }

    public List<Good> getExcludedGoodsFromSync() {

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
            int usesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USES_NUMBER));
            int priceId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_PRICE_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);
            good.setIsOrdered(isOrdered);
            good.setUsesNumber(usesNumber);
            good.setPriceId(priceId);

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
        int usesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USES_NUMBER));
        int priceId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_PRICE_ID));

        Good good = new Good( goodId,  goodName,  categoryId,  quantityLevelId,
                isToBuy,  sync, email, crud,  serverGoodId);
        good.setGoodDesc(goodDesc);
        good.setIsOrdered(isOrdered);
        good.setUsesNumber(usesNumber);
        good.setPriceId(priceId);

        Log.d(TAG, Constants.TAG_LISTA+"getGoodById called");

        return good;
    }


    public Good getGoodByServerGoodId(int serverGoodId) {

        Cursor cursor = db.getGoodByServerGoodId(serverGoodId);

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
        int usesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USES_NUMBER));
        int priceId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_PRICE_ID));

        Good good = new Good( goodId,  goodName,  categoryId,  quantityLevelId,
                isToBuy,  sync, email, crud,  serverGoodId);
        good.setGoodDesc(goodDesc);
        good.setIsOrdered(isOrdered);
        good.setUsesNumber(usesNumber);
        good.setPriceId(priceId);

        Log.d(TAG, Constants.TAG_LISTA+"getGoodByServerGoodId called");

        return good;
    }



    public List<Good> getUpdatedGoods(Context context) {

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
            int usesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USES_NUMBER));
            int priceId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_PRICE_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setServerCategoryId(serverCategoryId);
            good.setGoodDesc(goodDesc);
            good.setIsOrdered(isOrdered);
            good.setUsesNumber(usesNumber);
            good.setPriceId(priceId);

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
            int usesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USES_NUMBER));
            int priceId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_PRICE_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setServerCategoryId(serverCategoryId);
            good.setGoodDesc(goodDesc);
            good.setIsOrdered(isOrdered);
            good.setUsesNumber(usesNumber);
            good.setPriceId(priceId);

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
            int usesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USES_NUMBER));
            int priceId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_PRICE_ID));

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);
            good.setServerCategoryId(serverCategoryId);
            good.setIsOrdered(isOrdered);
            good.setUsesNumber(usesNumber);
            good.setPriceId(priceId);

            list.add(good);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getGoodsToOrderByServerCategoryId called");

        return list;

    }

    public int getGoodsToBuyNb(){
        return db.getGoodsToBuyNb();
    }

}
