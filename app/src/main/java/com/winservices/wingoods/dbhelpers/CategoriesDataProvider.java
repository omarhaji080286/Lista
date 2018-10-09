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

    /*public List<CategoryGroup> getFilteredCategoriesGroups(MyGoodsFilter filter) {

        List<CategoryGroup> categoryGroupList = new ArrayList<>();
        Cursor categories = null;

        filter.setGoodName(filter.getGoodName().replaceAll("'", "''"));

        switch (filter.getFilterType()) {
            case MyGoodsFilter.NO_FILTER:
                categories = db.getCategoriesByGood(filter.getGoodName());
                break;
            case MyGoodsFilter.FILTER_BY_CATEGORYID:
                categories = db.getCategoryById(filter.getCategoryId());
                break;
        }

        if (categories != null) {
            while (categories.moveToNext()) {

                Cursor goods = null;
                int categoryId = categories.getInt(categories.getColumnIndex(DataBaseHelper._ID));

                switch (filter.getFilterType()) {
                    case MyGoodsFilter.NO_FILTER:
                        goods = db.getGoodsByCategory(categoryId, filter.getGoodName());
                        break;
                    case MyGoodsFilter.FILTER_BY_CATEGORYID:
                        goods = db.getGoodsByCategoryAndName(categoryId, filter.getGoodName());
                        break;
                }

                List<GoodItem> goodItemList = new ArrayList<>();

                if (goods != null) {
                    while (goods.moveToNext()) {

                        int goodId = goods.getInt(goods.getColumnIndexOrThrow(DataBaseHelper._ID));
                        String goodName = goods.getString(goods.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_NAME));
                        String goodDesc = goods.getString(goods.getColumnIndexOrThrow(DataBaseHelper.COL_GOOD_DESC));
                        int quantityLevelId = goods.getInt(goods.getColumnIndexOrThrow(DataBaseHelper.COL_QUANTITY_LEVEL));
                        boolean isToBuy = (goods.getInt(goods.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_BUY)) == 1);

                        GoodItem goodItem = new GoodItem(goodId, goodName, categoryId, quantityLevelId, isToBuy);
                        goodItem.setGoodDesc(goodDesc);
                        goodItemList.add(goodItem);

                    }
                    goods.close();
                }

                String categoryName = categories.getString(categories.getColumnIndex(DataBaseHelper.COL_CATEGORY_NAME));
                int catId = categories.getInt(categories.getColumnIndex(DataBaseHelper.COL_CATEGORY_ID));

                CategoryGroup categoryGroup = new CategoryGroup(categoryName, goodItemList);
                categoryGroup.setCategoryId(catId);

                categoryGroupList.add(categoryGroup);
            }
            categories.close();
        }
        return categoryGroupList;

    }*/

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



    public List<Category> getCategoriesByName(String name) {
        //DataBaseHelper db = new DataBaseHelper(context);
        List<Category> list = new ArrayList<>();
        Cursor cursor = db.getCategoriesByName(name);
        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            Category category = new Category(categoryId, categoryName);
            list.add(category);
        }
        cursor.close();
        return list;
    }


    public List<CategoryItem> getCategoriesOverview() {
        //DataBaseHelper db = new DataBaseHelper(context);
        List<CategoryItem> list = new ArrayList<>();
        Cursor cursor = db.getAllCategoriesOverview();
        while (cursor.moveToNext()) {
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_NAME));
            int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CATEGORY_ICON));
            int redGoods = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.RED_GOODS));
            int orangeGoods = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.ORANGE_GOODS));
            int greenGoods = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.GREEN_GOODS));
            CategoryItem category = new CategoryItem(categoryId, categoryName, redGoods, orangeGoods, greenGoods);
            category.setIcon(icon);
            list.add(category);
        }
        cursor.close();
        return list;
    }

    public int getGoodsToBuyNumber(int categoryId) {
        return db.getGoodsToBuyNumber(categoryId);
    }

    public int getOrderedGoodsNumber(int categoryId) {
        return db.getOrderedGoodsNumber(categoryId);
    }




    public Category getCategoryByName(String categoryName) {
        //DataBaseHelper db = new DataBaseHelper(context);
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
        //DataBaseHelper db = new DataBaseHelper(context);
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
        //DataBaseHelper db = new DataBaseHelper(context);
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
        //DataBaseHelper db = new DataBaseHelper(context);
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
        //DataBaseHelper db = new DataBaseHelper(context);
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

    public List<Category> getAllcategories() {
        //DataBaseHelper db = new DataBaseHelper(context);
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
