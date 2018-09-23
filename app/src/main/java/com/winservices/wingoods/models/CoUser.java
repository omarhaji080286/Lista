package com.winservices.wingoods.models;


import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.utils.Constants;

public class CoUser {

    public static final int PENDING = 0;
    public static final int REJECTED = -1;
    public static final int ACCEPTED = 1;
    public static final int COMPLETED = 2;
    public static final int HAS_RESPONDED = 1;
    public static final int HAS_NOT_RESPONDED = 0;

    private int coUserId;
    private String coUserEmail;
    private int userId;
    private String email;
    private int confirmationStatus = PENDING;
    private int hasResponded = HAS_NOT_RESPONDED;
    private int syncStatus = 0;
    private int serverCoUserId = 0;

    public CoUser(String coUserEmail, int userId, String email, int confirmationStatus, int hasResponded, int syncStatus) {
        this.coUserEmail = coUserEmail;
        this.userId = userId;
        this.email = email;
        this.confirmationStatus = confirmationStatus;
        this.hasResponded = hasResponded;
        this.syncStatus = syncStatus;
    }

    public CoUser(int coUserId, String coUserEmail, int userId, String email, int confirmationStatus, int hasResponded, int syncStatus, int serverCoUserId) {
        this.coUserId = coUserId;
        this.coUserEmail = coUserEmail;
        this.userId = userId;
        this.email = email;
        this.confirmationStatus = confirmationStatus;
        this.hasResponded = hasResponded;
        this.syncStatus = syncStatus;
        this.serverCoUserId = serverCoUserId;
    }

    public int getCoUserId() {
        return coUserId;
    }

    public void setCoUserId(int coUserId) {
        this.coUserId = coUserId;
    }

    public String getCoUserEmail() {
        return coUserEmail;
    }

    public void setCoUserEmail(String coUserEmail) {
        this.coUserEmail = coUserEmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(int confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    public int getHasResponded() {
        return hasResponded;
    }

    public void setHasResponded(int hasResponded) {
        this.hasResponded = hasResponded;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public int getServerCoUserId() {
        return serverCoUserId;
    }

    public void setServerCoUserId(int serverCoUserId) {
        this.serverCoUserId = serverCoUserId;
    }
}
