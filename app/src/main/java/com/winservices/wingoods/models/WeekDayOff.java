package com.winservices.wingoods.models;

import android.os.Parcel;
import android.os.Parcelable;

public class WeekDayOff implements Parcelable {

    private int dayOffId;
    private int dayOff;
    private int serverShopId;

    public WeekDayOff() {
    }

    public int getDayOffId() {
        return dayOffId;
    }

    public void setDayOffId(int dayOffId) {
        this.dayOffId = dayOffId;
    }

    public int getDayOff() {
        return dayOff;
    }

    public void setDayOff(int dayOff) {
        this.dayOff = dayOff;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }


    public static final Parcelable.Creator<WeekDayOff> CREATOR = new Parcelable.Creator<WeekDayOff>() {
        public WeekDayOff createFromParcel(Parcel in) {
            return new WeekDayOff(in);
        }

        public WeekDayOff[] newArray(int size) {
            return new WeekDayOff[size];
        }
    };

    private WeekDayOff(Parcel input) {
        this.dayOff = input.readInt();
        this.dayOffId = input.readInt();
        //this.serverShopId = input.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dayOff);
        dest.writeInt(dayOffId);
        //dest.writeInt(serverShopId);
    }
}
