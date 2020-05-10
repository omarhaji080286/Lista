package com.winservices.wingoods.models;

import android.content.Context;

import com.winservices.wingoods.dbhelpers.CitiesDataManager;
import com.winservices.wingoods.dbhelpers.GroupsDataManager;

public class User {

    public static final String PREFIX_USER = "user_";
    public static final int IS_REGISTERED = 1;
    public static final int IS_NOT_REGISTERED = 0;
    private int userId;
    private String email;
    private String password;
    private String userName;
    private int serverUserId;
    private Group group;
    private int serverGroupId;
    private String signUpType;
    private int lastLoggedIn;
    private int groupId;
    private String fcmToken;
    private String userPhone;
    private int serverCityId;

    public User(String userPhone) {
        this.userPhone = userPhone;
    }

    public User(){}

    public User(int userId, String email, String password, String userName, int serverUserId, int serverGroupId, String signUpType) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.serverUserId = serverUserId;
        this.serverGroupId = serverGroupId;
        this.signUpType = signUpType;
    }

    public User(int userId, String email, String password, String userName) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.userName = userName;
    }

    public User(String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public int getServerCityId() {
        return serverCityId;
    }

    public void setServerCityId(int serverCityId) {
        this.serverCityId = serverCityId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(int lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public String getSignUpType() {
        return signUpType;
    }

    public void setSignUpType(String signUpType) {
        this.signUpType = signUpType;
    }

    public int getServerGroupId() {
        return serverGroupId;
    }

    public void setServerGroupId(int serverGroupId) {
        this.serverGroupId = serverGroupId;
    }

    public int getServerUserId() {
        return serverUserId;
    }

    public void setServerUserId(int serverUserId) {
        this.serverUserId = serverUserId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Group getGroup(Context context) {

        GroupsDataManager groupsDataManager = new GroupsDataManager(context);

        return groupsDataManager.getGroupByUserId(this.getServerUserId());
    }

    public City getCity(Context context){
        CitiesDataManager citiesDataManager = new CitiesDataManager(context);
        return citiesDataManager.getCityById(this.serverCityId);
    }
}
