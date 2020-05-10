package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.winservices.wingoods.models.Description;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.models.UserLocation;
import com.winservices.wingoods.utils.Constants;

public class UsersDataManager {

    private static final String TAG = "UsersDataManager";
    private DataBaseHelper db;

    public UsersDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
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

    void insertUserLocation(UserLocation userLocation) {
        db.insertUserLocation(userLocation);
    }

    public static class UpdateUser implements Runnable{

        private User user;
        private Context context;

        public UpdateUser(Context context, User user){
            this.user = user;
            this.context = context;
        }

        @Override
        public void run() {
            UsersDataManager usersDataManager = new UsersDataManager(context);
            usersDataManager.updateUser(user);
        }
    }

    public String[] getUserLocations() {
        User currentUser = getCurrentUser();
        Cursor cursor = db.getUserLocations(currentUser.getServerUserId());
        String[] Addresses = new String[cursor.getCount()];
        int i = 0;
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //int userLocationId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                String userAddress = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USER_ADDRESS));
                //String userGpsLocation = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USER_GPS_LOCATION));

                Addresses[i] = userAddress;
                i = i + 1;
            }
            cursor.close();
        }
        return Addresses;
    }



}
