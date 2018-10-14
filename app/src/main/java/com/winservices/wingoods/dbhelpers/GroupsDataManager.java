package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.util.Log;

import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.utils.Constants;

public class GroupsDataManager {

    private final static String TAG = "GroupsDataManager";
    private DataBaseHelper db;

    public GroupsDataManager(Context context) {
        this.db = new DataBaseHelper(context);
        //this.db = DataBaseHelper.getInstance(context);
        Log.d(TAG, Constants.TAG_LISTA+"DB opened");


    }

    public void closeDB(){
        db.close();
        Log.d(TAG, Constants.TAG_LISTA+"DB closed");
    }

    public int addGroup(Group group) {
        int result;
        if (db.addGroup(group)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        Log.d(TAG, Constants.TAG_LISTA+"addGroup called");

        return result;
    }

    public Group getGroupByUserId(int serverUserId){
        Log.d(TAG, Constants.TAG_LISTA+"getGroupByUserId called");
        return db.getGroupByUserId(serverUserId);
    }

    public Group getGroupByOwnerId( int ownerId){
        Log.d(TAG, Constants.TAG_LISTA+"getGroupByOwnerId called");
        return db.getGroupByOwnerId(ownerId);
    }

    boolean updateGroup(Group group) {
        Log.d(TAG, Constants.TAG_LISTA+"updateGroup called");
        return db.updateGroup(group);
    }

}
