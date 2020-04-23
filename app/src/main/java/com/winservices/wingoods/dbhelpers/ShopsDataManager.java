package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;
import com.winservices.wingoods.models.DateOff;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.models.WeekDayOff;

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
        for (int i = 0; i < shop.getDefaultCategories().size(); i++) {
            db.insertDefaultCategory(shop.getDefaultCategories().get(i));
        }
        for (int i = 0; i < shop.getWeekDaysOff().size(); i++) {
            db.insertWeekDayOff(shop.getWeekDaysOff().get(i));
        }
        for (int i = 0; i < shop.getDatesOff().size(); i++) {
            db.insertDateOff(shop.getDatesOff().get(i));
        }

    }

    public void deleteAllDefaultCategories(){
        db.deleteAllDefaultCategories();
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
        int serverShopTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_SHOP_TYPE_ID));
        String shopTypeName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_TYPE_NAME));
        int isDelivering = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_DELIVERING));
        int deliveryDelay = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DELIVERY_DELAY));

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
        shop.setIsDelivering(isDelivering);
        shop.setDeliveryDelay(deliveryDelay);

        shop.setDefaultCategories(getDCategories(serverShopId));
        shop.setWeekDaysOff(getWeekDaysOff(serverShopId));
        shop.setDatesOff(getDatesOff(serverShopId));

        Country country = new Country(serverCountryId, countryName);
        shop.setCountry(country);

        City city = new City(serverCityId, cityName, country);
        shop.setCity(city);

        ShopType shopType = new ShopType(serverShopTypeId, shopTypeName);
        shop.setShopType(shopType);

        return shop;

    }

    private List<DefaultCategory> getDCategories(int serverShopId){
        Cursor cursor = db.getDCategoriesByShopId(serverShopId);
        List<DefaultCategory> dCategories = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                String dCategoryName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_NAME));

                DefaultCategory dCategory = new DefaultCategory();
                dCategory.setServerShopId(serverShopId);
                dCategory.setDCategoryId(dCategoryId);
                dCategory.setDCategoryName(dCategoryName);

                dCategories.add(dCategory);

            }
            cursor.close();
        }
        return dCategories;
    }

    private List<WeekDayOff> getWeekDaysOff(int serverShopId){
        Cursor cursor = db.getWeekDaysOff(serverShopId);
        List<WeekDayOff> daysOff = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int dayOffId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                int dayOffValue = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DAY_OFF));

                WeekDayOff dayOff = new WeekDayOff();
                dayOff.setDayOffId(dayOffId);
                dayOff.setDayOff(dayOffValue);
                dayOff.setServerShopId(serverShopId);

                daysOff.add(dayOff);

            }
            cursor.close();
        }
        return daysOff;
    }

    private List<DateOff> getDatesOff(int serverShopId){
        Cursor cursor = db.getDatesOff(serverShopId);
        List<DateOff> datesOff = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int dateOffId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                String dateOffValue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DATE_OFF));
                String dateOffDesc = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DATE_OFF_DESC));

                DateOff dateOff = new DateOff();
                dateOff.setDateOffId(dateOffId);
                dateOff.setDateOffValue(dateOffValue);
                dateOff.setDateOffDesc(dateOffDesc);
                dateOff.setServerShopId(serverShopId);

                datesOff.add(dateOff);

            }
            cursor.close();
        }
        return datesOff;
    }

    public List<Shop> getAllShops(){
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
                int serverShopTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_SHOP_TYPE_ID));
                String shopTypeName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_TYPE_NAME));
                int isDelivering = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_IS_DELIVERING));
                int deliveryDelay = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DELIVERY_DELAY));

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
                shop.setIsDelivering(isDelivering);
                shop.setDeliveryDelay(deliveryDelay);

                Country country = new Country(serverCountryId, countryName);
                shop.setCountry(country);

                City city = new City(serverCityId, cityName, country);
                shop.setCity(city);

                ShopType shopType = new ShopType(serverShopTypeId, shopTypeName);
                shop.setShopType(shopType);

                shop.setDefaultCategories(getDCategories(serverShopId));
                shop.setWeekDaysOff(getWeekDaysOff(serverShopId));
                shop.setDatesOff(getDatesOff(serverShopId));

                shops.add(shop);
            }
            cursor.close();
        }
        return shops;
    }


}
