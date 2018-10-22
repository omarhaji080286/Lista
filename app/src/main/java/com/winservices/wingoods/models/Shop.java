package com.winservices.wingoods.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Shop implements Parcelable {

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

    }

    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    public Shop(Parcel input) {
        this.serverShopId = input.readInt();
        this.shopName = input.readString();
        this.shopAdress = input.readString();
        this.shopEmail = input.readString();
        this.shopPhone = input.readString();
        this.longitude = input.readDouble();
        this.latitude = input.readDouble();
        this.shopType = (ShopType)input.readSerializable();
        this.city = (City) input.readSerializable();
        this.country = (Country) input.readSerializable();
    }

    public Shop(){ }


}
