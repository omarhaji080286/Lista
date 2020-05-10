package com.winservices.wingoods.models;

import java.io.Serializable;

public class City implements Serializable{

    private int serverCityId;
    private String cityName;
    private Country country;
    private String longitude;
    private String latitude;

    public City(int serverCityId, String cityName, Country country) {
        this.serverCityId = serverCityId;
        this.cityName = cityName;
        this.country = country;
    }

    public City() {
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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
