package com.winservices.wingoods.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.winservices.wingoods.models.Amount;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.DateOff;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Description;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.models.UserLocation;
import com.winservices.wingoods.models.WeekDayOff;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String COL_CATEGORY_NAME = "category_name";
    public static final String _ID = "_id";
    public static final int SYNC_STATUS_OK = 1;
    public static final int SYNC_STATUS_FAILED = 0;
    public static final int IS_LOGGED_IN = 1;
    private static final int IS_NOT_LOGGED_IN = 0;
    //sign up type
    public static final String LISTA = "lista";
    private static final String COL_RECEIVED_INVITATION_ID = "received_invitation_id";
    static final String COL_IS_ORDERED = "is_ordered";


    //TODO Version web
    private final static String webVersion = "lista_21"; //updated on 03/05/2020

    //TODO Lista LOCAL (compte root)
    /*private static final String HOST = "http://192.168.43.211/lista_local/"+webVersion+"/webservices/";
    static final String SHOPS_IMG_URL = "http://192.168.43.211/lista_local/lista_uploads/shopImages/";
    public static final String APP_IMG_URL = "http://192.168.43.211/lista_local/lista_uploads/appImgs/";*/

    //TODO Lista LWS_PRE_PROD
    private static final String HOST = "http://lista-courses.com/lista_pre_prod/"+webVersion+"/webservices/";
    static final String SHOPS_IMG_URL = "http://www.lista-courses.com/lista_pre_prod/lista_uploads/shopImages/";
    public static final String APP_IMG_URL = "http://www.lista-courses.com/lista_pre_prod/lista_uploads/appImgs/";


    //TODO Lista LWS_PROD
    /*private static final String HOST = "http://lista-courses.com/lista_prod/"+webVersion+"/webservices/";
    static final String SHOPS_IMG_URL = "http://www.lista-courses.com/lista_prod/lista_uploads/shopImages/";
    public static final String APP_IMG_URL = "http://www.lista-courses.com/lista_prod/lista_uploads/appImgs/";*/

    private final static int DATABASE_VERSION = 11; //updated on 10-05-2020


    static final String GOODS_TO_BUY_NUMBER = "goods_to_buy_number";
    static final String ORDERED_GOODS_NUMBER = "ordered_goods_number";
    static final String GOODS_NUMBER = "goods_number";
    public static final String TAG = "DataBaseHelper";
    static final String COL_SERVER_USER_ID = "server_user_id";
    static final String COL_EMAIL = "email";
    static final String COL_USERID = "user_id";
    static final String COL_SYNC_STATUS = "sync_status";
    static final String COL_CATEGORY_COLOR = "category_color";
    static final String COL_CATEGORY_ICON = "category_icon";
    static final String COL_CRUD_STATUS = "crud_status";
    static final String COL_D_CATEGORY_ID = "d_category_id";
    private static final String COL_AMOUNT_ID = "amount_id";
    static final String COL_AMOUNT_VALUE = "amount_value";
    static final String COL_AMOUNT_TYPE_ID = "amount_type_id";
    static final String COL_AMOUNT_TYPE_NAME = "amount_type_name";
    static final String COL_CONFIRMATION_STATUS = "confirmation_status";
    static final String COL_CO_USER_ID = "co_user_id";
    static final String COL_CO_USER_EMAIL = "co_user_email";
    static final String COL_SERVER_CO_USER_ID = "server_co_user_id";
    static final String COL_SERVER_GROUP_ID = "server_group_id";
    static final String COL_SERVER_CATEGORY_ID = "server_category_id";
    static final String COL_SERVER_GOOD_ID = "server_good_id";
    static final String COL_HAS_RESPONDED = "has_responded";
    static final String COL_SENDER_PHONE = "sender_phone";
    static final String COL_INVITATION_RESPONSE = "invitation_response";
    static final String COL_CATEGORY_ID = "category_id";
    static final String COL_GOOD_NAME = "good_name";
    static final String COL_GOOD_DESC = "good_desc";
    static final String COL_QUANTITY_LEVEL = "quantity_level";
    static final String COL_IS_TO_BUY = "is_to_buy";
    private static final String RED_GOODS = "red_goods";
    private static final String ORANGE_GOODS = "orange_goods";
    private static final String GREEN_GOODS = "green_goods";
    static final String COL_DESC_ID = "desc_id";
    static final String COL_DESC_VALUE = "desc_value";
    static final String COL_CO_USER_PHONE = "co_user_phone";
    static final String COL_START_TIME = "start_time";
    static final String COL_END_TIME = "end_time";
    static final String COL_SHOP_PHONE = "shop_phone";
    static final String COL_SERVER_SHOP_ID = "server_shop_id";
    static final String COL_CREATION_DATE = "creation_date";
    static final String COL_ORDER_STATUS_ID = "status_id";
    static final String COL_ORDER_STATUS_NAME = "status_name";
    static final String COL_SHOP_NAME = "shop_name";
    static final String COL_ORDERED_GOODS_NUMBER = "ordered_goods_number";
    static final String COL_OPENING_TIME = "opening_time";
    static final String COL_CLOSING_TIME = "closing_time";
    static final String COL_PRICE_ID = "price_id";


    public static final String HOST_URL_STORE_DEVICE_INFOS = HOST + "storeDeviceInfos.php";
    public static final String HOST_URL_CHECK_CO_USER_REGISTRATION = HOST + "checkCoUserRegistration.php";
    public static final String HOST_URL_GET_AVAILABLE_ORDERS_NUM = HOST + "getAvailableOrdersNum.php";
    public static final String HOST_URL_REGISTER_USER = HOST + "registerUser.php";
    //public static final String HOST_URL_LOGIN_USER = HOST + "loginUser.php";
    //static final String HOST_URL_ADD_CO_USER = HOST + "addCoUser.php";
    public static final String HOST_URL_GET_INVITATIONS = HOST + "getInvitations.php";
    //static final String HOST_URL_UPDATE_CO_USER_RESPONSE = HOST + "updateCoUserResponse.php";
    public static final String HOST_URL_ADD_GROUP_AND_CO_USER = HOST + "addGroupAndCoUser.php";
    //static final String HOST_URL_GET_GROUP_DATA = HOST + "getGroupData.php";
    static final String HOST_URL_DELETE_USER_CATEGORIES_AND_GOODS = HOST + "deleteAllUserCategoriesAndGoods.php";
    //static final String HOST_URL_UPDATE_CATEGORIES_AND_GOODS = HOST + "updateCategoriesAndGoods.php";
    //static final String HOST_URL_UPDATE_CATEGORIES_AND_GOODS_FROM_SERVER = HOST + "updateCategoriesAndGoodsFromServer.php";
    public static final String HOST_URL_GET_TEAM_MEMBERS = HOST + "getTeamMembers.php";
    //static final String HOST_URL_ADD_CATEGORIES = HOST + "addCategories.php";
    //public static final String HOST_URL_ADD_GOODS = HOST + "addGoods.php";
    //public static final String HOST_URL_GET_SHOPS = HOST + "getShops.php";
    public static final String HOST_URL_GET_CITIES = HOST + "getCities.php";
    public static final String HOST_URL_ADD_ORDER = HOST + "addOrder.php";
    //public static final String HOST_URL_GET_ORDERS = HOST + "getOrders.php";
    public static final String HOST_URL_GET_ORDERED_GOODS = HOST + "getOrderedGoods.php";
    static final String HOST_URL_SYNC = HOST + "sync.php";
    public static final String HOST_URL_COMPLETE_ORDER = HOST + "completeOrder.php";
    public static final String HOST_URL_UPLOAD_USER_IMAGE = HOST + "uploadUserImage.php";
    public static final String HOST_URL_UPDATE_CITY = HOST + "updateUserCity.php";
    //public static final String HOST_URL_GET_SHOP_DETAILS = HOST + "getShopDetails.php";



    private static final int DELETED = -1;
    private static final int RESTORED = 1;
    private static final String TABLE_CO_USERS = "co_users";
    private static final String TABLE_DESCRIPTIONS = "descriptions";
    private static final String COL_LAST_LOGGED_IN = "last_logged_in";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_AMOUNTS = "amounts";
    private static final String COL_GROUP_ID = "group_id";
    private static final String COL_GROUP_NAME = "group_name";
    private static final String COL_OWNER_EMAIL = "owner_email";
    private static final String COL_SERVER_OWNER_ID = "server_owner_id";
    private static final String COL_SIGN_UP_TYPE = "sign_up_type";
    private static final String DATABASE_NAME = "DB_WinGoods.sqlite";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_GOODS = "goods";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_SHOPS = "shops";
    private static final String COL_GOOD_ID = "good_id";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_RECEIVED_INVITATIONS = "received_invitations";
    private static final String COL_PASSWORD = "password";
    private static final String COL_USERNAME = "user_name";
    private static final String COL_DEVICE_DESC_ID = "device_desc_id";
    private static final String COL_USER_PHONE = "user_phone";
    private static final String COL_SERVER_ORDER_ID = "server_order_id";
    static final String COL_SERVER_SHOP_TYPE_ID = "server_shop_type_id";
    static final String COL_SHOP_TYPE_NAME = "shop_type_name";
    static final String COL_USES_NUMBER = "uses_number";
    static final String COL_VISIBILITY = "visibility";
    static final String COL_SERVER_COUNTRY_ID = "server_country_id";
    static final String COL_COUNTRY_NAME = "country_name";
    static final String COL_SERVER_CITY_ID = "server_city_id";
    static final String COL_CITY_NAME = "city_name";
    static final String COL_SHOP_ADRESS = "shop_adress";
    static final String COL_SHOP_EMAIL = "shop_email";
    static final String COL_LONGITUDE = "longitude";
    static final String COL_LATITUDE = "latitude";
    private static final String TABLE_DEFAULT_CATEGORIES = "default_categories";
    static final String COL_D_CATEGORY_NAME = "d_category_name";
    static final String COL_IS_DELIVERING = "is_delivering";
    static final String COL_IS_TO_DELIVER = "is_to_deliver";
    static final String COL_USER_LOCATION = "user_location";
    static final String COL_ORDER_PRICE = "order_price";
    static final String COL_DELIVERY_DELAY = "delivery_delay";
    static final String TABLE_WEEK_DAYS_OFF = "week_days_off";
    static final String COL_DAY_OFF_ID = "day_off_id";
    static final String COL_DAY_OFF = "day_off";
    static final String TABLE_DATES_OFF = "dates_off";
    static final String COL_DATE_OFF_ID = "date_off_id";
    static final String COL_DATE_OFF_DESC = "date_off_desc";
    static final String COL_DATE_OFF = "date_off";
    static final String TABLE_USER_LOCATIONS = "user_locations";
    static final String COL_USER_LOCATION_ID = "user_location_id";
    static final String COL_USER_ADDRESS = "user_address";
    static final String COL_USER_GPS_LOCATION = "user_gps_location";
    static final String TABLE_CITIES = "cities";
    static final String COL_FACEBOOK_URL = "facebook_url";
    static final String COL_WEBSITE_URL = "website_url";

    private static DataBaseHelper instance;
    private SQLiteDatabase db;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DataBaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (instance == null) {
            instance = new DataBaseHelper(context.getApplicationContext());
            Log.d(TAG, Constants.TAG_LISTA + "DB Opened");
        }
        return instance;
    }

    public static void closeDB() {
        instance.close();
        instance = null;
        Log.d(TAG, Constants.TAG_LISTA + "DB Closed");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        this.db = db;

        db.execSQL("CREATE TABLE IF NOT EXISTS users ( " +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "password TEXT, " +
                "user_name INTEGER, " +
                "server_user_id INTEGER," +
                "server_group_id INTEGER," +
                "sign_up_type TEXT," +
                "last_logged_in INTEGER," +
                "group_id INTEGER, " +
                "user_phone TEXT, " +
                "server_city_id INTEGER " +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS groups ( " +
                "group_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "group_name TEXT," +
                "owner_email TEXT," +
                "server_owner_id INTEGER," +
                "server_group_id INTEGER," +
                "sync_status INTEGER " +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS co_users (" +
                "co_user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "co_user_email TEXT, " +
                "user_id INTEGER, " +
                "email TEXT, " +
                "confirmation_status INTEGER, " +
                "has_responded INTEGER, " +
                "server_co_user_id INTEGER, " +
                "sync_status INTEGER, " +
                "co_user_phone TEXT, " +
                "server_user_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users (user_id)) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS categories ( " +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_name TEXT, " +
                "category_color INTEGER, " +
                "category_icon INTEGER, " +
                "sync_status INTEGER, " +
                "user_id INTEGER, " +
                "email TEXT, " +
                "crud_status INTEGER, " +
                "server_category_id INTEGER," +
                "d_category_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users (user_id) ) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS goods (" +
                "good_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "good_name TEXT, " +
                "good_desc TEXT, " +
                "quantity_level INTEGER, " +
                "category_id INTEGER, " +
                "is_to_buy BOOLEAN, " +
                "sync_status INTEGER, " +
                "email TEXT, " +
                "crud_status INTEGER, " +
                "server_good_id INTEGER, " +
                "is_ordered INTEGER, " +
                "uses_number INTEGER, " +
                "price_id INTEGER, " +
                "FOREIGN KEY (category_id) REFERENCES categories (category_id)) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS received_invitations ( " +
                "received_invitation_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_phone TEXT, " +
                "invitation_response INTEGER, " +
                "user_id INTEGER, " +
                "server_group_id INTEGER, " +
                "server_co_user_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users (user_id)) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS amounts ( " +
                "amount_id INTEGER PRIMARY KEY, " +
                "amount_value TEXT, " +
                "amount_type_id INTEGER, " +
                "amount_type_name TEXT ) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS descriptions ( " +
                "device_desc_id INTEGER PRIMARY KEY, " +
                "desc_id INTEGER, " +
                "desc_value TEXT, " +
                "d_category_id INTEGER ) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS cities ( " +
                "server_city_id INTEGER PRIMARY KEY, " +
                "city_name TEXT, " +
                "server_country_id INTEGER, " +
                "longitude TEXT, " +
                "latitude TEXT ) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS user_locations ( " +
                "user_location_id INTEGER PRIMARY KEY, " +
                "user_address INTEGER, " +
                "user_gps_location TEXT, " +
                "server_user_id INTEGER ) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS shops ( " +
                "server_shop_id INTEGER PRIMARY KEY, " +
                "shop_name TEXT, " +
                "shop_phone TEXT, " +
                "opening_time TEXT, " +
                "closing_time TEXT, " +
                "server_shop_type_id INTEGER, " +
                "visibility INTEGER, " +
                "server_country_id INTEGER, " +
                "country_name TEXT, " +
                "server_city_id INTEGER, " +
                "city_name TEXT, " +
                "shop_adress TEXT, " +
                "shop_email TEXT, " +
                "longitude REAL, " +
                "latitude REAL, " +
                "shop_type_name TEXT, " +
                "is_delivering INTEGER, " +
                "delivery_delay INTEGER, " +
                "facebook_url TEXT, " +
                "website_url TEXT) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS week_days_off ( " +
                "day_off_id INTEGER PRIMARY KEY, " +
                "day_off INTEGER, " +
                "server_shop_id INTEGER, " +
                "FOREIGN KEY (server_shop_id) REFERENCES shops (server_shop_id)) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS dates_off ( " +
                "date_off_id INTEGER PRIMARY KEY, " +
                "date_off TEXT, " +
                "server_shop_id INTEGER, " +
                "date_off_desc TEXT, " +
                "FOREIGN KEY (server_shop_id) REFERENCES shops (server_shop_id)) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS orders ( " +
                "server_user_id INTEGER, " +
                "server_shop_id INTEGER, " +
                "server_order_id INTEGER PRIMARY KEY, " +
                "creation_date TEXT ," +
                "status_id INTEGER, " +
                "status_name TEXT," +
                "ordered_goods_number INTEGER," +
                "start_time TEXT," +
                "end_time TEXT," +
                "is_to_deliver INTEGER," +
                "user_address TEXT," +
                "user_location TEXT," +
                "order_price TEXT," +
                "FOREIGN KEY (server_shop_id) REFERENCES shops (server_shop_id)) ");

        db.execSQL("CREATE TABLE IF NOT EXISTS default_categories ( " +
                "d_category_id INTEGER, " +
                "d_category_name TEXT, " +
                "server_shop_id INTEGER, " +
                "FOREIGN KEY (server_shop_id) REFERENCES shops (server_shop_id)) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();

        switch (oldVersion){
            case 8 :
            case 9 :
                db.execSQL("CREATE TABLE IF NOT EXISTS cities ( " +
                        "server_city_id INTEGER PRIMARY KEY, " +
                        "city_name TEXT, " +
                        "server_country_id INTEGER, " +
                        "longitude TEXT, " +
                        "latitude TEXT ) ");
                db.execSQL("ALTER TABLE users ADD COLUMN server_city_id INTEGER");
                db.execSQL("ALTER TABLE goods ADD COLUMN  price_id INTEGER");
                db.execSQL("ALTER TABLE shops ADD COLUMN facebook_url TEXT");
                db.execSQL("ALTER TABLE shops ADD COLUMN  website_url TEXT");
                break;
            case 10 :
                db.execSQL("ALTER TABLE shops ADD COLUMN facebook_url TEXT");
                db.execSQL("ALTER TABLE shops ADD COLUMN  website_url TEXT");
                break;
            default:
                db.execSQL("DROP TABLE IF EXISTS shops ");
                db.execSQL("DROP TABLE IF EXISTS users ");
                onCreate(db);
        }


    }


    //SELECT QUERIES

    //DEFAULT CATEGORIES
    Cursor getDCategoriesByShopId(int serverShopId){
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_D_CATEGORY_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_DEFAULT_CATEGORIES
                    + " WHERE " + COL_SERVER_SHOP_ID + " = " + serverShopId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //WEEKDAYSOFF
    Cursor getWeekDaysOff(int serverShopId){
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_DAY_OFF_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_WEEK_DAYS_OFF
                    + " WHERE " + COL_SERVER_SHOP_ID + " = " + serverShopId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //DATESOFF
    Cursor getDatesOff(int serverShopId){
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_DATE_OFF_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_DATES_OFF
                    + " WHERE " + COL_SERVER_SHOP_ID + " = " + serverShopId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //SHOPS
    Cursor getShopById(int serverShopId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_SERVER_SHOP_ID + " as " + _ID + " , *"
                    + " from " + TABLE_SHOPS
                    + " where " + COL_SERVER_SHOP_ID + " = " + serverShopId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getAllShops(){
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_SERVER_SHOP_ID + " as " + _ID + " , *"
                    + " from " + TABLE_SHOPS
                    + " Where " + COL_VISIBILITY + " = 1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getShopsByServerCityId(int serverCityId){
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_SERVER_SHOP_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_SHOPS
                    + " WHERE " + COL_VISIBILITY + " = 1"
                    + " AND " + COL_SERVER_CITY_ID + " = " + serverCityId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //ORDERS

    Cursor getOrders(int groupedStatus) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            switch (groupedStatus) {
                case Order.ALL:
                    res = db.rawQuery("select " + COL_SERVER_ORDER_ID + " AS " + _ID + " , " + TABLE_ORDERS + ".* ," + TABLE_SHOPS + ".*"
                            + " FROM " + TABLE_ORDERS + ", " + TABLE_SHOPS
                            + " WHERE " + TABLE_ORDERS + "." + COL_SERVER_SHOP_ID + " = " + TABLE_SHOPS + "." + COL_SERVER_SHOP_ID
                            + " AND " + TABLE_SHOPS + "." + COL_VISIBILITY + " = 1"
                            + " ORDER BY " + TABLE_ORDERS + "." + COL_SERVER_ORDER_ID + " DESC", null);
                    break;
                case Order.NOT_CLOSED:
                    res = db.rawQuery("select " + COL_SERVER_ORDER_ID + " AS " + _ID + " , " + TABLE_ORDERS + ".* ," + TABLE_SHOPS + ".*"
                            + " FROM " + TABLE_ORDERS + ", " + TABLE_SHOPS
                            + " WHERE " + TABLE_ORDERS + "." + COL_ORDER_STATUS_ID + " NOT IN (" + Order.COMPLETED + ")"
                            + " AND " + TABLE_ORDERS + "." + COL_SERVER_SHOP_ID + " = " + TABLE_SHOPS + "." + COL_SERVER_SHOP_ID
                            + " AND " + TABLE_SHOPS + "." + COL_VISIBILITY + " = 1"
                            + " ORDER BY " + TABLE_ORDERS + "." + COL_SERVER_ORDER_ID + " DESC", null);
                    break;
                case Order.CLOSED:
                    res = db.rawQuery("select " + COL_SERVER_ORDER_ID + " AS " + _ID + " , " + TABLE_ORDERS + ".* ," + TABLE_SHOPS + ".*"
                            + " FROM " + TABLE_ORDERS + ", " + TABLE_SHOPS
                            + " WHERE " + TABLE_ORDERS + "." + COL_ORDER_STATUS_ID + " IN (" + Order.COMPLETED + ")"
                            + " AND " + TABLE_ORDERS + "." + COL_SERVER_SHOP_ID + " = " + TABLE_SHOPS + "." + COL_SERVER_SHOP_ID
                            + " AND " + TABLE_SHOPS + "." + COL_VISIBILITY + " = 1"
                            + " ORDER BY " + TABLE_ORDERS + "." + COL_SERVER_ORDER_ID + " DESC", null);
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Order getOrder(int serverOrderId) {
        db = this.getReadableDatabase();
        Cursor res;
        Order order = new Order();
        try {
            res = db.rawQuery("select " + COL_SERVER_ORDER_ID + " AS " + _ID + " , " + TABLE_ORDERS + ".* ," + TABLE_SHOPS + ".*"
                    + " FROM " + TABLE_ORDERS + ", " + TABLE_SHOPS
                    + " WHERE " + TABLE_ORDERS + "." + COL_SERVER_SHOP_ID + " = " + TABLE_SHOPS + "." + COL_SERVER_SHOP_ID
                    + " AND " + TABLE_ORDERS + "." + COL_SERVER_ORDER_ID + " = " + serverOrderId, null);

            while (res.moveToNext()) {
                int serverUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_USER_ID));
                int serverShopId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_SHOP_ID));
                Date creationDate = UtilsFunctions.stringToDate(res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_CREATION_DATE)));
                int statusId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_ORDER_STATUS_ID));
                String statusName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_ORDER_STATUS_NAME));
                int orderedGoodsNumber = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_ORDERED_GOODS_NUMBER));
                String shopName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_NAME));
                String startTime = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_START_TIME));
                String endTime = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_END_TIME));
                int serverShopTypeId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_SHOP_TYPE_ID));
                String shopTypeName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_SHOP_TYPE_NAME));
                int isToDeliver = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_IS_TO_DELIVER));
                String userAddress = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_USER_ADDRESS));
                String userLocation = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_USER_LOCATION));
                String orderPrice = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_ORDER_PRICE));

                order.setServerOrderId(serverOrderId);

                User user = new User();
                user.setServerUserId(serverUserId);
                order.setUser(user);

                Shop shop = new Shop();
                shop.setServerShopId(serverShopId);
                shop.setShopName(shopName);
                order.setShop(shop);

                ShopType shopType = new ShopType(serverShopTypeId, shopTypeName);
                shop.setShopType(shopType);

                order.setCreationDate(creationDate);
                order.setStatusId(statusId);
                order.setStatusName(statusName);
                order.setOrderedGoodsNumber(orderedGoodsNumber);
                order.setStartTime(startTime);
                order.setEndTime(endTime);
                order.setIsToDeliver(isToDeliver);
                order.setUserAddress(userAddress);
                order.setUserLocation(userLocation);
                order.setOrderPrice(orderPrice);

            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    //DESCRIPTIONS
    Cursor getDescriptions(int dCategoryId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            if (dCategoryId == Description.ALL) {
                res = db.rawQuery("select " + COL_DEVICE_DESC_ID + " as " + _ID + " , *"
                        + " from " + TABLE_DESCRIPTIONS, null);
            } else {
                res = db.rawQuery("select " + COL_DEVICE_DESC_ID + " as " + _ID + " , *"
                        + " from " + TABLE_DESCRIPTIONS
                        + " where " + COL_D_CATEGORY_ID + " = " + dCategoryId, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getDescByServerId(int descId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_DEVICE_DESC_ID + " as " + _ID + " , *"
                    + " from " + TABLE_DESCRIPTIONS
                    + " where " + COL_DESC_ID + " = " + descId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getUserDescByValue(String descValue) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_DEVICE_DESC_ID + " as " + _ID + " , *"
                    + " from " + TABLE_DESCRIPTIONS
                    + " where " + COL_DESC_VALUE + " = '" + descValue
                    + "' and " + COL_DESC_ID + " = " + Description.USER_DESCRIPTION, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //AMOUNTS
    Cursor getAmounts(int amountTypeId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            if (amountTypeId == Amount.ALL) {
                res = db.rawQuery("select " + COL_AMOUNT_ID + " as " + _ID + " , *"
                        + " from " + TABLE_AMOUNTS, null);
            } else {
                res = db.rawQuery("select " + COL_AMOUNT_ID + " as " + _ID + " , *"
                        + " from " + TABLE_AMOUNTS
                        + " where " + COL_AMOUNT_TYPE_ID + " = " + amountTypeId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //AMOUNTS
    Cursor getCities() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
                res = db.rawQuery("select " + COL_SERVER_CITY_ID + " as " + _ID + " , *"
                        + " from " + TABLE_CITIES, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getAmountById(int amountId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_AMOUNT_ID + " as " + _ID + " , * "
                    + " FROM " + TABLE_AMOUNTS
                    + " where " + COL_AMOUNT_ID + " = " + amountId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //GROUPS

    public Group getGroupById(int groupId) {

        db = this.getReadableDatabase();
        Cursor res;
        Group group = null;
        try {
            String sql = "select " + TABLE_GROUPS + "." + COL_GROUP_ID + " as " + _ID + " , * "
                    + " from " + TABLE_GROUPS
                    + " WHERE " + COL_GROUP_ID + " = " + groupId;

            res = db.rawQuery(sql, null);

            while (res.moveToNext()) {
                //int groupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                String groupName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_GROUP_NAME));
                String ownerEmail = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_OWNER_EMAIL));
                int serverOwnerId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_OWNER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));
                int syncStatus = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));

                group = new Group(groupId, groupName, ownerEmail, serverOwnerId, serverGroupId, syncStatus);

            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }

    Group getGroupByUserId(int serverUserId) {
        db = this.getReadableDatabase();
        Cursor res;
        Group group = null;
        try {
            String sql = "select " + TABLE_GROUPS + "." + COL_GROUP_ID + " as " + _ID + " , " + TABLE_GROUPS + ".* "
                    + " from " + TABLE_GROUPS + ", " + TABLE_USERS
                    + " WHERE " + TABLE_GROUPS + "." + COL_GROUP_ID + " = " + TABLE_USERS + "." + COL_GROUP_ID
                    + " AND " + TABLE_USERS + "." + COL_SERVER_USER_ID + " = " + serverUserId;

            res = db.rawQuery(sql, null);

            while (res.moveToNext()) {
                int groupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                String groupName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_GROUP_NAME));
                String ownerEmail = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_OWNER_EMAIL));
                int serverOwnerId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_OWNER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));
                int syncStatus = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));

                group = new Group(groupId, groupName, ownerEmail, serverOwnerId, serverGroupId, syncStatus);

            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }


    Group getGroupByOwnerId(int serverOwnerID) {
        db = this.getReadableDatabase();
        Cursor res;
        Group group = null;
        try {

            String sql = "select " + COL_GROUP_ID + " as " + _ID + " , * "
                    + " from " + TABLE_GROUPS
                    + " WHERE " + TABLE_GROUPS + "." + COL_SERVER_OWNER_ID + " = " + serverOwnerID;

            res = db.rawQuery(sql, null);

            while (res.moveToNext()) {
                int groupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                String groupName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_GROUP_NAME));
                String ownerEmail = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_OWNER_EMAIL));
                int serverOwnerId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_OWNER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));
                int syncStatus = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));

                group = new Group(groupId, groupName, ownerEmail, serverOwnerId, serverGroupId, syncStatus);

            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }

    //INVITATIONS

    boolean invitationExists(String senderPhone) {
        String countQuery = "select * from " + TABLE_RECEIVED_INVITATIONS
                + " where "
                + COL_SENDER_PHONE + " = '" + senderPhone + "'"
                + "AND " + COL_USERID + "=" + getCurrentUser().getUserId();

        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return (cnt > 0);
    }

    boolean isInvitationPending() {
        String countQuery = "select * from " + TABLE_RECEIVED_INVITATIONS
                + " where "
                + COL_INVITATION_RESPONSE + " = " + CoUser.PENDING;

        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return (cnt > 0);
    }


    ReceivedInvitation getReceivedInvitation(String senderPhone) {
        db = this.getReadableDatabase();
        Cursor res;
        ReceivedInvitation invitation = null;
        try {
            res = db.rawQuery("SELECT " + COL_RECEIVED_INVITATION_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_RECEIVED_INVITATIONS
                    + " WHERE " + COL_SENDER_PHONE + " = '" + senderPhone + "'"
                    + " AND " + COL_USERID + "=" + getCurrentUser().getUserId(), null);

            while (res.moveToNext()) {
                int receivedInvitationID = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                int userId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
                int invitationResponse = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_INVITATION_RESPONSE));
                int serverCoUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CO_USER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));

                invitation = new ReceivedInvitation(receivedInvitationID, senderPhone, invitationResponse, userId);
                invitation.setServerCoUserId(serverCoUserId);
                invitation.setServerGroupId(serverGroupId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return invitation;
    }

    Cursor getNotSyncReceivedInvitations() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            String sql = "select " + COL_RECEIVED_INVITATION_ID + " as " + _ID + " , * "
                    + " from " + TABLE_RECEIVED_INVITATIONS
                    + " where " + COL_INVITATION_RESPONSE + " <> " + CoUser.COMPLETED
                    + " and " + COL_USERID + "=" + getCurrentUser().getUserId();
            res = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getReceivedInvitationById(int id) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_RECEIVED_INVITATION_ID + " as " + _ID + " , * "
                    + " FROM " + TABLE_RECEIVED_INVITATIONS
                    + " where " + COL_RECEIVED_INVITATION_ID + " = " + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //COUSERS

    Cursor getNotSyncCoUsers() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CO_USER_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CO_USERS
                    + " where " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_USERID + "=" + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    boolean coUserExists(String coUserPhone, int userId) {
        String countQuery = "select * from " + TABLE_CO_USERS
                + " where "
                + COL_USERID + " = " + userId
                + " AND " + COL_CO_USER_PHONE + " = '" + coUserPhone + "'";

        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return (cnt > 0);
    }

    Cursor getCoUserById(int coUserId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_CO_USER_ID + " as " + _ID + " , * "
                    + " FROM " + TABLE_CO_USERS
                    + " where " + COL_CO_USER_ID + " = " + coUserId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //USERS


    User getUserByEmail(String email, String signUpType) {
        db = this.getReadableDatabase();
        Cursor res;
        User user = null;
        try {
            res = db.rawQuery("SELECT " + COL_USERID + " as " + _ID + " , *"
                    + " FROM " + TABLE_USERS
                    + " where " + COL_EMAIL + " = '" + email + "'"
                    + " AND " + COL_SIGN_UP_TYPE + " = '" + signUpType + "'", null);


            while (res.moveToNext()) {
                int userId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                //String email = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
                String password = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_PASSWORD));
                String userName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_USERNAME));
                int serverUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_USER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));
                //String signUpType = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_SIGN_UP_TYPE));
                int lastLoggedIn = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_LAST_LOGGED_IN));
                int groupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_GROUP_ID));

                user = new User(userId, email, password, userName);
                user.setServerUserId(serverUserId);
                user.setServerGroupId(serverGroupId);
                user.setSignUpType(signUpType);
                user.setLastLoggedIn(lastLoggedIn);
                user.setGroupId(groupId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    User getUserByServerUserId(int serverUserId) {
        db = this.getReadableDatabase();
        Cursor res;
        User user = null;
        try {
            res = db.rawQuery("SELECT " + COL_USERID + " as " + _ID + " , *"
                    + " FROM " + TABLE_USERS
                    + " where " + COL_SERVER_USER_ID + " = " + serverUserId, null);


            while (res.moveToNext()) {
                int userId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                String email = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
                String password = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_PASSWORD));
                String userName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_USERNAME));
                //int serverUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_USER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));
                String signUpType = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_SIGN_UP_TYPE));
                int lastLoggedIn = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_LAST_LOGGED_IN));
                int groupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_GROUP_ID));

                user = new User(userId, email, password, userName);
                user.setServerUserId(serverUserId);
                user.setServerGroupId(serverGroupId);
                user.setSignUpType(signUpType);
                user.setLastLoggedIn(lastLoggedIn);
                user.setGroupId(groupId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }


    boolean userExists(User user) {
        String countQuery = "select * from " + TABLE_USERS
                + " where " + COL_EMAIL + " = '" + user.getEmail() + "'"
                + " AND " + COL_SIGN_UP_TYPE + " = '" + user.getSignUpType() + "'";
        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return (cnt > 0);
    }

    User getCurrentUser() {
        db = this.getReadableDatabase();
        Cursor res;
        User currentUser = null;
        try {
            res = db.rawQuery("SELECT " + COL_USERID + " as " + _ID + " , *"
                    + " FROM " + TABLE_USERS
                    + " where " + COL_LAST_LOGGED_IN + " = " + IS_LOGGED_IN, null);

            while (res.moveToNext()) {
                int userId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                String email = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
                String password = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_PASSWORD));
                String userName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_USERNAME));
                int serverUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_USER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));
                String signUpType = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_SIGN_UP_TYPE));
                int lastLoggedIn = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_LAST_LOGGED_IN));
                int groupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_GROUP_ID));
                String userPhone = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_USER_PHONE));
                int serverCityId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CITY_ID));

                currentUser = new User(userId, email, password, userName);
                currentUser.setServerUserId(serverUserId);
                currentUser.setServerGroupId(serverGroupId);
                currentUser.setSignUpType(signUpType);
                currentUser.setLastLoggedIn(lastLoggedIn);
                currentUser.setGroupId(groupId);
                currentUser.setUserPhone(userPhone);
                currentUser.setServerCityId(serverCityId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentUser;
    }



    //CATEGORIES


    int getGoodsToBuyNumber(int categoryId) {
        String countQuery = "SELECT " + TABLE_GOODS + ".* FROM " + TABLE_GOODS
                + " WHERE " + TABLE_GOODS + "." + COL_CATEGORY_ID + " = " + categoryId
                + " AND " + TABLE_GOODS + "." + COL_IS_TO_BUY + " = 1 "
                + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> " + DELETED;
        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return cnt;
    }

    int getOrderedGoodsNumber(int categoryId) {
        String countQuery = "SELECT * FROM " + TABLE_GOODS
                + " WHERE " + COL_CATEGORY_ID + " = " + categoryId
                + " AND " + COL_IS_ORDERED + " = 1 "
                + " AND " + COL_CRUD_STATUS + " <> " + DELETED;
        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return cnt;
    }

    int getGoodsNumber(int categoryId) {
        String countQuery = "SELECT * FROM " + TABLE_GOODS
                + " WHERE " + COL_CATEGORY_ID + " = " + categoryId
                + " AND " + COL_CRUD_STATUS + " <> " + DELETED;
        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return cnt;
    }


    Cursor getCategoryByName(String name) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CATEGORY_NAME + " = '" + name.replaceAll("'", "''") + "' "
                    + " and " + COL_USERID + " = " + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoryByServerCategoryIdAndUserId(int serverCategoryId, int userId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_SERVER_CATEGORY_ID + " = " + serverCategoryId
                    + " and " + COL_USERID + " = " + userId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoryByServerCategoryId(int serverCategoryId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_SERVER_CATEGORY_ID + " = " + serverCategoryId
                    + " and " + COL_SERVER_CATEGORY_ID + " = " + serverCategoryId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getCategoryByCrud(int crudStatus) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CRUD_STATUS + " = " + crudStatus +
                    " and " + COL_USERID + " = " + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Cursor getAllCategories() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = " + getCurrentUser().getUserId()
                    + " and " + COL_CRUD_STATUS + " <> -1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Cursor getCategoriesWithGoodsNotOrdered() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " AS " + _ID + " , " + TABLE_CATEGORIES + "." + "*"
                    + " FROM " + TABLE_CATEGORIES + ", " + TABLE_GOODS + "," + TABLE_USERS
                    + " WHERE " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_CATEGORIES + "." + COL_USERID + " = " + TABLE_USERS + "." + COL_USERID
                    + " AND " + TABLE_GOODS + "." + COL_IS_TO_BUY + " = 1"
                    + " AND " + TABLE_GOODS + "." + COL_IS_ORDERED + " = 0"
                    + " AND " + TABLE_USERS + "." + COL_USERID + " = " + getCurrentUser().getUserId()
                    + " GROUP BY " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //return categories that are already synchronized with server (have a server category id)
    Cursor getExcludedCategoriesFromSync() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = " + getCurrentUser().getUserId()
                    + " and " + COL_SERVER_CATEGORY_ID + " <> 0", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //return categories that have been updated on mobile user (but already have been synchronized with server)
    Cursor getUpdatedCategories() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = " + getCurrentUser().getUserId()
                    + " and " + COL_SERVER_CATEGORY_ID + " <> 0"
                    + " and " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_USERID + "=" + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    /*Cursor getAllCategoriesOverview() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " , ( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_QUANTITY_LEVEL + " = " + "'1'"
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1 "
                    + " GROUP BY " + COL_CATEGORY_ID + " ) as " + RED_GOODS
                    + " , ( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_QUANTITY_LEVEL + " = " + "'2'"
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1 "
                    + " GROUP BY " + COL_CATEGORY_ID + " ) as " + ORANGE_GOODS
                    + " , ( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_QUANTITY_LEVEL + " = " + "'3'"
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1 "
                    + " GROUP BY " + COL_CATEGORY_ID + " ) as " + GREEN_GOODS
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CRUD_STATUS + " <> -1"
                    + " and " + COL_USERID + " = " + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }*/


    Cursor getCategoryById(int categoryId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            String sql = "SELECT " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " as " + _ID + " , "
                    + TABLE_CATEGORIES + ".* ,"

                    + "( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_IS_TO_BUY + " = " + "'1'"
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1 "
                    + " AND " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + categoryId
                    + " GROUP BY " + TABLE_GOODS + "." + COL_CATEGORY_ID + " ) as " + GOODS_TO_BUY_NUMBER + ", "

                    + "( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_IS_ORDERED + " = " + "'1'"
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1 "
                    + " AND " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + categoryId
                    + " GROUP BY " + TABLE_GOODS + "." + COL_CATEGORY_ID + " ) as " + ORDERED_GOODS_NUMBER + ", "

                    + "( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1 "
                    + " AND " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + categoryId
                    + " GROUP BY " + TABLE_GOODS + "." + COL_CATEGORY_ID + " ) as " + GOODS_NUMBER

                    + " FROM " + TABLE_CATEGORIES
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + categoryId;

            res = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getCategoryByGoodId(int goodId) {
        db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " as " + _ID + " , " + TABLE_CATEGORIES + "." + "*"
                    + " FROM " + TABLE_CATEGORIES + " , " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = " + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " and " + TABLE_GOODS + "." + COL_GOOD_ID + " = " + goodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getNotSyncCategories() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_SERVER_CATEGORY_ID + " = 0"
                    + " and " + COL_USERID + "=" + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //GOODS

    Cursor getGoodById(int goodId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , * "
                    + " FROM " + TABLE_GOODS
                    + " where " + COL_GOOD_ID + " = " + goodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCityById(int serverCityId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_SERVER_CITY_ID + " as " + _ID + " , * "
                    + " FROM " + TABLE_CITIES
                    + " where " + COL_SERVER_CITY_ID + " = " + serverCityId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getGoodByServerGoodId(int serverGoodId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + TABLE_GOODS + ".*"
                    + " FROM " + TABLE_GOODS + ", " + TABLE_CATEGORIES
                    + " WHERE  " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_CATEGORIES + "." + COL_USERID + "=" + getCurrentUser().getUserId()
                    + " AND " + TABLE_GOODS + "." + COL_SERVER_GOOD_ID + " = " + serverGoodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getGoodsByCategory(int categoryId, String searchGoodName) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            String sql = "SELECT " + TABLE_GOODS + "." + COL_GOOD_ID + " as " + _ID + " , "
                    + TABLE_GOODS + ".*, " + TABLE_CATEGORIES + "." + COL_SERVER_CATEGORY_ID
                    + " FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_CATEGORY_ID + "=" + categoryId
                    + " AND " + TABLE_GOODS + "." + COL_GOOD_NAME + " LIKE '%" + searchGoodName + "%'"
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1"
                    + " ORDER BY "  + TABLE_GOODS + "." + COL_IS_TO_BUY + " DESC, "
                                    + TABLE_GOODS + "." + COL_USES_NUMBER + " DESC, "
                                    + TABLE_GOODS + "." + COL_GOOD_NAME + " ASC";

            res = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getGoodsToOrderByServerCategory(int serverCategoryId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS + "." + COL_GOOD_ID + " as " + _ID + ","
                    + TABLE_GOODS + ".* ," + TABLE_CATEGORIES + "." + COL_SERVER_CATEGORY_ID
                    + " FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_CATEGORIES + "." + COL_SERVER_CATEGORY_ID + " = " + serverCategoryId
                    + " AND " + TABLE_GOODS + "." + COL_IS_TO_BUY + " = 1 "
                    + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> -1"
                    + " AND " + TABLE_GOODS + "." + COL_IS_ORDERED + " = 0"
                    + " ORDER BY " + TABLE_GOODS + "." + COL_GOOD_NAME, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getExcludedGoodsFromSync() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS + "." + COL_GOOD_ID + " as " + _ID + " , " + TABLE_GOODS + ".*"
                    + " FROM " + TABLE_GOODS + " , " + TABLE_CATEGORIES
                    + " WHERE " + TABLE_GOODS + "." + COL_CATEGORY_ID + "=" + TABLE_CATEGORIES + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_SERVER_GOOD_ID + " <> 0"
                    + " AND " + TABLE_CATEGORIES + "." + COL_USERID + "=" + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getUpdatedGoods() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS + "." + COL_GOOD_ID + " as " + _ID + ","
                    + TABLE_GOODS + ".* ," + TABLE_CATEGORIES + "." + COL_SERVER_CATEGORY_ID
                    + "  FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_SERVER_GOOD_ID + " <> 0"
                    + " AND " + TABLE_GOODS + "." + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " AND " + TABLE_CATEGORIES + "." + COL_USERID + "=" + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getNotSyncGoods() {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS + "." + COL_GOOD_ID + " as " + _ID + ","
                    + TABLE_GOODS + ".* ," + TABLE_CATEGORIES + "." + COL_SERVER_CATEGORY_ID
                    + " FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " AND " + TABLE_GOODS + "." + COL_SERVER_GOOD_ID + " = 0 "
                    + " AND " + TABLE_CATEGORIES + "." + COL_USERID + "=" + getCurrentUser().getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    int getGoodsToBuyNb() {
        String countQuery = "SELECT * "
                + " FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                + " WHERE " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                + " AND " + TABLE_GOODS + "." + COL_CRUD_STATUS + " <> " + DELETED
                + " AND " + TABLE_GOODS + "." + COL_IS_TO_BUY + " = " + 1
                + " AND " + TABLE_GOODS + "." + COL_IS_ORDERED + " = " + 0
                + " AND " + TABLE_CATEGORIES + "." + COL_USERID + "=" + getCurrentUser().getUserId();

        db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", e.toString());
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return cnt;
    }


    // UPDATE QUERIES

    //INVITATIONS
    boolean updateReceivedInvitation(ReceivedInvitation invitation) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SENDER_PHONE, invitation.getInvitationPhone());
        contentValues.put(COL_INVITATION_RESPONSE, invitation.getResponse());
        contentValues.put(COL_USERID, getCurrentUser().getUserId());
        contentValues.put(COL_SERVER_CO_USER_ID, invitation.getServerCoUserId());
        contentValues.put(COL_SERVER_GROUP_ID, invitation.getServerGroupId());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_RECEIVED_INVITATIONS, contentValues,
                    COL_RECEIVED_INVITATION_ID + " = " + invitation.getReceivedInvitationId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }
    // GOODS

    boolean updateGood(Good good) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GOOD_NAME, good.getGoodName());
        contentValues.put(COL_CATEGORY_ID, good.getCategoryId());
        contentValues.put(COL_QUANTITY_LEVEL, good.getQuantityLevelId());
        contentValues.put(COL_IS_TO_BUY, good.isToBuy());
        contentValues.put(COL_SYNC_STATUS, good.getSync());
        contentValues.put(COL_CRUD_STATUS, good.getCrudStatus());
        contentValues.put(COL_SERVER_GOOD_ID, good.getServerGoodId());
        contentValues.put(COL_GOOD_DESC, good.getGoodDesc());
        contentValues.put(COL_IS_ORDERED, good.getIsOrdered());
        contentValues.put(COL_USES_NUMBER, good.getUsesNumber());

        int affectedRows = 0;
        db.beginTransaction();
        try {
            affectedRows = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + good.getGoodId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return (affectedRows == 1);
    }

    //CATEGORIES

    boolean updateCategoryName(Category category) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(COL_SYNC_STATUS, DataBaseHelper.SYNC_STATUS_FAILED);

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_CATEGORIES, contentValues, COL_CATEGORY_ID + " = " + category.getCategoryId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    boolean updateCategory(Category category) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(COL_CATEGORY_COLOR, category.getColor());
        contentValues.put(COL_CATEGORY_ICON, category.getIcon());
        contentValues.put(COL_CATEGORY_ICON, category.getIcon());
        contentValues.put(COL_SYNC_STATUS, category.getSync());
        contentValues.put(COL_USERID, category.getUserId());
        contentValues.put(COL_CRUD_STATUS, category.getCrudStatus());
        contentValues.put(COL_SERVER_CATEGORY_ID, category.getServerCategoryId());
        contentValues.put(COL_D_CATEGORY_ID, category.getDCategoryId());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_CATEGORIES, contentValues, COL_CATEGORY_ID + " = " + category.getCategoryId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }


    //CO_USERS

    boolean updateCoUserAfterSync(int coUserId, int syncStatus, int serverCoUserId) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SYNC_STATUS, syncStatus);
        contentValues.put(COL_SERVER_CO_USER_ID, serverCoUserId);
        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_CO_USERS, contentValues, COL_CO_USER_ID + " = " + coUserId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    boolean updateCoUser(CoUser coUser) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_CO_USER_EMAIL, coUser.getCoUserEmail());
        contentValues.put(COL_USERID, coUser.getUserId());
        contentValues.put(COL_EMAIL, coUser.getEmail());
        contentValues.put(COL_CONFIRMATION_STATUS, coUser.getConfirmationStatus());
        contentValues.put(COL_HAS_RESPONDED, coUser.getHasResponded());
        contentValues.put(COL_SYNC_STATUS, coUser.getSyncStatus());
        contentValues.put(COL_SERVER_CO_USER_ID, coUser.getServerCoUserId());
        contentValues.put(COL_CO_USER_PHONE, coUser.getCoUserPhone());
        contentValues.put(COL_SERVER_USER_ID, coUser.getServerUserId());

        int affectedRows = 0;
        db.beginTransaction();
        try {
            affectedRows = db.update(TABLE_CO_USERS, contentValues, COL_CO_USER_ID + " = " + coUser.getCoUserId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return (affectedRows == 1);
    }


    //DELETE QUERIES

    // GOODS
    Boolean deleteGoodById(int goodId) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CRUD_STATUS, DELETED);
        contentValues.put(COL_SYNC_STATUS, SYNC_STATUS_FAILED);


        db.beginTransaction();
        int affectedRowsInGoods;
        boolean res = false;
        try {
            affectedRowsInGoods = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + goodId, null);

            if (affectedRowsInGoods == 1) {
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        } finally {
            if (res) db.setTransactionSuccessful();
            db.endTransaction();
        }
        return res;
    }

    //CATEGORIES

    boolean deleteCategory(int categoryId) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CRUD_STATUS, DELETED);
        contentValues.put(COL_SYNC_STATUS, SYNC_STATUS_FAILED);


        db.beginTransaction();
        int affectedRowsInCategories;
        int affectedRowsInGoods;
        boolean res = false;
        try {
            affectedRowsInCategories = db.update(TABLE_CATEGORIES, contentValues, COL_CATEGORY_ID + " = " + categoryId, null);
            affectedRowsInGoods = db.update(TABLE_GOODS, contentValues, COL_CATEGORY_ID + " = " + categoryId, null);

            if (affectedRowsInCategories + affectedRowsInGoods > 0) {
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        } finally {
            if (res) db.setTransactionSuccessful();
            db.endTransaction();
        }
        return res;
    }


    boolean deleteAllUserCategoriesAndGoods() {
        db = this.getWritableDatabase();
        db.beginTransaction();
        boolean res = false;
        try {

            db.execSQL("DELETE FROM " + TABLE_GOODS
                    + " WHERE EXISTS "
                    + "( SELECT * "
                    + " FROM " + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_CATEGORIES + "." + COL_USERID + "=" + getCurrentUser().getUserId() + ")");


            db.execSQL("DELETE FROM " + TABLE_CATEGORIES
                    + " WHERE " + COL_USERID + "=" + getCurrentUser().getUserId());

            res = true;

        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        } finally {
            if (res) db.setTransactionSuccessful();
            db.endTransaction();
        }
        return res;
    }

    //CREATE QUERIES

    //DEFAULT CATEGORIES
    boolean insertDefaultCategory(DefaultCategory defaultCategory) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_D_CATEGORY_ID, defaultCategory.getDCategoryId());
        contentValues.put(COL_D_CATEGORY_NAME, defaultCategory.getDCategoryName());
        contentValues.put(COL_SERVER_SHOP_ID, defaultCategory.getServerShopId());

        long result = db.insertWithOnConflict(TABLE_DEFAULT_CATEGORIES, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }

    boolean deleteAllDefaultCategories() {
        db = this.getWritableDatabase();
        db.beginTransaction();
        boolean res = false;
        try {

            db.execSQL("DELETE FROM " + TABLE_DEFAULT_CATEGORIES
                    + " WHERE 1 ");

            res = true;

        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        } finally {
            if (res) db.setTransactionSuccessful();
            db.endTransaction();
        }
        return res;
    }



    //ORDERS

    boolean insertOrder(Order order) {

        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SERVER_ORDER_ID, order.getServerOrderId());
        contentValues.put(COL_SERVER_USER_ID, order.getUser().getServerUserId());
        contentValues.put(COL_SERVER_SHOP_ID, order.getShop().getServerShopId());
        contentValues.put(COL_CREATION_DATE, UtilsFunctions.dateToString(order.getCreationDate(), "yyyy-MM-dd HH:mm:ss"));
        contentValues.put(COL_ORDER_STATUS_ID, order.getStatusId());
        contentValues.put(COL_ORDER_STATUS_NAME, order.getStatusName());
        contentValues.put(COL_ORDERED_GOODS_NUMBER, order.getOrderedGoodsNumber());
        contentValues.put(COL_START_TIME, order.getStartTime());
        contentValues.put(COL_END_TIME, order.getEndTime());
        contentValues.put(COL_IS_TO_DELIVER, order.getIsToDeliver());
        contentValues.put(COL_USER_ADDRESS, order.getUserAddress());
        contentValues.put(COL_USER_LOCATION, order.getUserLocation());
        contentValues.put(COL_ORDER_PRICE, order.getOrderPrice());

        long result = db.insertWithOnConflict(TABLE_ORDERS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }

    //SHOPS

    boolean insertShop(Shop shop) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SERVER_SHOP_ID, shop.getServerShopId());
        contentValues.put(COL_SHOP_PHONE, shop.getShopPhone());
        contentValues.put(COL_SHOP_NAME, shop.getShopName());
        contentValues.put(COL_OPENING_TIME, shop.getOpeningTime());
        contentValues.put(COL_CLOSING_TIME, shop.getClosingTime());
        contentValues.put(COL_SERVER_SHOP_TYPE_ID, shop.getShopType().getServerShopTypeId());
        contentValues.put(COL_SHOP_TYPE_NAME, shop.getShopType().getShopTypeName());
        contentValues.put(COL_VISIBILITY, shop.getVisibility());
        contentValues.put(COL_SERVER_COUNTRY_ID, shop.getCountry().getServerCountryId());
        contentValues.put(COL_COUNTRY_NAME, shop.getCountry().getCountryName());
        contentValues.put(COL_SERVER_CITY_ID, shop.getCity().getServerCityId());
        contentValues.put(COL_CITY_NAME, shop.getCity().getCityName());
        contentValues.put(COL_SHOP_ADRESS, shop.getShopAdress());
        contentValues.put(COL_SHOP_EMAIL, shop.getShopEmail());
        contentValues.put(COL_LONGITUDE, shop.getLongitude());
        contentValues.put(COL_LATITUDE, shop.getLatitude());
        contentValues.put(COL_IS_DELIVERING, shop.getIsDelivering());
        contentValues.put(COL_DELIVERY_DELAY, shop.getDeliveryDelay());
        contentValues.put(COL_FACEBOOK_URL, shop.getFacebookUrl());
        contentValues.put(COL_WEBSITE_URL, shop.getWebsiteUrl());

        long result = db.insertWithOnConflict(TABLE_SHOPS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }

    //WEEKDAYSOFF
    boolean insertWeekDayOff(WeekDayOff weekDayOff) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DAY_OFF_ID, weekDayOff.getDayOffId());
        contentValues.put(COL_DAY_OFF, weekDayOff.getDayOff());
        contentValues.put(COL_SERVER_SHOP_ID, weekDayOff.getServerShopId());

        long result = db.insertWithOnConflict(TABLE_WEEK_DAYS_OFF, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }

    //DATEOFF
    boolean insertDateOff(DateOff dateOff) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE_OFF_ID, dateOff.getDateOffId());
        contentValues.put(COL_DATE_OFF, dateOff.getDateOffValue());
        contentValues.put(COL_DATE_OFF_DESC, dateOff.getDateOffDesc());
        contentValues.put(COL_SERVER_SHOP_ID, dateOff.getServerShopId());

        long result = db.insertWithOnConflict(TABLE_DATES_OFF, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }

    //DESCRIPTIONS
    boolean insertDesc(Description desc) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DESC_ID, desc.getDescId());
        contentValues.put(COL_DESC_VALUE, desc.getDescValue());
        contentValues.put(COL_D_CATEGORY_ID, desc.getdCategoryId());

        long result = db.insert(TABLE_DESCRIPTIONS, null, contentValues);
        return (result != -1);
    }

    //USER LOCATIONS
    boolean insertUserLocation(UserLocation userLocation) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_LOCATION_ID, userLocation.getUserLocationId());
        contentValues.put(COL_USER_ADDRESS, userLocation.getUserAddress());
        contentValues.put(COL_USER_GPS_LOCATION, userLocation.getUserGpsLocation());
        contentValues.put(COL_SERVER_USER_ID, userLocation.getServerUserId());

        long result =db.insertWithOnConflict(TABLE_USER_LOCATIONS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }

    //CITIES
    boolean insertCity(City city) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SERVER_CITY_ID, city.getServerCityId());
        contentValues.put(COL_CITY_NAME, city.getCityName());
        contentValues.put(COL_SERVER_COUNTRY_ID, city.getCountry().getServerCountryId());
        contentValues.put(COL_LONGITUDE, city.getLongitude());
        contentValues.put(COL_LATITUDE, city.getLatitude());

        long result =db.insertWithOnConflict(TABLE_CITIES, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return (result != -1);
    }

    //DESCRIPTIONS
    Cursor getUserLocations(int serverUserId) {
        db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_USER_LOCATION_ID + " as " + _ID + " , *"
                    + " from " + TABLE_USER_LOCATIONS
                    + " where " + COL_SERVER_USER_ID + " = " + serverUserId, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //AMOUNTS
    boolean insertAmount(Amount amount) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_AMOUNT_ID, amount.getAmountId());
        contentValues.put(COL_AMOUNT_VALUE, amount.getAmountValue());
        contentValues.put(COL_AMOUNT_TYPE_ID, amount.getAmountTypeId());
        contentValues.put(COL_AMOUNT_TYPE_NAME, amount.getAmountTypeName());

        long result = db.insert(TABLE_AMOUNTS, null, contentValues);
        return (result != -1);
    }

    //GROUPS

    boolean addGroup(Group group) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GROUP_NAME, group.getGroupName());
        contentValues.put(COL_OWNER_EMAIL, group.getOwnerEmail());
        contentValues.put(COL_SERVER_OWNER_ID, group.getServerOwnerId());
        contentValues.put(COL_SERVER_GROUP_ID, group.getServerGroupId());
        contentValues.put(COL_SYNC_STATUS, group.getSyncStatus());

        long result = db.insert(TABLE_GROUPS, null, contentValues);
        return (result != -1);
    }


    //INVITATIONS
    boolean addReceivedInvitation(ReceivedInvitation invitation) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SENDER_PHONE, invitation.getInvitationPhone());
        contentValues.put(COL_INVITATION_RESPONSE, invitation.getResponse());
        contentValues.put(COL_USERID, getCurrentUser().getUserId());
        contentValues.put(COL_SERVER_CO_USER_ID, invitation.getServerCoUserId());
        contentValues.put(COL_SERVER_GROUP_ID, invitation.getServerGroupId());

        long result = db.insert(TABLE_RECEIVED_INVITATIONS, null, contentValues);
        return (result != -1);
    }

    //CATEGORIES

    boolean addCategory(Category category) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(COL_CATEGORY_COLOR, category.getColor());
        contentValues.put(COL_CATEGORY_ICON, category.getIcon());
        contentValues.put(COL_SYNC_STATUS, category.getSync());
        contentValues.put(COL_EMAIL, category.getEmail());
        contentValues.put(COL_USERID, category.getUserId());
        contentValues.put(COL_CRUD_STATUS, category.getCrudStatus());
        contentValues.put(COL_SERVER_CATEGORY_ID, category.getServerCategoryId());
        contentValues.put(COL_D_CATEGORY_ID, category.getDCategoryId());

        long result = db.insert(TABLE_CATEGORIES, null, contentValues);
        return (result != -1);
    }

    //UPDATES

    //DESCRIPTIONS
    boolean updateDesc(Description desc) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DESC_ID, desc.getDescId());
        contentValues.put(COL_DESC_VALUE, desc.getDescValue());
        contentValues.put(COL_D_CATEGORY_ID, desc.getdCategoryId());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_DESCRIPTIONS, contentValues, COL_DESC_ID + " = " + desc.getDescId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    //AMOUNTS
    boolean updateAmount(Amount amount) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_AMOUNT_ID, amount.getAmountId());
        contentValues.put(COL_AMOUNT_VALUE, amount.getAmountValue());
        contentValues.put(COL_AMOUNT_TYPE_ID, amount.getAmountTypeId());
        contentValues.put(COL_AMOUNT_TYPE_NAME, amount.getAmountTypeName());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_AMOUNTS, contentValues, COL_AMOUNT_ID + " = " + amount.getAmountId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    //GROUPS

    boolean updateGroup(Group group) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GROUP_NAME, group.getGroupName());
        contentValues.put(COL_SERVER_GROUP_ID, group.getServerGroupId());
        contentValues.put(COL_SERVER_OWNER_ID, group.getServerOwnerId());
        contentValues.put(COL_SYNC_STATUS, group.getSyncStatus());
        contentValues.put(COL_OWNER_EMAIL, group.getOwnerEmail());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_GROUPS, contentValues, COL_GROUP_ID + " = " + group.getGroupId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    //USERS

    boolean addUser(User user) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_USERNAME, user.getUserName());
        contentValues.put(COL_SERVER_USER_ID, user.getServerUserId());
        contentValues.put(COL_SIGN_UP_TYPE, user.getSignUpType());
        contentValues.put(COL_LAST_LOGGED_IN, user.getLastLoggedIn());
        contentValues.put(COL_USER_PHONE, user.getUserPhone());
        contentValues.put(COL_SERVER_CITY_ID, user.getServerCityId());

        if (user.getServerGroupId() != 0)
            contentValues.put(COL_SERVER_GROUP_ID, user.getServerGroupId());

        long result = db.insert(TABLE_USERS, null, contentValues);
        return (result != -1);
    }

    boolean updateUser(User user) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_USERNAME, user.getUserName());
        contentValues.put(COL_SERVER_USER_ID, user.getServerUserId());
        contentValues.put(COL_SERVER_GROUP_ID, user.getServerGroupId());
        contentValues.put(COL_LAST_LOGGED_IN, user.getLastLoggedIn());
        contentValues.put(COL_GROUP_ID, user.getGroupId());
        contentValues.put(COL_SERVER_CITY_ID, user.getServerCityId());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_USERS, contentValues, COL_USERID + " = " + user.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }


    boolean updateLastLoggedIn(int serverUserId) {
        db = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        ContentValues contentValues2 = new ContentValues();

        contentValues1.put(COL_LAST_LOGGED_IN, IS_NOT_LOGGED_IN);
        contentValues2.put(COL_LAST_LOGGED_IN, IS_LOGGED_IN);

        int affectedRows1 = 0;
        int affectedRows2 = 0;
        try {
            affectedRows1 = db.update(TABLE_USERS, contentValues1, COL_SERVER_USER_ID + " <> " + serverUserId, null);
            affectedRows2 = db.update(TABLE_USERS, contentValues2, COL_SERVER_USER_ID + " = " + serverUserId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows1 + affectedRows2 > 0);
    }

    //GOODS

    boolean addGood(Good good) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GOOD_NAME, good.getGoodName());
        contentValues.put(COL_GOOD_DESC, good.getGoodDesc());
        contentValues.put(COL_CATEGORY_ID, good.getCategoryId());
        contentValues.put(COL_QUANTITY_LEVEL, good.getQuantityLevelId());
        int isToBuyInt = 0;
        if (good.isToBuy()) isToBuyInt = 1;
        contentValues.put(COL_IS_TO_BUY, isToBuyInt);
        contentValues.put(COL_SYNC_STATUS, good.getSync());
        contentValues.put(COL_EMAIL, good.getEmail());
        contentValues.put(COL_CRUD_STATUS, good.getCrudStatus());
        contentValues.put(COL_SERVER_GOOD_ID, good.getServerGoodId());
        contentValues.put(COL_IS_ORDERED, good.getIsOrdered());
        contentValues.put(COL_USES_NUMBER, good.getUsesNumber());
        contentValues.put(COL_PRICE_ID, good.getPriceId());

        long result = db.insert(TABLE_GOODS, null, contentValues);
        return (result != -1);
    }

    boolean restoreGood(Good good) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CRUD_STATUS, RESTORED);
        contentValues.put(COL_SYNC_STATUS, SYNC_STATUS_FAILED);

        db.beginTransaction();
        int affectedRowsInGoods;
        boolean res = false;
        try {
            affectedRowsInGoods = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + good.getGoodId(), null);

            if (affectedRowsInGoods == 1) {
                res = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        } finally {
            if (res) db.setTransactionSuccessful();
            db.endTransaction();
        }
        return res;
    }

    // CO_USERS

    boolean addCoUser(CoUser coUser) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_CO_USER_EMAIL, coUser.getCoUserEmail());
        contentValues.put(COL_USERID, coUser.getUserId());
        contentValues.put(COL_EMAIL, coUser.getEmail());
        contentValues.put(COL_CONFIRMATION_STATUS, coUser.getConfirmationStatus());
        contentValues.put(COL_HAS_RESPONDED, coUser.getHasResponded());
        contentValues.put(COL_SYNC_STATUS, coUser.getSyncStatus());
        contentValues.put(COL_SERVER_CO_USER_ID, coUser.getServerCoUserId());
        contentValues.put(COL_CO_USER_PHONE, coUser.getCoUserPhone());
        contentValues.put(COL_SERVER_USER_ID, coUser.getServerUserId());

        long result = db.insert(TABLE_CO_USERS, null, contentValues);
        return (result != -1);
    }


}
