package com.winservices.wingoods.models;

import com.winservices.wingoods.dbhelpers.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class Group {

    private int groupId;
    private String groupName;
    private String ownerEmail;
    private int serverOwnerId;
    private int serverGroupId;
    private int syncStatus = DataBaseHelper.SYNC_STATUS_FAILED;

    public Group(int groupId, String groupName, String ownerEmail, int serverOwnerId, int serverGroupId, int syncStatus) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.ownerEmail = ownerEmail;
        this.serverOwnerId = serverOwnerId;
        this.serverGroupId = serverGroupId;
        this.syncStatus = syncStatus;
    }

    public Group(String groupName, String ownerEmail, int serverOwnerId) {
        this.groupName = groupName;
        this.ownerEmail = ownerEmail;
        this.serverOwnerId = serverOwnerId;
    }

    public Group(String groupName, String ownerEmail, int serverOwnerId, int serverGroupId, int syncStatus) {
        this.groupName = groupName;
        this.ownerEmail = ownerEmail;
        this.serverOwnerId = serverOwnerId;
        this.serverGroupId = serverGroupId;
        this.syncStatus = syncStatus;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public int getServerOwnerId() {
        return serverOwnerId;
    }

    public void setServerOwnerId(int serverOwnerId) {
        this.serverOwnerId = serverOwnerId;
    }

    public int getServerGroupId() {
        return serverGroupId;
    }

    public void setServerGroupId(int serverGroupId) {
        this.serverGroupId = serverGroupId;
    }

    public JSONObject toJSONObject(){

        JSONObject JSONGroup = new JSONObject();

        try {
            JSONGroup.put("groupId", this.groupId);
            JSONGroup.put("groupName", this.groupName);
            JSONGroup.put("ownerEmail", this.ownerEmail);
            JSONGroup.put("serverOwnerId", this.serverOwnerId);
            JSONGroup.put("serverGroupId", this.serverGroupId);
            JSONGroup.put("syncStatus", this.syncStatus);

            return JSONGroup;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
