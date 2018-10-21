package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.util.Log;

import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.utils.Constants;

public class GroupsDataManager {

    private final static String TAG = "GroupsDataManager";
    private DataBaseHelper db;

    public GroupsDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
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

    public boolean updateGroup(Group group) {
        Log.d(TAG, Constants.TAG_LISTA+"updateGroup called");
        return db.updateGroup(group);
    }

    public Group getGroupById(int groupId) {
        return db.getGroupById(groupId);
    }
}
