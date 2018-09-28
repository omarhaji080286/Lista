package com.winservices.wingoods.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopsFilter implements Serializable {

    private ArrayList<City> selectedCities;

    public ShopsFilter() { }

    public ArrayList<City> getSelectedCities() {
        return selectedCities;
    }

    public void setSelectedCities(ArrayList<City> selectedCities) {
        this.selectedCities = selectedCities;
    }

    public void reset(){
        this.selectedCities = new ArrayList<>();
    }
}
