package com.winservices.wingoods.models;

public class Description {

    public static final int ALL = -1;

    private int descId;
    private String descValue;
    private int dCategoryId;

    public Description(int descId, String descValue, int dCategoryId) {
        this.descId = descId;
        this.descValue = descValue;
        this.dCategoryId = dCategoryId;
    }

    public int getDescId() {
        return descId;
    }

    public void setDescId(int descId) {
        this.descId = descId;
    }

    public String getDescValue() {
        return descValue;
    }

    public void setDescValue(String descValue) {
        this.descValue = descValue;
    }

    public int getdCategoryId() {
        return dCategoryId;
    }

    public void setdCategoryId(int dCategoryId) {
        this.dCategoryId = dCategoryId;
    }
}
