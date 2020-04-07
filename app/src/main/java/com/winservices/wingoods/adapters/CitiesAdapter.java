package com.winservices.wingoods.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.viewholders.CityInFilterShopsViewHolder;

import java.util.ArrayList;


public class CitiesAdapter extends RecyclerView.Adapter<CityInFilterShopsViewHolder> {

    private static final String TAG = "CitiesAdapter";
    private ArrayList<City> cities;
    private Context context;
    private ArrayList<City> selectedCities;
    private SparseBooleanArray cityStateArray;

    public CitiesAdapter(ArrayList<City> cities, Context context) {
        this.cities = cities;
        this.context = context;
        this.cityStateArray = new SparseBooleanArray();
        for (int i = 0; i < cities.size(); i++) {
            cityStateArray.put(cities.get(i).getServerCityId(),true);
        }
        this.selectedCities = new ArrayList<>();
        this.selectedCities.addAll(cities);
    }

    @Override
    public CityInFilterShopsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city_in_filter_shops, parent, false);
        return new CityInFilterShopsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CityInFilterShopsViewHolder holder, final int position) {

        final City city = cities.get(position);

        holder.cityName.setText(cities.get(position).getCityName());

        if (cityStateArray.get(city.getServerCityId(), false)) {
            holder.checkBoxCity.setChecked(true);
        } else {
            holder.checkBoxCity.setChecked(false);
        }

        holder.checkBoxCity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (holder.checkBoxCity.isPressed()) {
                    if (cityStateArray.get(city.getServerCityId(), false)) {
                        removeFromSelectedCities(city);
                        holder.checkBoxCity.setChecked(false);
                        cityStateArray.put(city.getServerCityId(), false);
                    } else {
                        addToSelectedCities(city);
                        holder.checkBoxCity.setChecked(true);
                        cityStateArray.put(city.getServerCityId(), true);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    private void removeFromSelectedCities(City unSelectedCity){
        for (int i = 0; i < selectedCities.size(); i++) {
            if (selectedCities.get(i).getServerCityId()==unSelectedCity.getServerCityId()){
                selectedCities.remove(i);
            }
        }
    }

    private void addToSelectedCities(City selectedCity){
        boolean cityalreadySelected = false;
        for (int i = 0; i < selectedCities.size(); i++) {
            if (selectedCities.get(i).getServerCityId()==selectedCity.getServerCityId()){
                cityalreadySelected = true;
            }
        }
        if (!cityalreadySelected) {
            selectedCities.add(selectedCity);
        }
    }

    public ArrayList<City> getSelectedCities() {
        return selectedCities;
    }

    public void setSelectedCities(ArrayList<City> selectedCities) {
        this.selectedCities = selectedCities;
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            boolean selected = false;
            for (int j = 0; j < selectedCities.size(); j++) {
                City selectedCity = selectedCities.get(j);
                if (selectedCity.getServerCityId() == city.getServerCityId()) {
                    cityStateArray.put(cities.get(i).getServerCityId(), true);
                    selected = true;
                }
            }
            if (!selected) {
                cityStateArray.put(cities.get(i).getServerCityId(), false);
            }
        }
        notifyDataSetChanged();
    }
}
