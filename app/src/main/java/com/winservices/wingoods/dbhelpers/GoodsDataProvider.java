package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.Good;

import java.util.ArrayList;
import java.util.List;

public class GoodsDataProvider {

    private DataBaseHelper db;

    public GoodsDataProvider(Context context) {
        this.db = new DataBaseHelper(context);
    }

    public void closeDB(){
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
        return sb.toString();
    }


    List<Good> getNotSyncGoods(Context context) {
        //DataBaseHelper db = new DataBaseHelper(context);
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

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);
            good.setServerCategoryId(context);

            list.add(good);
        }
        cursor.close();
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

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);

            list.add(good);
        }
        cursor.close();
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

        Good good = new Good( goodId,  goodName,  categoryId,  quantityLevelId,
                isToBuy,  sync, email, crud,  serverGoodId);
        good.setGoodDesc(goodDesc);

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

        Good good = new Good( goodId,  goodName,  categoryId,  quantityLevelId,
                isToBuy,  sync, email, crud,  serverGoodId);
        good.setGoodDesc(goodDesc);

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

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setServerCategoryId(context);
            good.setGoodDesc(goodDesc);

            list.add(good);
        }
        cursor.close();
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

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setServerCategoryId(context);
            good.setGoodDesc(goodDesc);

            list.add(good);
        }
        cursor.close();
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

            Good good = new Good(goodId, goodName, categoryId, quantityLevel, isToBuy, sync, crud, email);
            good.setServerGoodId(serverGoodId);
            good.setGoodDesc(goodDesc);

            list.add(good);
        }
        cursor.close();
        return list;

    }
}
