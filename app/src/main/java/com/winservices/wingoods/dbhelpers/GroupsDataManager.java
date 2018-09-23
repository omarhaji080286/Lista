package com.winservices.wingoods.dbhelpers;

import android.content.Context;

import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.utils.Constants;

public class GroupsDataManager {

    private DataBaseHelper db;

    public GroupsDataManager(Context context) {
        this.db = new DataBaseHelper(context);
    }

    public void closeDB(){
        db.close();
    }

    public int addGroup(Group group) {
        int result;
        if (db.addGroup(group)) {
            result = Constants.SUCCESS;
        } else {
            result = Constants.ERROR;
        }
        return result;
    }

    public Group getGroupByUserId(int serverUserId){
        return db.getGroupByUserId(serverUserId);
    }

    public Group getGroupByOwnerId( int ownerId){
        return db.getGroupByOwnerId(ownerId);
    }

    boolean updateGroup(Group group){
        return db.updateGroup(group);
    }

}
