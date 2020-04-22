package com.winservices.wingoods.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DateOff {

    private int dateOffId;
    private String DateOff;
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

    public String getDateOff() {
        return DateOff;
    }

    public void setDateOff(String dateOff) {
        DateOff = dateOff;
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

    public DateOff(Parcel input) {
        this.dateOffId = input.readInt();
        this.dateOffDesc = input.readString();
        this.serverShopId = input.readInt();

    }
}
