package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;

public class DataManager {

    private DataBaseHelper db;
    private final static String TAG = "DataManager";

    public DataManager(Context context) {
        this.db = new DataBaseHelper(context);
        //this.db = DataBaseHelper.getInstance(context);
        Log.d(TAG, Constants.TAG_LISTA+"DB opened");

    }

    public void closeDB(){
        Log.d(TAG, Constants.TAG_LISTA+"DB closed");
        db.close();
    }

    //CREATE

    // CATEGORIES

    public int addCategory(Context context, Category category) {
        int result;

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        if (categoriesDataProvider.getCategoryByName(category.getCategoryName())==null) {
            if (db.addCategory(category)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        } else {
            result = Constants.DATAEXISTS;
        }
        categoriesDataProvider.closeDB();
        Log.d(TAG, Constants.TAG_LISTA+"addCategory called");

        return result;
    }


    // GOODS

    // INSERT

    public int addGood(Good good) {
        int result;
        if (db.addGood(good)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        Log.d(TAG, Constants.TAG_LISTA+"addGood called");

        return result;

    }

    public int restoreGood(Good good) {
        int result;
        if (db.restoreGood(good)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        Log.d(TAG, Constants.TAG_LISTA+"restoreGood called");

        return result;
    }


    //UPDATE

    //GOODS


    public int updateGood(Good good) {
        int result;

        if (db.updateGood(good)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        Log.d(TAG, Constants.TAG_LISTA+"updateGood called");

        return result;
    }




    //UPDATE

    //CATEGORIES

    public  int updateCategoryName(Category category) {
        int result;
            if (db.updateCategoryName(category)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        Log.d(TAG, Constants.TAG_LISTA+"updateCategoryName called");

        return result;
    }

    public int updateCategory(Category category) {
        int result;
        if (db.updateCategory(category)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        Log.d(TAG, Constants.TAG_LISTA+"updateCategory called");

        return result;
    }


    //DELETE

    //GOODS

    public boolean deleteGoodById(int goodId) {
        Log.d(TAG, Constants.TAG_LISTA+"deleteGoodById called");
        return db.deleteGoodById(goodId);
    }

    //CATEGORIES

    public boolean deleteCategory(int categoryId) {
        Log.d(TAG, Constants.TAG_LISTA+"deleteCategory called");
        return db.deleteCategory(categoryId);
    }

    public boolean deleteAllUserCategoriesAndGoods() {
        Log.d(TAG, Constants.TAG_LISTA+"deleteAllUserCategoriesAndGoods called");
        return db.deleteAllUserCategoriesAndGoods();
    }


}
