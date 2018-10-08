package com.winservices.wingoods.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.models.User;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String COL_CATEGORY_NAME = "category_name";
    public static final String _ID = "_id";
    static final int SYNC_STATUS_OK = 1;
    public static final int SYNC_STATUS_FAILED = 0;
    public static final int IS_LOGGED_IN = 1;
    public static final int IS_NOT_LOGGED_IN = 0;

    private static final int DELETED = -1;
    private static final int RESTORED = 1;

    //sign up type
    public static final String GOOGLE = "google";
    public static final String FACEBOOK = "facebook";
    public static final String LISTA = "lista";

    //Lista DEV (compte omar.haji@gmail.com)
    private static final String HOST = "http://lista.onlinewebshop.net/webservices/";

    //Lista ALPHA (compte karimamrani0909@gmail.com)
    //private static final String HOST = "http://lista-alpha.onlinewebshop.net/webservices/";


    static final String COL_EMAIL = "email";
    static final String COL_USERID = "user_id";
    static final String COL_SYNC_STATUS = "sync_status";
    static final String COL_CATEGORY_COLOR = "category_color";
    static final String COL_CATEGORY_ICON = "category_icon";
    static final String COL_CRUD_STATUS = "crud_status";

    private static final String TABLE_CO_USERS = "co_users";
    static final String COL_CONFIRMATION_STATUS = "confirmation_status";
    static final String COL_CO_USER_ID = "co_user_id";
    static final String COL_CO_USER_EMAIL = "co_user_email";
    static final String COL_SERVER_CO_USER_ID = "server_co_user_id";
    private static final String COL_LAST_LOGGED_IN = "last_logged_in";

    private static final String TABLE_GROUPS = "groups";

    private static final String COL_GROUP_ID = "group_id";
    private static final String COL_GROUP_NAME = "group_name";
    private static final String COL_OWNER_EMAIL = "owner_email";
    private static final String COL_SERVER_OWNER_ID = "server_owner_id";
    static final String COL_SERVER_GROUP_ID = "server_group_id";
    static final String COL_SERVER_CATEGORY_ID = "server_category_id";
    private static final String COL_SERVER_USER_ID = "server_user_id";
    static final String COL_SERVER_GOOD_ID = "server_good_id";
    static final String COL_HAS_RESPONDED = "has_responded";
    static final String COL_SENDER_EMAIL = "sender_email";
    static final String COL_INVITATION_RESPONSE = "invitation_response";
    private static final String COL_RECEIVED_INVITATION_ID = "received_invitation_id";
    private static final String COL_SIGN_UP_TYPE = "sign_up_type";
    static final String COL_CATEGORY_ID = "category_id";
    static final String COL_GOOD_NAME = "good_name";
    static final String COL_GOOD_DESC = "good_desc";
    static final String COL_QUANTITY_LEVEL = "quantity_level";
    static final String COL_IS_TO_BUY = "is_to_buy";
    static final String RED_GOODS = "red_goods";
    static final String ORANGE_GOODS = "orange_goods";
    static final String GREEN_GOODS = "green_goods";
    private static final String DATABASE_NAME = "DB_WinGoods.sqlite";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_GOODS = "goods";
    private static final String COL_GOOD_ID = "good_id";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_RECEIVED_INVITATIONS = "received_invitations";
    private static final String COL_PASSWORD = "password";
    private static final String COL_USERNAME = "user_name";
    public static final String COL_IS_ORDERED = "is_ordered";
    public static final String HOST_URL_ADD_USER = HOST + "registerUser.php";
    public static final String HOST_URL_LOGIN_USER = HOST + "loginUser.php";
    static final String HOST_URL_ADD_CO_USER = HOST + "addCoUser.php";
    public static final String HOST_URL_GET_INVITATIONS = HOST + "getInvitations.php";
    static final String HOST_URL_UPDATE_CO_USER_RESPONSE = HOST + "updateCoUserResponse.php";
    static final String HOST_URL_ADD_GROUP = HOST + "addGroup.php";
    static final String HOST_URL_GET_GROUP_DATA = HOST + "getGroupData.php";
    static final String HOST_URL_DELETE_USER_CATEGORIES_AND_GOODS = HOST + "deleteAllUserCategoriesAndGoods.php";
    static final String HOST_URL_UPDATE_CATEGORIES_AND_GOODS = HOST + "updateCategoriesAndGoods.php";
    static final String HOST_URL_UPDATE_CATEGORIES_AND_GOODS_FROM_SERVER = HOST + "updateCategoriesAndGoodsFromServer.php";
    public static final String HOST_URL_GET_TEAM_MEMBERS = HOST + "getTeamMembers.php";
    static final String HOST_URL_ADD_CATEGORIES = HOST + "addCategories.php";
    static final String HOST_URL_ADD_GOODS = HOST + "addGoods.php";
    public static final String HOST_URL_GET_SHOPS = HOST + "getShops.php";
    public static final String HOST_URL_GET_CITIES = HOST + "getCities.php";
    public static final String HOST_URL_ADD_ORDER = HOST + "addOrder.php";

    private final static int DATABASE_VERSION = 1;

    private User currentUser;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        currentUser = getCurrentUser();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users ( " +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "password TEXT, " +
                "user_name INTEGER, " +
                "server_user_id INTEGER," +
                "server_group_id INTEGER," +
                "sign_up_type TEXT," +
                "last_logged_in INTEGER," +
                "group_id INTEGER" +
                ")");

        db.execSQL("CREATE TABLE groups ( " +
                "group_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "group_name TEXT," +
                "owner_email TEXT," +
                "server_owner_id INTEGER," +
                "server_group_id INTEGER," +
                "sync_status INTEGER " +
                ")");

        db.execSQL("CREATE TABLE co_users (" +
                "co_user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "co_user_email TEXT, " +
                "user_id INTEGER, " +
                "email INTEGER, " +
                "confirmation_status INTEGER, " +
                "has_responded INTEGER, " +
                "server_co_user_id INTEGER, " +
                "sync_status INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users (user_id)) ");

        db.execSQL("CREATE TABLE categories ( " +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_name TEXT, " +
                "category_color INTEGER, " +
                "category_icon INTEGER, " +
                "sync_status INTEGER, " +
                "user_id INTEGER, " +
                "email TEXT, " +
                "crud_status INTEGER, " +
                "server_category_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users (user_id)) ");

        db.execSQL("CREATE TABLE goods (" +
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
                "FOREIGN KEY (category_id) REFERENCES categories (category_id)) ");

        db.execSQL("CREATE TABLE received_invitations ( " +
                "received_invitation_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_email TEXT, " +
                "invitation_response INTEGER, " +
                "user_id INTEGER, " +
                "server_group_id INTEGER, " +
                "server_co_user_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users (user_id)) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();

        db.execSQL("DROP TABLE IF EXISTS goods ");
        db.execSQL("DROP TABLE IF EXISTS categories ");
        db.execSQL("DROP TABLE IF EXISTS co_users ");
        db.execSQL("DROP TABLE IF EXISTS received_invitations ");
        db.execSQL("DROP TABLE IF EXISTS users ");
        db.execSQL("DROP TABLE IF EXISTS groups ");

        onCreate(db);
    }



    //SELECT QUERIES

    //GROUPS

    Group getGroupByUserId(int serverUserId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        Group group = null;
        try {
            String sql = "select "+TABLE_GROUPS+"." + COL_GROUP_ID + " as " + _ID + " , "+TABLE_GROUPS+".* "
                    + " from " + TABLE_GROUPS + ", " + TABLE_USERS
                    + " WHERE " + TABLE_GROUPS+"."+COL_GROUP_ID+" = "+ TABLE_USERS+"."+COL_GROUP_ID
                    + " AND " + TABLE_USERS+"."+COL_SERVER_USER_ID + " = " + serverUserId;


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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        Group group = null;
        try {

            String sql = "select " + COL_GROUP_ID + " as " + _ID + " , * "
                    + " from " + TABLE_GROUPS
                    + " WHERE " + TABLE_GROUPS+"."+COL_SERVER_OWNER_ID+" = "+serverOwnerID;

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

    boolean invitationExists(String senderEmail) {
        String countQuery = "select * from " + TABLE_RECEIVED_INVITATIONS
                + " where "
                + COL_SENDER_EMAIL + " = '" + senderEmail + "'"
                + "AND " + COL_USERID+"="+currentUser.getUserId();

        SQLiteDatabase db = this.getReadableDatabase();
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


    ReceivedInvitation getReceivedInvitation(String senderEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        ReceivedInvitation invitation = null;
        try {
            res = db.rawQuery("SELECT " + COL_RECEIVED_INVITATION_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_RECEIVED_INVITATIONS
                    + " WHERE " + COL_SENDER_EMAIL + " = '" + senderEmail + "'"
                    + " AND " + COL_USERID+"="+currentUser.getUserId(), null);

            while (res.moveToNext()) {
                int receivedInvitationID = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                int userId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
                int invitationResponse = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_INVITATION_RESPONSE));
                int serverCoUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CO_USER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));

                invitation = new ReceivedInvitation(receivedInvitationID, senderEmail, invitationResponse, userId);
                invitation.setServerCoUserId(serverCoUserId);
                invitation.setServerGroupeId(serverGroupId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return invitation;
    }

    Cursor getNotSyncResponses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            String sql = "select " + COL_RECEIVED_INVITATION_ID + " as " + _ID + " , * "
                    + " from " + TABLE_RECEIVED_INVITATIONS
                    + " where " + COL_INVITATION_RESPONSE + " <> " + CoUser.COMPLETED
                    + " and " + COL_USERID +"="+ currentUser.getUserId();
            res = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //CO USERS

    Cursor getNotSyncCoUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CO_USER_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CO_USERS
                    + " where " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_USERID+"="+currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    boolean coUserExists(String coUserEmail, String email) {
        String countQuery = "select * from " + TABLE_CO_USERS
                + " where "
                + COL_EMAIL + " = '" + email + "'"
                + " AND " + COL_CO_USER_EMAIL + " = '" + coUserEmail + "'";

        SQLiteDatabase db = this.getReadableDatabase();
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



    //USERS



    User getUserByEmail(String email, String signUpType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        User user = null;
        try {
            res = db.rawQuery("SELECT " + COL_USERID + " as " + _ID + " , *"
                    + " FROM " + TABLE_USERS
                    + " where " + COL_EMAIL + " = '" + email +"'"
                    + " AND " + COL_SIGN_UP_TYPE + " = '" + signUpType +"'", null);


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
        SQLiteDatabase db = this.getReadableDatabase();
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
                + " where " +  COL_EMAIL + " = '" + user.getEmail() + "'"
                + " AND " + COL_SIGN_UP_TYPE + " = '" + user.getSignUpType() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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

                currentUser = new User(userId, email, password, userName);
                currentUser.setServerUserId(serverUserId);
                currentUser.setServerGroupId(serverGroupId);
                currentUser.setSignUpType(signUpType);
                currentUser.setLastLoggedIn(lastLoggedIn);
                currentUser.setGroupId(groupId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentUser;
    }

    //CATEGORIES


    int getGoodsToBuyNumber(int categoryId) {
        String countQuery = "SELECT * FROM " + TABLE_GOODS
                + " WHERE " +  COL_CATEGORY_ID+ " = " + categoryId
                + " AND " +  COL_IS_TO_BUY + " = 1 "
                + " AND " + COL_CRUD_STATUS + " <> " + DELETED;
        SQLiteDatabase db = this.getReadableDatabase();
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
        return cnt ;
    }

    Cursor getCategoriesByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , " + COL_CATEGORY_NAME
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CATEGORY_NAME + " LIKE '%" + name + "%' "
                    + " and " + COL_CRUD_STATUS + " <> -1" +
                    " and " + COL_USERID + " = " + currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoryByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CATEGORY_NAME + " = '" + name.replaceAll("'", "''") + "' "
                    + " and " + COL_USERID + " = " + currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoryByServerCategoryIdAndUserId(int serverCategoryId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
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


    Cursor getCategoryByCrud(int crudStatus) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CRUD_STATUS + " = " + crudStatus+
                    " and " + COL_USERID + " = " + currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = "+ currentUser.getUserId()
                    + " and " + COL_CRUD_STATUS + " <> -1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //return categories that are already synchronized with server (have a server category id)
    Cursor getExcludedCategoriesFromSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = " + currentUser.getUserId()
                    + " and " + COL_SERVER_CATEGORY_ID + " <> 0"
                    + " and " + COL_USERID + " = " + currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //return categories that have been updated on mobile user (but already have been synchronized with server)
    Cursor getUpdatedCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = " + currentUser.getUserId()
                    + " and " + COL_SERVER_CATEGORY_ID + " <> 0"
                    + " and " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_USERID +"="+currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }



    Cursor getAllCategoriesOverview() {
        SQLiteDatabase db = this.getReadableDatabase();
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
                    + " and " + COL_USERID + " = " + currentUser.getUserId() , null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_CATEGORIES
                    + " where " + COL_CATEGORY_ID + " = " + categoryId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getCategoryByGoodId(int goodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_CATEGORIES+"."+COL_CATEGORY_ID + " as " + _ID + " , "+TABLE_CATEGORIES+"."+"*"
                    + " FROM " + TABLE_CATEGORIES + " , " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES+"."+COL_CATEGORY_ID + " = " + TABLE_GOODS+"."+COL_CATEGORY_ID
                    + " and " + TABLE_GOODS+"."+COL_GOOD_ID + " = " + goodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getPurchasableCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * from " + TABLE_CATEGORIES +
                    " where exists" +
                    " ( select * from goods where" +
                    " categories.category_id = goods.category_id" +
                    " and goods." + COL_IS_TO_BUY + " = 1" +
                    " and goods." + COL_CRUD_STATUS + " <> -1 )" +
                    " and " + COL_CRUD_STATUS + " <> -1" +
                    " and " + COL_USERID +"="+currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getNotSyncCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_SERVER_CATEGORY_ID + " = 0"
                    + " and " + COL_USERID +"="+currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //GOODS


    Cursor getGoodById(int goodId) {
        SQLiteDatabase db = this.getReadableDatabase();
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


    Cursor getGoodByServerGoodId(int serverGoodId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + TABLE_GOODS + ".*"
                    + " FROM " + TABLE_GOODS + ", " + TABLE_CATEGORIES
                    + " WHERE  " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_CATEGORIES + "."+COL_USERID + "=" + userId
                    + " AND " + TABLE_GOODS +"."+COL_SERVER_GOOD_ID + " = " + serverGoodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getGoodsByCategory(int categoryId, String searchGoodName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + "  * FROM " + TABLE_GOODS
                    + " WHERE " + COL_CATEGORY_ID + "=" + categoryId
                    + " AND " + COL_GOOD_NAME + " LIKE '%" + searchGoodName + "%'"
                    + " AND " + COL_CRUD_STATUS + " <> -1"
                    + " ORDER BY " + COL_IS_TO_BUY + " DESC, " + COL_GOOD_NAME + " ASC", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getGoodsToOrderByServerCategory(int serverCategoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS+"."+COL_GOOD_ID + " as " + _ID + "," + TABLE_GOODS+".*"
                    + " FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES+"."+COL_CATEGORY_ID+"="+TABLE_GOODS+"."+COL_CATEGORY_ID
                    + " AND " + TABLE_CATEGORIES+"."+COL_SERVER_CATEGORY_ID + " = " + serverCategoryId
                    + " AND " + TABLE_GOODS+"."+COL_IS_TO_BUY + " = 1 "
                    + " AND " + TABLE_GOODS+"."+COL_CRUD_STATUS + " <> -1"
                    + " AND " + TABLE_GOODS+"."+COL_IS_ORDERED + " = 0"
                    + " ORDER BY " + TABLE_GOODS+"."+COL_GOOD_NAME , null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getExcludedGoodsFromSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS+"."+COL_GOOD_ID + " as " + _ID + " , " + TABLE_GOODS+".*"
                    + " FROM " + TABLE_GOODS + " , " + TABLE_CATEGORIES
                    + " WHERE " + TABLE_GOODS + "." +COL_CATEGORY_ID + "=" + TABLE_CATEGORIES+"."+COL_CATEGORY_ID
                    + " AND " +TABLE_GOODS+"."+COL_SERVER_GOOD_ID + " <> 0"
                    + " AND " + TABLE_CATEGORIES+"."+COL_USERID+"="+currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getUpdatedGoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS+"."+COL_GOOD_ID + " as " + _ID + "," + TABLE_GOODS+".*"
                    + "  FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES+"."+COL_CATEGORY_ID+"="+TABLE_GOODS+"."+COL_CATEGORY_ID
                    + " AND " +TABLE_GOODS+"."+COL_SERVER_GOOD_ID + " <> 0"
                    + " AND " +TABLE_GOODS+"."+COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " AND " +TABLE_CATEGORIES+"."+COL_USERID+"="+currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getNotSyncGoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + TABLE_GOODS+"."+COL_GOOD_ID + " as " + _ID + "," + TABLE_GOODS+".*"
                    + " FROM " + TABLE_GOODS + "," + TABLE_CATEGORIES
                    + " WHERE " + TABLE_CATEGORIES+"."+COL_CATEGORY_ID+"="+TABLE_GOODS+"."+COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS+"."+COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " AND " + TABLE_GOODS+"."+COL_SERVER_GOOD_ID + " = 0 "
                    + " AND " + TABLE_CATEGORIES+"."+COL_USERID+"="+currentUser.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    // UPDATE QUERIES

    //INVITATIONS
    boolean updateReceivedInvitation(ReceivedInvitation invitation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_INVITATION_RESPONSE, invitation.getResponse());

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
        SQLiteDatabase db = this.getWritableDatabase();
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

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + good.getGoodId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    //CATEGORIES

    boolean updateCategoryName(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(COL_CATEGORY_COLOR, category.getColor());
        contentValues.put(COL_CATEGORY_ICON, category.getIcon());
        contentValues.put(COL_CATEGORY_ICON, category.getIcon());
        contentValues.put(COL_SYNC_STATUS, category.getSync());
        contentValues.put(COL_USERID, category.getUserId());
        contentValues.put(COL_CRUD_STATUS, category.getCrudStatus());
        contentValues.put(COL_SERVER_CATEGORY_ID, category.getServerCategoryId());

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
        SQLiteDatabase db = this.getWritableDatabase();
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


    //DELETE QUERIES

    // GOODS
    Boolean deleteGoodById(int goodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CRUD_STATUS, DELETED);
        contentValues.put(COL_SYNC_STATUS, SYNC_STATUS_FAILED);


        db.beginTransaction();
        int affectedRowsInGoods;
        boolean res = false;
        try {
            affectedRowsInGoods = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + goodId, null);

            if ( affectedRowsInGoods == 1) {
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
        SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean res = false;
        try {

            db.execSQL("DELETE FROM "+ TABLE_GOODS
                        + " WHERE EXISTS "
                        + "( SELECT * "
                        + " FROM " + TABLE_CATEGORIES
                        + " WHERE " + TABLE_CATEGORIES+"."+COL_CATEGORY_ID+"="+TABLE_GOODS+"."+COL_CATEGORY_ID
                        + " AND " +  TABLE_CATEGORIES+"."+COL_USERID+"="+currentUser.getUserId() + ")" );


            db.execSQL("DELETE FROM " + TABLE_CATEGORIES
                    + " WHERE " +  COL_USERID+"="+currentUser.getUserId());

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

    //GROUPS

    boolean addGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SENDER_EMAIL, invitation.getInvitationEmail());
        contentValues.put(COL_INVITATION_RESPONSE, invitation.getResponse());
        contentValues.put(COL_USERID, getCurrentUser().getUserId());
        contentValues.put(COL_SERVER_CO_USER_ID, invitation.getServerCoUserId());
        contentValues.put(COL_SERVER_GROUP_ID, invitation.getServerGroupeId());

        long result = db.insert(TABLE_RECEIVED_INVITATIONS, null, contentValues);
        return (result != -1);
    }

    //CATEGORIES

    boolean addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(COL_CATEGORY_COLOR, category.getColor());
        contentValues.put(COL_CATEGORY_ICON, category.getIcon());
        contentValues.put(COL_SYNC_STATUS, category.getSync());
        contentValues.put(COL_EMAIL, category.getEmail());
        contentValues.put(COL_USERID, category.getUserId());
        contentValues.put(COL_CRUD_STATUS, category.getCrudStatus());
        contentValues.put(COL_SERVER_CATEGORY_ID, category.getServerCategoryId());

        long result = db.insert(TABLE_CATEGORIES, null, contentValues);
        return (result != -1);
    }

    //UPDATES

    //GROUPS


    boolean updateGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_USERNAME, user.getUserName());
        contentValues.put(COL_SERVER_USER_ID, user.getServerUserId());
        contentValues.put(COL_SIGN_UP_TYPE, user.getSignUpType());
        contentValues.put(COL_LAST_LOGGED_IN, user.getLastLoggedIn());

        long result = db.insert(TABLE_USERS, null, contentValues);
        return (result != -1);
    }

    boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_USERNAME, user.getUserName());
        contentValues.put(COL_SERVER_USER_ID, user.getServerUserId());
        contentValues.put(COL_SERVER_GROUP_ID, user.getServerGroupId());
        contentValues.put(COL_LAST_LOGGED_IN, user.getLastLoggedIn());
        contentValues.put(COL_GROUP_ID, user.getGroupId());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_USERS, contentValues, COL_USERID + " = " + user.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }


    boolean updateLastLoggedIn(int serverUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        return ( affectedRows1 + affectedRows2 > 0);
    }


    //GOODS

    boolean addGood(Good good) {
        SQLiteDatabase db = this.getWritableDatabase();
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

        long result = db.insert(TABLE_GOODS, null, contentValues);
        return (result != -1);
    }

    boolean restoreGood(Good good) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CRUD_STATUS, RESTORED);
        contentValues.put(COL_SYNC_STATUS, SYNC_STATUS_FAILED);

        db.beginTransaction();
        int affectedRowsInGoods;
        boolean res = false;
        try {
            affectedRowsInGoods = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + good.getGoodId(), null);

            if ( affectedRowsInGoods == 1) {
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_CO_USER_EMAIL, coUser.getCoUserEmail());
        contentValues.put(COL_USERID, coUser.getUserId());
        contentValues.put(COL_EMAIL, coUser.getEmail());
        contentValues.put(COL_CONFIRMATION_STATUS, coUser.getConfirmationStatus());
        contentValues.put(COL_HAS_RESPONDED, coUser.getHasResponded());
        contentValues.put(COL_SYNC_STATUS, DataBaseHelper.SYNC_STATUS_FAILED);

        long result = db.insert(TABLE_CO_USERS, null, contentValues);
        return (result != -1);
    }


}
