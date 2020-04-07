package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDataProvider {

    private final static String TAG = "CategoriesDataProvider";
    private DataBaseHelper db;
    private Context context;

    public CategoriesDataProvider(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        this.context = context;
    }

    public List<CategoryGroup> getMainGoodsList(String searchGoodName) {

        List<CategoryGroup> mainGoodsList = new ArrayList<>();
        List<Category> categories;

        categories = this.getAllCategories();

        for (int i = 0; i < categories.size(); i++) {

            Category category = categories.get(i);

            List<Good> goods;

            GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
            goods = goodsDataProvider.getGoodsByCategoryId(context, category.getCategoryId(), searchGoodName);

            CategoryGroup categoryGroup = new CategoryGroup(category.getCategoryName(), goods);
            categoryGroup.setCategoryId(category.getCategoryId());
            categoryGroup.setCategory(category);

            mainGoodsList.add(categoryGroup);

        }
        Log.d(TAG, Constants.TAG_LISTA + "getMainGoodsList called");
        return mainGoodsList;

    }

    public List<CategoryGroup> getCategoriesForOrder(Shop shop) {

        List<CategoryGroup> groups = new ArrayList<>();
        List<Category> categoriesToOrder = new ArrayList<>();

        List<Category> categories = this.getCategoriesWithGoodsNotOrdered();

        List<DefaultCategory> shopDCategories = shop.getDefaultCategories();

        for (int i = 0; i < shopDCategories.size(); i++) {
            DefaultCategory defaultCategory = shopDCategories.get(i);
            for (int j = 0; j < categories.size(); j++) {
                Category category = categories.get(j);
                if (defaultCategory.getDCategoryId() == category.getDCategoryId()) {
                    categoriesToOrder.add(category);
                }
            }
        }

        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);

        for (int i = 0; i < categoriesToOrder.size(); i++) {

            Category category = categoriesToOrder.get(i);
            List<Good> notOrderedGoods = goodsDataProvider.getGoodsToOrderByServerCategoryId(category.getServerCategoryId());

            if (notOrderedGoods.size()>0){
                CategoryGroup categoryGroup = new CategoryGroup(category.getCategoryName(), notOrderedGoods);
                categoryGroup.setCategoryId(category.getCategoryId());
                categoryGroup.setServerCategoryId(category.getServerCategoryId());
                groups.add(categoryGroup);
            }

        }

        Log.d(TAG, Constants.TAG_LISTA + "getCategoriesForOrder called");

        return groups;

    }

    public int getGoodsToBuyNumber(int categoryId) {
        Log.d(TAG, Constants.TAG_LISTA + "getGoodsToBuyNumber called");
        return db.getGoodsToBuyNumber(categoryId);
    }

    public int getOrderedGoodsNumber(int categoryId) {
        Log.d(TAG, Constants.TAG_LISTA + "getOrderedGoodsNumber called");
        return db.getOrderedGoodsNumber(categoryId);
    }

    public int getGoodsNumber(int categoryId) {
        Log.d(TAG, Constants.TAG_LISTA + "getGoodsNumber called");
        return db.getGoodsNumber(categoryId);
    }


    Category getCategoryByName(String categoryName) {
        Cursor cursor = db.getCategoryByName(categoryName);

        if (cursor.getCount() == 0) {
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
            int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

            Log.d(TAG, Constants.TAG_LISTA + "getCategoryByName called");
            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
            category.setDCategoryID(dCategoryId);
            return category;
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
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

        Log.d(TAG, Constants.TAG_LISTA + "getCategoryByServerCategoryIdAndUserId called");
        Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        category.setDCategoryID(dCategoryId);
        return category;
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
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

        Log.d(TAG, Constants.TAG_LISTA + "getCategoryByCrud called");
        Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        category.setDCategoryID(dCategoryId);
        return category;
    }


    public List<Category> getNotSyncCategories() {
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
            int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email);
            category.setDCategoryID(dCategoryId);
            list.add(category);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA + "getNotSyncCategories called");
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
            int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
            category.setDCategoryID(dCategoryId);
            categories.add(category);

        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA + "getExcludedCategoriesFromSync called");
        return categories;
    }

    private List<Category> getCategoriesWithGoodsNotOrdered() {
        List<Category> categories = new ArrayList<>();
        Cursor cursor = db.getCategoriesWithGoodsNotOrdered();

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
            int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
            category.setDCategoryID(dCategoryId);
            categories.add(category);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA + "getCategoriesWithGoodsNotOrdered called");
        return categories;
    }

    public List<Category> getAllCategories() {
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
            int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

            int goodsToBuyNumber = getGoodsToBuyNumber(categoryId);
            int orderedGoodsNumber = getOrderedGoodsNumber(categoryId);
            int goodsNumber = getGoodsNumber(categoryId);

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
            category.setGoodsToBuyNumber(goodsToBuyNumber);
            category.setOrderedGoodsNumber(orderedGoodsNumber);
            category.setGoodsNumber(goodsNumber);
            category.setDCategoryID(dCategoryId);

            categories.add(category);

        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA + "getAllCategories called");
        return categories;
    }


    public List<Category> getUpdatedCategories() {
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
            int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

            Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
            category.setDCategoryID(dCategoryId);

            categories.add(category);

        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA + "getUpdatedCategories called");

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
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

        Log.d(TAG, Constants.TAG_LISTA + "getCategoryByGoodId called");
        Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        category.setDCategoryID(dCategoryId);
        return category;
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
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));
        int goodsToBuyNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.GOODS_TO_BUY_NUMBER));
        int orderedGoodsNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.ORDERED_GOODS_NUMBER));
        int goodsNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.GOODS_NUMBER));

        Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        category.setGoodsToBuyNumber(goodsToBuyNumber);
        category.setOrderedGoodsNumber(orderedGoodsNumber);
        category.setGoodsNumber(goodsNumber);
        category.setDCategoryID(dCategoryId);

        Log.d(TAG, Constants.TAG_LISTA + "getCategoryById called");
        return category;

    }


    public Category getCategoryByServerCategoryId(int serverCategoryId) {
        Cursor cursor = db.getCategoryByServerCategoryId(serverCategoryId);
        cursor.moveToNext();
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_COLOR));
        int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
        int sync = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
        int crud = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CRUD_STATUS));
        //int serverCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CATEGORY_ID));
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

        Category category = new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        category.setDCategoryID(dCategoryId);

        Log.d(TAG, Constants.TAG_LISTA + "getCategoryByServerCategoryId called");
        return category;

    }
}
