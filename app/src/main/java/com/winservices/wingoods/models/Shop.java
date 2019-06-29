package com.winservices.wingoods.models;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.DrawFilter;
import android.os.Parcel;
import android.os.Parcelable;

import com.winservices.wingoods.R;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.ArrayList;
import java.util.List;

public class Shop implements Parcelable {

    public static final String DEFAULT_IMAGE = "defaultImage";
    public static final String PREFIX_SHOP = "shop_";
    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
    private int serverShopId;
    private String shopName;
    private String shopAdress;
    private String shopEmail;
    private String shopPhone;
    private double longitude;
    private double latitude;
    private ShopType shopType;
    private City city;
    private Country country;
    private String markerId;
    private List<DefaultCategory> defaultCategories;
    private String openingTime;
    private String closingTime;
    private int visibility;

    public Shop(Parcel input) {
        this.serverShopId = input.readInt();
        this.shopName = input.readString();
        this.shopAdress = input.readString();
        this.shopEmail = input.readString();
        this.shopPhone = input.readString();
        this.longitude = input.readDouble();
        this.latitude = input.readDouble();
        this.shopType = (ShopType) input.readSerializable();
        this.city = (City) input.readSerializable();
        this.country = (Country) input.readSerializable();
        this.visibility = input.readInt();
        this.defaultCategories = new ArrayList<>();
        input.readTypedList(defaultCategories, DefaultCategory.CREATOR);

    }

    public Shop() {
    }

    public static Bitmap getShopImage(Context context, int serverShopId) {
        String imagePath = SharedPrefManager.getInstance(context).getImagePath(Shop.PREFIX_SHOP + serverShopId);
        if (imagePath!=null) {
            return UtilsFunctions.getOrientedBitmap(imagePath);
        }
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_shop_image);
    }

    public List<DefaultCategory> getDefaultCategories() {
        return defaultCategories;
    }

    public void setDefaultCategories(List<DefaultCategory> defaultCategories) {
        this.defaultCategories = defaultCategories;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAdress() {
        return shopAdress;
    }

    public void setShopAdress(String shopAdress) {
        this.shopAdress = shopAdress;
    }

    public String getShopEmail() {
        return shopEmail;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(serverShopId);
        parcel.writeString(shopName);
        parcel.writeString(shopAdress);
        parcel.writeString(shopEmail);
        parcel.writeString(shopPhone);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeSerializable(shopType);
        parcel.writeSerializable(city);
        parcel.writeSerializable(country);
        parcel.writeInt(visibility);
        parcel.writeTypedList(defaultCategories);


    }


}
