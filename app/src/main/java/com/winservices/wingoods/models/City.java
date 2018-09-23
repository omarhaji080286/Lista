package com.winservices.wingoods.models;

import java.io.Serializable;

public class City implements Serializable{

    private int serverCityId;
    private String cityName;
    private Country country;

    public City(int serverCityId, String cityName, Country country) {
        this.serverCityId = serverCityId;
        this.cityName = cityName;
        this.country = country;
    }

    public int getServerCityId() {
        return serverCityId;
    }

    public void setServerCityId(int serverCityId) {
        this.serverCityId = serverCityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
