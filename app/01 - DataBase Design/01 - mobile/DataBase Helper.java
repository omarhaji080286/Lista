package com.winservices.wingoods.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.models.User;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String COL_CATEGORY_NAME = "category_name";
    public static final String _ID = "_id";
    public static final int SYNC_STATUS_OK = 1;
    public static final int SYNC_STATUS_FAILED = 0;
    public static final String COL_EMAIL = "email";
    public static final String COL_USERID = "user_id";
    public static final String DEFAULT_EMAIL = "default@lista.com";
    public static final String COL_SYNC_STATUS = "sync_status";
    public static final String COL_CATEGORY_COLOR = "category_color";
    public static final String COL_CATEGORY_ICON = "category_icon";
    public static final String TABLE_CO_USERS = "co_users";
    public static final String COL_CONFIRMATION_STATUS = "confirmation_status";
    public static final String COL_CO_USER_ID = "co_user_id";
    public static final String COL_CO_USER_EMAIL = "co_user_email";
    public static final String COL_SERVER_CO_USER_ID = "server_co_user_id";

    public static final String TABLE_GROUPS = "groups";

    public static final String COL_GROUP_ID = "group_id";
    public static final String COL_GROUP_NAME = "group_name";
    public static final String COL_OWNER_EMAIL = "owner_email";
    public static final String COL_SERVER_OWNER_ID = "server_owner_id";
    public static final String COL_SERVER_GROUP_ID = "server_group_id";
    public static final String COL_SERVER_CATEGORY_ID = "server_category_id";
    public static final String COL_SERVER_USER_ID = "server_user_id";
    public static final String COL_SERVER_GOOD_ID = "server_good_id";
    public static final String COL_HAS_RESPONDED = "has_responded";
    public static final String COL_SENDER_EMAIL = "sender_email";
    public static final String COL_INVITATION_RESPONSE = "invitation_response";
    public static final String COL_RECEIVED_INVITATION_ID = "received_invitation_id";
    static final String COL_ = "category_id";
    static final String COL_CATEGORY_ID = "category_id";
    static final String COL_GOOD_NAME = "good_name";
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
    private static final String HOST = "http://lista.onlinewebshop.net/webservices/";
    public static final String HOST_URL_ADD_USER = HOST + "registerUser.php";
    public static final String HOST_URL_LOGIN_USER = HOST + "loginUser.php";
    public static final String HOST_URL_ADD_CATEGORY = HOST + "addCategory.php";
    public static final String HOST_URL_ADD_GOOD = HOST + "addGood.php";
    public static final String HOST_URL_ADD_CO_USER = HOST + "addCoUser.php";
    public static final String HOST_URL_GET_INVITATIONS = HOST + "getInvitations.php";
    public static final String HOST_URL_UPDATE_CO_USER_RESPONSE = HOST + "updateCoUserResponse.php";
    public static final String HOST_URL_ADD_GROUP = HOST + "addGroup.php";
    public static final String HOST_URL_GET_GROUP_DATA = HOST + "getGroupData.php";
    public static final String HOST_URL_DELETE_USER_CATEGORIES_AND_GOODS = HOST + "deleteAllUserCategoriesAndGoods.php";
    public static final String HOST_URL_UPDATE_CATEGORIES_AND_GOODS = HOST + "updateCategoriesAndGoods.php";
    public static final String HOST_URL_UPDATE_CATEGORIES_AND_GOODS_FROM_SERVER = HOST + "updateCategoriesAndGoodsFromServer.php";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users ( " +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "password TEXT, " +
                "user_name INTEGER, " +
                "server_user_id INTEGER," +
                "server_group_id INTEGER" +
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
                "quantity_level INTEGER, " +
                "category_id INTEGER, " +
                "is_to_buy BOOLEAN, " +
                "sync_status INTEGER, " +
                "email TEXT, " +
                "crud_status INTEGER, " +
                "server_good_id INTEGER, " +
                "FOREIGN KEY (category_id) REFERENCES categories (category_id)) ");

        db.execSQL("CREATE TABLE received_invitations ( " +
                "received_invitation_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_email TEXT, " +
                "invitation_response INTEGER, " +
                "user_id INTEGER, " +
                "server_group_id INTEGER, " +
                "server_co_user_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES users (user_id)) ");


        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('1', 'FRUITS & LEGUMES', '" + R.color.indigo + "', '" + R.drawable.fruit + "', '0', '1', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('2', 'PAIN & VIENNOISERIES', '" + R.color.deepOrange + "', '" + R.drawable.bread + "', '0', '1', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('3', 'VIANDES & POISSONS', '" + R.color.brown + "', '" + R.drawable.steak + "', '0', '1', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('4', 'MARCHAND D''EPICES', '" + R.color.deepGreen + "', '" + R.drawable.spices + "', '0', '1', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('5', 'MENAGE & HYGIENE', '" + R.color.lime + "', '" + R.drawable.gel + "', '0', '1', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('6', 'ALIMENTATION GENERALE', '" + R.color.teal + "', '" + R.drawable.grocery + "', '0', '1', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('7', 'FROMAGES & CHARCUTERIE', '" + R.color.pink + "', '" + R.drawable.cheese + "', '0', '1', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO categories (category_id, category_name, category_color, category_icon, sync_status, user_id, email, server_category_id) VALUES ('8', 'DIVERS', '" + R.color.purple + "', '" + R.drawable.others + "', '0', '1', 'default@lista.com', '0')");

        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('1', 'Carottes', '1', '1', '1', '0', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('2', 'Pain', '2', '1', '1', '0', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('3', 'Viande hachée', '3', '1', '1', '0', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('4', 'Cumin', '4', '1', '1', '0', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('5', 'Dentifrice', '5', '1', '1', '0', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('6', 'Sauce tomate', '6', '1', '1', '0', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('7', 'Kiri', '7', '1', '1', '0', 'default@lista.com', '0')");
        db.execSQL("INSERT INTO goods (good_id, good_name, category_id, quantity_level, is_to_buy, sync_status, email, server_good_id) VALUES ('8', 'Lampe', '8', '1', '1', '0', 'default@lista.com', '0')");

        db.execSQL("INSERT INTO users (user_id, email, password, user_name, server_user_id) VALUES ('1', 'default@lista.com', 'pass', 'name', '0')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS shared_categories ");
        db.execSQL("DROP TABLE IF EXISTS goods ");
        db.execSQL("DROP TABLE IF EXISTS categories ");
        db.execSQL("DROP TABLE IF EXISTS co_users ");
        db.execSQL("DROP TABLE IF EXISTS users ");

        onCreate(db);
    }


    boolean categoryExists(String name) {
        String countQuery = "select * from " + TABLE_CATEGORIES + " where " +
                COL_CATEGORY_NAME + " = '" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cnt = 0;
        if (cursor != null) {
            cnt = cursor.getCount();
            cursor.close();
        }
        return (cnt > 0);
    }

    boolean goodExists(String name) {
        String countQuery = "select * from " + TABLE_GOODS + " where " +
                COL_GOOD_NAME + " = '" + name + "'";
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


    //SELECT QUERIES

    //GROUPS

    boolean groupExists(String groupName) {
        String countQuery = "select * from " + TABLE_GROUPS
                + " where "
                + COL_GROUP_NAME + " = '" + groupName + "'";

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


    Group getGroupByUserId(int ownerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        Group group = null;
        try {
            String sql = "select " + COL_GROUP_ID + " as " + _ID + " , * "
                    + " from " + TABLE_GROUPS
                    + " where " + COL_SERVER_OWNER_ID + " = " + ownerId;
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
                + COL_SENDER_EMAIL + " = '" + senderEmail + "'";

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
                    + " WHERE " + COL_SENDER_EMAIL + " = '" + senderEmail + "'", null);

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
                    + " where " + COL_INVITATION_RESPONSE + " <> " + CoUser.COMPLETED;
            res = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    //CO USERS AND SHARED CATEGORIES

    Cursor getNotSyncCoUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CO_USER_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CO_USERS
                    + " where " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    boolean coUserExists(String coUserEmail, String email) {
        String countQuery = "select * from " + TABLE_CO_USERS
                + " where "
                + COL_EMAIL + " = '" + email + "'"
                + "AND " + COL_CO_USER_EMAIL + " = '" + coUserEmail + "'";

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


    CoUser getCoUserByEmail(String coUserEmail, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        CoUser coUser = null;
        try {
            res = db.rawQuery("SELECT " + COL_CO_USER_ID + " as " + _ID + " , *"
                    + " FROM " + TABLE_CO_USERS
                    + " WHERE " + COL_CO_USER_EMAIL + " = '" + coUserEmail + "'"
                    + "AND " + COL_EMAIL + " = '" + email + "'", null);

            while (res.moveToNext()) {
                int coUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                //String coUserEmail = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_EMAIL));
                //String email = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
                int userId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
                int confirmationStatus = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_CONFIRMATION_STATUS));
                int hasResponded = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_HAS_RESPONDED));
                int sync_status = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
                int serverCoUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CO_USER_ID));
                coUser = new CoUser(coUserId, coUserEmail, userId, email, confirmationStatus, hasResponded, sync_status, serverCoUserId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return coUser;
    }


    //USERS

    boolean userExists(User user) {
        String countQuery = "select * from " + TABLE_USERS + " where " +
                COL_EMAIL + " = '" + user.getEmail() + "'";
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
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        User currentUser = null;
        try {
            res = db.rawQuery("SELECT " + COL_USERID + " as " + _ID + " , *"
                    + " FROM " + TABLE_USERS
                    + " where " + COL_USERID + " = 1", null);


            while (res.moveToNext()) {
                int userId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper._ID));
                String email = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
                String password = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_PASSWORD));
                String userName = res.getString(res.getColumnIndexOrThrow(DataBaseHelper.COL_USERNAME));
                int serverUserId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_USER_ID));
                int serverGroupId = res.getInt(res.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_GROUP_ID));

                currentUser = new User(userId, email, password, userName);
                currentUser.setServerUserId(serverUserId);
                currentUser.setServerGroupId(serverGroupId);
            }
            res.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentUser;
    }

    //CATEGORIES

    Cursor getCategoriesByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , " + COL_CATEGORY_NAME
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CATEGORY_NAME + " LIKE '%" + name + "%' ", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoryByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_CATEGORY_NAME + " = '" + name.replaceAll("'", "''") + "' ", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoryByServerCategoryId(int serverCategoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_SERVER_CATEGORY_ID + " = " + serverCategoryId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = 1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //return categories that are already synchronized with server (have a server category id)
    public Cursor getExcludedCategoriesFromSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , *"
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_USERID + " = 1"
                    + " and " + COL_SERVER_CATEGORY_ID + " <> 0", null);
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
                    + " where " + COL_USERID + " = 1"
                    + " and " + COL_SERVER_CATEGORY_ID + " <> 0"
                    + " and " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }



    Cursor getAllCategoriesOverview() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , " + COL_CATEGORY_NAME
                    + " , ( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = "
                    + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_QUANTITY_LEVEL + " = " + "'1'"
                    + " GROUP BY " + COL_CATEGORY_ID + " ) as " + RED_GOODS
                    + " , ( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = "
                    + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_QUANTITY_LEVEL + " = " + "'2'"
                    + " GROUP BY " + COL_CATEGORY_ID + " ) as " + ORANGE_GOODS
                    + " , ( select count(*) from " + TABLE_GOODS
                    + " where " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + " = "
                    + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_QUANTITY_LEVEL + " = " + "'3'"
                    + " GROUP BY " + COL_CATEGORY_ID + " ) as " + GREEN_GOODS
                    + " from " + TABLE_CATEGORIES, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoriesByGood(String searchGoodName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_CATEGORY_ID + " as " + _ID + " , " + COL_CATEGORY_NAME
                    + " FROM " + TABLE_CATEGORIES +
                    " where exists" +
                    " ( select * from goods where" +
                    " categories.category_id = goods.category_id" +
                    " and goods.good_name like '%" + searchGoodName + "%' )", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_CATEGORY_ID + " as " + _ID + " , " + COL_CATEGORY_NAME
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

    Cursor getCategoriesByLevel(int quantityLevelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * from " + TABLE_CATEGORIES +
                    " where exists" +
                    " ( select * from goods where" +
                    " categories.category_id = goods.category_id" +
                    " and goods.quantity_level = " + quantityLevelId + ")", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    boolean categoryExistsWithDifferentId(Category category) {
        String countQuery = "SELECT * FROM " + TABLE_CATEGORIES +
                " WHERE " + COL_CATEGORY_NAME + " = '" + category.getCategoryName() + "'"
                + " AND " + COL_CATEGORY_ID + " != " + category.getCategoryId();
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

    Cursor getPuchasableCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * from " + TABLE_CATEGORIES +
                    " where exists" +
                    " ( select * from goods where" +
                    " categories.category_id = goods.category_id" +
                    " and goods." + COL_IS_TO_BUY + " = 1 )", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getNotSyncCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_CATEGORY_ID + " as " + _ID + " , * "
                    + " from " + TABLE_CATEGORIES
                    + " where " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_SERVER_CATEGORY_ID + " = 0", null);
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


    Cursor getGoodByServerGoodId(int serverGoodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , * "
                    + " FROM " + TABLE_GOODS
                    + " where " + COL_SERVER_GOOD_ID + " = " + serverGoodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getGoodsWithCategoryByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("select " + COL_GOOD_ID + " as " + _ID + " , "
                    + COL_GOOD_NAME + " , " + COL_CATEGORY_NAME
                    + " , * from " + TABLE_GOODS + ", " + TABLE_CATEGORIES
                    + " WHERE ( " + TABLE_CATEGORIES + "." + COL_CATEGORY_ID + "=" + TABLE_GOODS + "." + COL_CATEGORY_ID
                    + " AND " + TABLE_GOODS + "." + COL_GOOD_NAME + " LIKE '%" + name + "%' )", null);
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
                    + " AND " + COL_GOOD_NAME + " LIKE '%" + searchGoodName + "%'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getGoodsByCategoryAndName(int categoryId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + "  * FROM " + TABLE_GOODS
                    + " WHERE " + COL_CATEGORY_ID + "=" + categoryId
                    + " AND " + COL_GOOD_NAME + " LIKE '%" + name + "%'"
                    + " ORDER BY " + COL_GOOD_ID, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    boolean goodExistsWithDifferentId(String name, int id) {
        String countQuery = "select * from " + TABLE_GOODS +
                " where " + COL_GOOD_NAME + " = '" + name + "'"
                + " AND " + COL_GOOD_ID + " != " + id;
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

    Cursor getGoodsToBuy(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + "  * FROM " + TABLE_GOODS
                    + " WHERE " + COL_CATEGORY_ID + " = " + categoryId
                    + " AND " + COL_IS_TO_BUY + " = 1 "
                    + " ORDER BY " + COL_GOOD_ID, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getExcludedGoodsFromSync() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + "  * FROM " + TABLE_GOODS
                    + " WHERE " + COL_SERVER_GOOD_ID + " <> 0", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    Cursor getUpdatedGoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + "  * FROM " + TABLE_GOODS
                    + " WHERE " + COL_SERVER_GOOD_ID + " <> 0"
                    + " and " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Cursor getNotSyncGoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COL_GOOD_ID + " as " + _ID + " , " + "  * FROM " + TABLE_GOODS
                    + " WHERE " + COL_SYNC_STATUS + " = " + SYNC_STATUS_FAILED
                    + " and " + COL_SERVER_GOOD_ID + " = 0 ", null);
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

    boolean updateGoodAfterSync(int goodId, int serverGoodId, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SERVER_GOOD_ID, serverGoodId);
        contentValues.put(COL_SYNC_STATUS, syncStatus);

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + goodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    boolean updateGoodLevel(int goodId, int level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_QUANTITY_LEVEL, level);
        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_GOODS, contentValues, COL_GOOD_ID + " = " + goodId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    boolean updateGood(Good good) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GOOD_NAME, good.getGoodName());
        contentValues.put(COL_CATEGORY_ID, good.getCategoryId());
        contentValues.put(COL_QUANTITY_LEVEL, good.getQuantityLevelId());
        contentValues.put(COL_IS_TO_BUY, good.isToBuy());
        contentValues.put(COL_SYNC_STATUS, DataBaseHelper.SYNC_STATUS_FAILED);
        contentValues.put(COL_SERVER_GOOD_ID, good.getServerGoodId());

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

    boolean updateCategoryAfterSync(int categoryId, int serverCategoryId, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SERVER_CATEGORY_ID, serverCategoryId);
        contentValues.put(COL_SYNC_STATUS, syncStatus);
        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_CATEGORIES, contentValues, COL_CATEGORY_ID + " = " + categoryId, null);
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
        Boolean res = false;
        try {
            //db.delete returns number of rows affected
            res = (db.delete(TABLE_GOODS, COL_GOOD_ID + " = " + goodId, null) > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //CATEGORIES

    boolean deleteCategory(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        boolean res = false;
        try {
            db.delete(TABLE_GOODS, COL_CATEGORY_ID + " = " + categoryId, null);
            int resCategory = db.delete(TABLE_CATEGORIES, COL_CATEGORY_ID + " = " + categoryId, null);
            if (resCategory > 0) {
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
            int resGoods = db.delete(TABLE_GOODS, null, null);
            int resCategory = db.delete(TABLE_CATEGORIES, null, null);
            if (resCategory > 0 && resGoods > 0) {
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

    //CREATE QUERIES

    //GROUPS

    boolean addGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GROUP_NAME, group.getGroupName());
        contentValues.put(COL_OWNER_EMAIL, group.getOwnerEmail());
        contentValues.put(COL_SERVER_OWNER_ID, group.getServerOwnerId());
        contentValues.put(COL_SERVER_GROUP_ID, group.getServerGroupId());
        contentValues.put(COL_SYNC_STATUS, group.getServerGroupId());

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

    boolean updateDefaultUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_USERNAME, user.getUserName());
        contentValues.put(COL_SERVER_USER_ID, user.getServerUserId());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_USERS, contentValues, COL_EMAIL + " = 'default@lista.com' ", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_USERNAME, user.getUserName());
        contentValues.put(COL_SERVER_USER_ID, user.getServerUserId());
        contentValues.put(COL_SERVER_GROUP_ID, user.getServerGroupId());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_USERS, contentValues, COL_USERID + " = " + user.getUserId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }


    boolean updateUserEmailInGoods(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_GOODS, contentValues, COL_EMAIL + " = 'default@lista.com' ", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    boolean updateUserEmailInCategories(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.getEmail());

        int affectedRows = 0;
        try {
            affectedRows = db.update(TABLE_CATEGORIES, contentValues, COL_EMAIL + " = 'default@lista.com' ", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (affectedRows == 1);
    }

    //GOODS

    boolean addGood(Good good) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GOOD_NAME, good.getGoodName());
        contentValues.put(COL_CATEGORY_ID, good.getCategoryId());
        contentValues.put(COL_QUANTITY_LEVEL, good.getQuantityLevelId());
        int isToBuyInt = 0;
        if (good.isToBuy()) isToBuyInt = 1;
        contentValues.put(COL_IS_TO_BUY, isToBuyInt);
        contentValues.put(COL_SYNC_STATUS, good.getSync());
        contentValues.put(COL_EMAIL, good.getEmail());
        contentValues.put(COL_SERVER_GOOD_ID, good.getServerGoodId());

        long result = db.insert(TABLE_GOODS, null, contentValues);
        return (result != -1);
    }

    boolean restoreGood(Good good) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_GOOD_ID, good.getGoodId());
        contentValues.put(COL_GOOD_NAME, good.getGoodName());
        contentValues.put(COL_CATEGORY_ID, good.getCategoryId());
        contentValues.put(COL_QUANTITY_LEVEL, good.getQuantityLevelId());
        long result = db.insert(TABLE_GOODS, null, contentValues);
        return (result != -1);
    }

    //SHARED CATEGORIES AND CO_USERS

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
