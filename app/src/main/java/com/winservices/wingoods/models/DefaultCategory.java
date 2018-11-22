package com.winservices.wingoods.models;

public class DefaultCategory {

    public static final String PREFIX_D_CATEGORY = "d_category_";

    private int dCategoryId;
    private String dCategoryName;


    public DefaultCategory(int dCategoryId, String dCategoryName) {
        this.dCategoryId = dCategoryId;
        this.dCategoryName = dCategoryName;
    }

    public int getDCategoryId() {
        return dCategoryId;
    }

    public void setDCategoryId(int dCategoryId) {
        this.dCategoryId = dCategoryId;
    }

    public String getDCategoryName() {
        return dCategoryName;
    }

    public void setDCategoryName(String dCategoryName) {
        this.dCategoryName = dCategoryName;
    }
}
