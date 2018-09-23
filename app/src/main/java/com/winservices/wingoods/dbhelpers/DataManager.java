package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.provider.ContactsContract;

import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;

public class DataManager {

    private DataBaseHelper db;

    public DataManager(Context context) {
        this.db = new DataBaseHelper(context);;
    }

    public void closeDB(){
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

        return result;
    }

    public int restoreGood(Good good) {
        int result;
        if (db.restoreGood(good)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
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
        return result;
    }

    public int updateCategory(Category category) {
        int result;
        if (db.updateCategory(category)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        return result;
    }


    //DELETE

    //GOODS

    public boolean deleteGoodById(int goodId) {
        return db.deleteGoodById(goodId);
    }

    //CATEGORIES

    public boolean deleteCategory(int categoryId) {
        return db.deleteCategory(categoryId);
    }

    public boolean deleteAllUserCategoriesAndGoods() {
        return db.deleteAllUserCategoriesAndGoods();
    }


}
