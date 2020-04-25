package com.winservices.wingoods.models;

public class UserLocation {

    private int userLocationId;
    private String userAddress;
    private String userGpsLocation;
    private String serverUserId;

    public UserLocation() {
    }

    public int getUserLocationId() {
        return userLocationId;
    }

    public void setUserLocationId(int userLocationId) {
        this.userLocationId = userLocationId;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserGpsLocation() {
        return userGpsLocation;
    }

    public void setUserGpsLocation(String userGpsLocation) {
        this.userGpsLocation = userGpsLocation;
    }

    public String getServerUserId() {
        return serverUserId;
    }

    public void setServerUserId(String serverUserId) {
        this.serverUserId = serverUserId;
    }
}
