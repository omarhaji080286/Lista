package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.CategoryItem;
import com.winservices.wingoods.models.Good;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDataProvider {

    private DataBaseHelper db;
    private Context context;

    public CategoriesDataProvider(Context context) {
        this.db = new DataBaseHelper(context);
        this.context = context;
    }

    public void closeDB(){
        db.close();
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

        return mainGoodsList;

    }

    public List<CategoryGroup> getAdditionalGoodsList(int serverCategoryIdToOrder) {

        List<CategoryGroup> additionalGoodsList = new ArrayList<>();
        List<Category> categories;

        categories = this.getCategoriesWithGoodsNotOrdered(serverCategoryIdToOrder);

        for (int i = 0; i < categories.size(); i++) {

            Category category = categories.get(i);
            List<Good> goods = category.getNotOrderedGoods(context);
            CategoryGroup categoryGroup = new CategoryGroup(category.getCategoryName(), goods);
            categoryGroup.setCategoryId(category.getCategoryId());
            additionalGoodsList.add(categoryGroup);

        }

        return additionalGoodsList;

    }


    public int getGoodsToBuyNumber(int categoryId) {
        return db.getGoodsToBuyNumber(categoryId);
    }

    public int getOrderedGoodsNumber(int categoryId) {
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

            return new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
        }

    }

    Category getCategoryByServerCategoryIdAndUserId(int serverCategoryId, int userId) {
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
        return list;
    }


    List<Category> getExcludedCategoriesFromSync() {
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

        return categories;
    }


    List<Category> getUpdatedCategories() {
        //DataBaseHelper db = new DataBaseHelper(context);
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

        return categories;
    }


    public Category getCategoryByGoodId(int goodId) {
        //DataBaseHelper db = new DataBaseHelper(context);
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


        return new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);
    }


    public Category getCategoryById(int categoryId) {
        //DataBaseHelper db = new DataBaseHelper(context);
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


        return new Category(categoryId, categoryName, color, icon, sync, userId, email, crud, serverCategoryId);

    }

    /*public static String[] getCategoriesNames(Context context){


        String categoriesNames[] = new String[100];

        DataBaseHelper db = new DataBaseHelper(context);
        Cursor cursor = db.getCategoriesNames();
        int i=0;
        while (cursor.moveToNext()) {
            categoriesNames[i] = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            i++;
        }
        cursor.close();

        return categoriesNames;
    }*/



}
