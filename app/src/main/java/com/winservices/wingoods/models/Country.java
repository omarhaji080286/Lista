package com.winservices.wingoods.models;


import java.io.Serializable;

public class Country implements Serializable{

    private int serverCountryId;
    private String countryName;

    public Country(int serverCountryId, String countryName) {
        this.serverCountryId = serverCountryId;
        this.countryName = countryName;
    }

    public int getServerCountryId() {
        return serverCountryId;
    }

    public void setServerCountryId(int serverCountryId) {
        this.serverCountryId = serverCountryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
