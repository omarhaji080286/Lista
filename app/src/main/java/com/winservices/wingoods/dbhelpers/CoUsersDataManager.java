package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class CoUsersDataManager {

    private DataBaseHelper db;
    private static final String TAG = "CoUsersDataManager";

    public CoUsersDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
    }

    public List<CoUser> getNotSyncCoUsers() {
        List<CoUser> list = new ArrayList<>();
        Cursor cursor = db.getNotSyncCoUsers();
        while (cursor.moveToNext()) {
            int coUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String coUserPhone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_PHONE));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int confirmationStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CONFIRMATION_STATUS));
            int hasResponed = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_HAS_RESPONDED));
            int serverCoUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_ID));
            int syncStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));

            CoUser coUser = new CoUser(coUserId, coUserPhone, userId, email, hasResponed, confirmationStatus, syncStatus, serverCoUserId);
            list.add(coUser);
        }
        cursor.close();
        Log.d(TAG, Constants.TAG_LISTA+"getNotSyncCoUsers called");
        return list;
    }

    boolean updateCoUserAfterSync(int coUserId, int syncStatus, int serverCoUserId){
        Log.d(TAG, Constants.TAG_LISTA+"updateCoUserAfterSync called");
        return db.updateCoUserAfterSync (coUserId, syncStatus, serverCoUserId);
    }

    public int addCoUser(CoUser coUser) {
        int result;
        if (db.coUserExists(coUser.getCoUserPhone(), coUser.getUserId())) {
            result = Constants.DATAEXISTS;
        } else {
            if (db.addCoUser(coUser)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        }
        Log.d(TAG, Constants.TAG_LISTA+"addCoUser called");

        return result;
    }

    public CoUser getCoUserById(int coUserId) {
        Cursor cursor = db.getCoUserById(coUserId);

        cursor.moveToNext();

        //int coUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_ID));
        String coUserPhone = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_PHONE));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_ID));
        int confirmationStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CONFIRMATION_STATUS));
        int hasResponded = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_HAS_RESPONDED));
        int syncStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));
        int serverCoUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_CO_USER_ID));
        int serverUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SERVER_USER_ID));

        CoUser coUser = new CoUser(coUserPhone, userId, confirmationStatus, hasResponded, syncStatus, serverUserId);
        coUser.setCoUserId(coUserId);
        coUser.setServerCoUserId(serverCoUserId);

        return coUser;
    }

    public int updateCoUser(CoUser coUser) {
        int result;

        if (db.updateCoUser(coUser)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        Log.d(TAG, Constants.TAG_LISTA+"updateCoUser called");

        return result;
    }



}
