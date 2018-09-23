package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class CoUsersDataManager {

    private DataBaseHelper db;

    public CoUsersDataManager(Context context) {
        this.db = new DataBaseHelper(context);;
    }

    List<CoUser> getNotSyncCoUsers() {
        List<CoUser> list = new ArrayList<>();
        Cursor cursor = db.getNotSyncCoUsers();
        while (cursor.moveToNext()) {
            int coUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
            String coUserEmail = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_EMAIL));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USERID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_EMAIL));
            int confirmationStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CONFIRMATION_STATUS));
            int hasResponed = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_HAS_RESPONDED));
            int serverCoUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_CO_USER_ID));
            int syncStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_SYNC_STATUS));

            CoUser coUser = new CoUser(coUserId, coUserEmail, userId, email, hasResponed, confirmationStatus, syncStatus, serverCoUserId);
            list.add(coUser);
        }
        cursor.close();
        return list;
    }

    boolean updateCoUserAfterSync(int coUserId, int syncStatus, int serverCoUserId){
        //DataBaseHelper db = new DataBaseHelper(context);
        return db.updateCoUserAfterSync (coUserId, syncStatus, serverCoUserId);
    }

    public int addCoUser(Context context, CoUser coUser) {
        int result;
        if (db.coUserExists(coUser.getCoUserEmail(), coUser.getEmail())) {
            result = Constants.DATAEXISTS;
        } else {
            if (db.addCoUser(coUser)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        }
        return result;
    }

    public void closeDB(){
        db.close();
    }


}
