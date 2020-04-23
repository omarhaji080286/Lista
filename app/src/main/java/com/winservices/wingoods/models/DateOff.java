package com.winservices.wingoods.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DateOff implements  Parcelable{

    private int dateOffId;
    private String dateOffValue;
    private String dateOffDesc;
    private int serverShopId;

    public DateOff() {
    }

    public int getDateOffId() {
        return dateOffId;
    }

    public void setDateOffId(int dateOffId) {
        this.dateOffId = dateOffId;
    }

    public String getDateOffValue() {
        return dateOffValue;
    }

    public void setDateOffValue(String dateOffValue) {
        this.dateOffValue = dateOffValue;
    }

    public String getDateOffDesc() {
        return dateOffDesc;
    }

    public void setDateOffDesc(String dateOffDesc) {
        this.dateOffDesc = dateOffDesc;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public static final Parcelable.Creator<DateOff> CREATOR = new Parcelable.Creator<DateOff>() {
        public DateOff createFromParcel(Parcel in) {
            return new DateOff(in);
        }

        public DateOff[] newArray(int size) {
            return new DateOff[size];
        }
    };

    private DateOff(Parcel input) {
        this.dateOffId = input.readInt();
        this.dateOffValue = input.readString();
        this.dateOffDesc = input.readString();
        //this.serverShopId = input.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dateOffId);
        dest.writeString(dateOffValue);
        dest.writeString(dateOffDesc);
        //dest.writeInt(serverShopId);

    }
}
