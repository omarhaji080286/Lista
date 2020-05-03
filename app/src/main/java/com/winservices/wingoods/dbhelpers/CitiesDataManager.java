package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.Amount;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;

import java.util.ArrayList;
import java.util.List;

public class CitiesDataManager {

    private final static String TAG = AmountsDataManager.class.getSimpleName();
    private DataBaseHelper db;
    private Context context;

    CitiesDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        this.context = context;
    }

    void insertCity(City city) {
        db.insertCity(city);
    }

    public List<City> getCities() {
        Cursor cursor = db.getCities();
        List<City> cities = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int serverCityId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                String cityName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CITY_NAME));
                int serverCountryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_COUNTRY_ID));
                String longitude = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LONGITUDE));
                String latitude = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LATITUDE));

                City city = new City();
                Country country = new Country();

                country.setServerCountryId(serverCountryId);
                city.setCountry(country);
                city.setServerCityId(serverCityId);
                city.setCityName(cityName);
                city.setLatitude(latitude);
                city.setLongitude(longitude);

                cities.add(city);
            }
            cursor.close();
        }
        return cities;
    }

}
