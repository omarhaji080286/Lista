package com.winservices.wingoods.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DefaultCategory implements Parcelable {

    public static final String PREFIX_D_CATEGORY = "d_category_";
    public static final int GROCERIES = 1;
    public static final int HYGIENE_PRODUCTS = 6;
    public static final int COSMETICS = 8;
    public static final int HARDWARES = 10;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(dCategoryId);
        parcel.writeString(dCategoryName);
    }

    public static final Parcelable.Creator<DefaultCategory> CREATOR = new Parcelable.Creator<DefaultCategory>() {
        public DefaultCategory createFromParcel(Parcel in) {
            return new DefaultCategory(in);
        }

        public DefaultCategory[] newArray(int size) {
            return new DefaultCategory[size];
        }
    };

    public DefaultCategory(Parcel input) {
        this.dCategoryId = input.readInt();
        this.dCategoryName = input.readString();

    }

    public DefaultCategory(){ }


}
