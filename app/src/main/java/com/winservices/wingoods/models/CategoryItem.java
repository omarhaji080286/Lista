package com.winservices.wingoods.models;


import android.content.Context;

import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;

public class CategoryItem extends Category {

    private int redGoods, orangeGoods, greenGoods;
    public final static int RED = 1;
    public final static int ORANGE = 2;
    public final static int GREEN = 3;


    public CategoryItem(int categoryId, String categoryName, int redGoods, int orangeGoods, int greenGoods) {
        super(categoryId, categoryName);
        this.redGoods = redGoods;
        this.orangeGoods = orangeGoods;
        this.greenGoods = greenGoods;
    }

    public int getRedGoods() {
        return redGoods;
    }

    public void setRedGoods(int redGoods) {
        this.redGoods = redGoods;
    }

    public int getOrangeGoods() {
        return orangeGoods;
    }

    public void setOrangeGoods(int orangeGoods) {
        this.orangeGoods = orangeGoods;
    }

    public int getGreenGoods() {
        return greenGoods;
    }

    public void setGreenGoods(int greenGoods) {
        this.greenGoods = greenGoods;
    }

    public int getTotalGoodsCount(){
        return redGoods + orangeGoods + greenGoods;
    }

    public int getCategoryLevel(){
        int categoryLevel=0;
        int maxValue = Math.max(redGoods,Math.max(orangeGoods, greenGoods));
        if (redGoods==maxValue){
            categoryLevel = RED;
        } else if (orangeGoods==maxValue){
            categoryLevel = ORANGE;
        } else if (greenGoods==maxValue){
            categoryLevel = GREEN;
        }
        return categoryLevel;
    }

    public int getGoodsToBuyNumber(Context context){
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        int number = categoriesDataProvider.getGoodsToBuyNumber(this.getCategoryId());
        categoriesDataProvider.closeDB();
        return number;
    }

}
