package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;

public class UsersDataManager {

    private static final String TAG = "UsersDataManager";
    private DataBaseHelper db;

    public UsersDataManager(Context context) {
        this.db = new DataBaseHelper(context);
        //this.db = DataBaseHelper.getInstance(context);
        Log.d(TAG, Constants.TAG_LISTA+"DB Opened");
    }

    public void closeDB(){
        db.close();
    }

    public User getUserByServerUserId(int serverUserId){
        Log.d(TAG, Constants.TAG_LISTA+"DB closed");
        return db.getUserByServerUserId(serverUserId);

    }

    public User getUserByEmail(String email, String signUpType){
        Log.d(TAG, Constants.TAG_LISTA+"getUserByEmail called");
        return db.getUserByEmail(email,signUpType);
    }

    public int addUser(User user) {
        int result;
        if (db.userExists(user)) {
            result = Constants.DATAEXISTS;
        } else {
            if (db.addUser(user)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        }
        Log.d(TAG, Constants.TAG_LISTA+"addUser called");
        return result;
    }

    public User getCurrentUser(){
        Log.d(TAG, Constants.TAG_LISTA+"getCurrentUser called");
        return db.getCurrentUser();
    }

    public int updateUser(User user) {
        int result;
            if (db.updateUser(user)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        Log.d(TAG, Constants.TAG_LISTA+"updateUser called");
        return result;
    }


    public int updateLastLoggedIn(int serverUserId) {
        int result;
        if (db.updateLastLoggedIn(serverUserId)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        Log.d(TAG, Constants.TAG_LISTA+"updateLastLoggedIn called");

        return result;
    }



}
