package com.winservices.wingoods.models;



public class ReceivedInvitation {

    private int receivedInvitationId;
    private String invitationEmail;
    private String invitationCategories;
    private int response = CoUser.PENDING;
    private int userId;
    private int serverCoUserId;
    private int serverGroupeId;

    public ReceivedInvitation(String invitationEmail, String invitationCategories, int response) {
        this.invitationEmail = invitationEmail;
        this.invitationCategories = invitationCategories;
        this.response = response;
    }

    public ReceivedInvitation(int receivedInvitationId, String invitationEmail, int response, int userId) {
        this.receivedInvitationId = receivedInvitationId;
        this.invitationEmail = invitationEmail;
        this.response = response;
        this.userId = userId;
    }

    public ReceivedInvitation(int receivedInvitationId, String invitationEmail, int response, int userId, int serverCoUserId) {
        this.receivedInvitationId = receivedInvitationId;
        this.invitationEmail = invitationEmail;
        this.response = response;
        this.userId = userId;
        this.serverCoUserId = serverCoUserId;
    }

    public int getServerGroupeId() {
        return serverGroupeId;
    }

    public void setServerGroupeId(int serverGroupeId) {
        this.serverGroupeId = serverGroupeId;
    }

    public int getReceivedInvitationId() {
        return receivedInvitationId;
    }

    public void setReceivedInvitationId(int receivedInvitationId) {
        this.receivedInvitationId = receivedInvitationId;
    }

    public String getInvitationEmail() {
        return invitationEmail;
    }

    public void setInvitationEmail(String invitationEmail) {
        this.invitationEmail = invitationEmail;
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
}
