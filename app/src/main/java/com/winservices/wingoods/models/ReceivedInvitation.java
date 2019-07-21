package com.winservices.wingoods.models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ReceivedInvitation {

    private int receivedInvitationId;
    //private String invitationEmail;
    private String invitationCategories;
    private int response = CoUser.PENDING;
    private int userId;
    private int serverCoUserId;
    private int serverGroupId;
    private String invitationPhone;

    public ReceivedInvitation(int serverCoUserId, int serverGroupId, String invitationPhone) {
        this.serverCoUserId = serverCoUserId;
        this.serverGroupId = serverGroupId;
        this.invitationPhone = invitationPhone;
    }

    public ReceivedInvitation(String invitationPhone, String invitationCategories, int response) {
        this.invitationPhone = invitationPhone;
        this.invitationCategories = invitationCategories;
        this.response = response;
    }

    public ReceivedInvitation(int receivedInvitationId, String invitationPhone, int response, int userId) {
        this.receivedInvitationId = receivedInvitationId;
        this.invitationPhone = invitationPhone;
        this.response = response;
        this.userId = userId;
    }

    public ReceivedInvitation(int receivedInvitationId, String invitationPhone, int response, int userId, int serverCoUserId) {
        this.receivedInvitationId = receivedInvitationId;
        this.invitationPhone = invitationPhone;
        this.response = response;
        this.userId = userId;
        this.serverCoUserId = serverCoUserId;
    }

    public String getInvitationPhone() {
        return invitationPhone;
    }

    public void setInvitationPhone(String invitationPhone) {
        this.invitationPhone = invitationPhone;
    }

    public int getServerGroupId() {
        return serverGroupId;
    }

    public void setServerGroupId(int serverGroupId) {
        this.serverGroupId = serverGroupId;
    }

    public int getReceivedInvitationId() {
        return receivedInvitationId;
    }

    public void setReceivedInvitationId(int receivedInvitationId) {
        this.receivedInvitationId = receivedInvitationId;
    }

    public String getInvitationCategories() {
        return invitationCategories;
    }

    public void setInvitationCategories(String invitationCategories) {
        this.invitationCategories = invitationCategories;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getServerCoUserId() {
        return serverCoUserId;
    }

    public void setServerCoUserId(int serverCoUserId) {
        this.serverCoUserId = serverCoUserId;
    }

    public JSONObject toJSONObject(){

        JSONObject jsonRI = new JSONObject();

        try {
            jsonRI.put("receivedInvitationId", this.receivedInvitationId);
            jsonRI.put("invitationCategories", this.invitationCategories);
            jsonRI.put("response", this.response);
            jsonRI.put("userId", this.userId);
            jsonRI.put("serverCoUserId", this.serverCoUserId);
            jsonRI.put("serverGroupId", this.serverGroupId);

            return jsonRI;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray listToJSONArray(List<ReceivedInvitation> receivedInvitations){
        JSONArray jsonRIs = new JSONArray();
        for (int i = 0; i < receivedInvitations.size(); i++) {
            JSONObject jsonRI = receivedInvitations.get(i).toJSONObject();
            jsonRIs.put(jsonRI);
        }
        return jsonRIs;
    }

}
