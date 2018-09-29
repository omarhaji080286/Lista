package com.winservices.wingoods.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopsFilter implements Serializable {

    public final static boolean ENABLE = true;
    public final static boolean DISABLE = false;
    private boolean filterState = DISABLE;
    private ArrayList<City> selectedCities;

    public ShopsFilter() {
        this.filterState = DISABLE;
        this.selectedCities = new ArrayList<>();
    }

    public ArrayList<City> getSelectedCities() {
        return selectedCities;
    }

    public void setSelectedCities(ArrayList<City> selectedCities) {
        this.selectedCities = selectedCities;
    }

    public boolean isEnable() {
        return filterState;
    }

    public void setFilterState(boolean filterState) {
        this.filterState = filterState;
    }

    public void reset(){
        this.filterState = DISABLE;
        this.selectedCities = new ArrayList<>();
    }
}
