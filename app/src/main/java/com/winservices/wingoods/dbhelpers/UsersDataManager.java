package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;

public class UsersDataManager {

    private DataBaseHelper db;

    public UsersDataManager(Context context) {
        this.db = new DataBaseHelper(context);
    }

    public void closeDB(){
        db.close();
    }

    public User getUserByServerUserId(int serverUserId){
        return db.getUserByServerUserId(serverUserId);
    }

    public User getUserByEmail(String email, String signUpType){
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
        return result;
    }

    public User getCurrentUser(){
        return db.getCurrentUser();
    }

    public int updateUser(User user) {
        int result;
            if (db.updateUser(user)) {
                result = Constants.SUCCESS;
            } else {
                result = Constants.ERROR;
            }
        return result;
    }


    public int updateLastLoggedIn(int serverUserId) {
        int result;
        if (db.updateLastLoggedIn(serverUserId)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        return result;
    }



}
