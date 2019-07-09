package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;
import com.winservices.wingoods.models.Shop;

import java.util.ArrayList;
import java.util.List;

public class ShopsDataManager {

    //private final static String TAG = ShopsDataManager.class.getSimpleName();
    private DataBaseHelper db;
    //private Context context;

    public ShopsDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        //this.context = context;
    }

    void insertShop(Shop shop) {
        db.insertShop(shop);
    }

    public Shop getShopById(int serverShopId) {

        Cursor cursor = db.getShopById(serverShopId);
        cursor.moveToNext();
        //int serverShopId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String shopName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_NAME));
        String shopPhone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_PHONE));
        String openingTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_OPENING_TIME));
        String closingTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CLOSING_TIME));
        int visibility = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_VISIBILITY));
        int serverCountryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_COUNTRY_ID));
        String countryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_COUNTRY_NAME));
        int serverCityId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CITY_ID));
        String cityName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CITY_NAME));
        String shopAdress = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_ADRESS));
        String shopEmail = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_EMAIL));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LONGITUDE));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LATITUDE));

        Shop shop = new Shop();
        shop.setServerShopId(serverShopId);
        shop.setShopName(shopName);
        shop.setShopPhone(shopPhone);
        shop.setOpeningTime(openingTime);
        shop.setClosingTime(closingTime);
        shop.setVisibility(visibility);
        shop.setShopAdress(shopAdress);
        shop.setShopEmail(shopEmail);
        shop.setLongitude(longitude);
        shop.setLatitude(latitude);

        Country country = new Country(serverCountryId, countryName);
        shop.setCountry(country);

        City city = new City(serverCityId, cityName, country);
        shop.setCity(city);

        return shop;

    }

    List<Shop> getAllShops(){
        Cursor cursor = db.getAllShops();
        List<Shop> shops = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int serverShopId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                String shopName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_NAME));
                String shopPhone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_PHONE));
                String openingTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_OPENING_TIME));
                String closingTime = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CLOSING_TIME));
                int visibility = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_VISIBILITY));
                int serverCountryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_COUNTRY_ID));
                String countryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_COUNTRY_NAME));
                int serverCityId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CITY_ID));
                String cityName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CITY_NAME));
                String shopAdress = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_ADRESS));
                String shopEmail = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_EMAIL));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LONGITUDE));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LATITUDE));

                Shop shop = new Shop();
                shop.setServerShopId(serverShopId);
                shop.setShopName(shopName);
                shop.setShopPhone(shopPhone);
                shop.setOpeningTime(openingTime);
                shop.setClosingTime(closingTime);
                shop.setVisibility(visibility);
                shop.setShopAdress(shopAdress);
                shop.setShopEmail(shopEmail);
                shop.setLongitude(longitude);
                shop.setLatitude(latitude);

                Country country = new Country(serverCountryId, countryName);
                shop.setCountry(country);

                City city = new City(serverCityId, cityName, country);
                shop.setCity(city);

                shops.add(shop);
            }
            cursor.close();
        }
        return shops;
    }


}
