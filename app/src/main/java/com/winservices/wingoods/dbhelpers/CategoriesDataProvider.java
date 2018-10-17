package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.CategoryItem;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDataProvider {

    private DataBaseHelper db;
    private Context context;
    private final static String TAG = "CategoriesDataProvider";

    public CategoriesDataProvider(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        this.context = context;
    }

    public List<CategoryGroup> getMainGoodsList(String searchGoodName) {

        List<CategoryGroup> mainGoodsList = new ArrayList<>();
        List<Category> categories;

        categories = this.getAllcategories();

        for (int i = 0; i < categories.size(); i++) {

            Category category = categories.get(i);

            List<Good> goods;

            GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
            goods = goodsDataProvider.getGoodsByCategoryId(context, category.getCategoryId(), searchGoodName);

            CategoryGroup categoryGroup = new CategoryGroup(category.getCategoryName(), goods);
            categoryGroup.setCategoryId(category.getCategoryId());

            mainGoodsList.add(categoryGroup);

        }
        Log.d(TAG, Constants.TAG_LISTA+"getMainGoodsList called");
        return mainGoodsList;

    }

    public List<CategoryGroup> getAdditionalGoodsList(int serverCategoryIdToOrder) {

        List<CategoryGroup> additionalGoodsList = new ArrayList<>();
        List<Category> categories;

        categories = this.getCategoriesWithGoodsNotOrdered(serverCategoryIdToOrder);

        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);



        for (int i = 0; i < categories.size(); i++) {

            Category category = categories.get(i);
            List<Good> notOrderedGoods = goodsDataProvider.getGoodsToOrderByServerCategoryId(category.getServerCategoryId());
            CategoryGroup categoryGroup = new CategoryGroup(category.getCategoryName(), notOrderedGoods);
            categoryGroup.setCategoryId(category.getCategoryId());
            categoryGroup.setServerCategoryId(category.getServerCategoryId());
            additionalGoodsList.add(categoryGroup);

        }

        Log.d(TAG, Constants.TAG_LISTA+"getAdditionalGoodsList called");

        return additionalGoodsList;

    }


    public int getGoodsToBuyNumber(int categoryId) {
        Log.d(TAG, Constants.TAG_LISTA+"getGoodsToBuyNumber called");
        return db.getGoodsToBuyNumber(categoryId);
    }

    public int getOrderedGoodsNumber(int categoryId) {
        Log.d(TAG, Constants.TAG_LISTA+"getOrderedGoodsNumber called");
        return db.getOrderedGoodsNumber(categoryId);
    }


    Category getCategoryByName(String categoryName) {
        Cursor cursor = db.getCategoryByName(categoryName);

        if (cursor.getCount()==0) {
            return null;
        } else {
            cursor.moveToNext();
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            //String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Log.d(TAG, Constants.TAG_LISTA+"getCategoryByName called");
            return new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        }

    }

    public Category getCategoryByServerCategoryIdAndUserId(int serverCategoryId, int userId) {
        Cursor cursor = db.getCategoryByServerCategoryIdAndUserId(serverCategoryId, userId);
        cursor.moveToNext();
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
        int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
        int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        //int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
        int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));

        Log.d(TAG, Constants.TAG_LISTA+"getCategoryByServerCategoryIdAndUserId called");
        return new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
    }

    public Category getCategoryByCrud(int crudStatus) {
        Cursor cursor = db.getCategoryByCrud(crudStatus);
        cursor.moveToNext();
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
        int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
        int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
        int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
        int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

        Log.d(TAG, Constants.TAG_LISTA+"getCategoryByCrud called");
        return new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
    }


    List<Category> getNotSyncCategories() {
        List<Category> list = new ArrayList<>();
        Cursor cursor = db.getNotSyncCategories();
        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email);
            list.add(category);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getNotSyncCategories called");
        return list;
    }


    public List<Category> getExcludedCategoriesFromSync() {
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.getExcludedCategoriesFromSync();

        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);

            categories.add(category);

        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getExcludedCategoriesFromSync called");
        return categories;
    }

    private List<Category> getCategoriesWithGoodsNotOrdered(int serverCategoryIdToOrder){
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.getCategoriesWithGoodsNotOrdered(serverCategoryIdToOrder);

        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
            categories.add(category);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getCategoriesWithGoodsNotOrdered called");
        return categories;
    }

    public List<Category> getAllcategories() {
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.getAllCategories();

        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);

            categories.add(category);

        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getAllcategories called");
        return categories;
    }


    List<Category> getUpdatedCategories() {
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.getUpdatedCategories();

        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
            int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
            int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);

            categories.add(category);

        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getUpdatedCategories called");

        return categories;
    }


    public Category getCategoryByGoodId(int goodId) {
        Cursor cursor = db.getCategoryByGoodId(goodId);
        cursor.moveToNext();
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
        int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
        int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
        int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
        int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));

        Log.d(TAG, Constants.TAG_LISTA+"getCategoryByGoodId called");

        return new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
    }


    public Category getCategoryById(int categoryId) {
        Cursor cursor = db.getCategoryById(categoryId);
        cursor.moveToNext();
        //int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
        int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
        int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
        int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
        int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));
        int goodsToBuyNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.GOODS_TO_BUY_NUMBER));
        int orderedGoodsNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.ORDERED_GOODS_NUMBER));
        int goodsNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.GOODS_NUMBER));

        Category category =  new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        category.setGoodsToBuyNumber(goodsToBuyNumber);
        category.setOrderedGoodsNumber(orderedGoodsNumber);
        category.setGoodsNumber(goodsNumber);

        Log.d(TAG, Constants.TAG_LISTA+"getCategoryById called");
        return category;

    }



}
