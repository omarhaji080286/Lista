package com.winservices.wingoods.models;

import android.content.Context;

import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private int categoryId = 0;
    private String categoryName;
    private int color;
    private int icon;
    private int sync;
    private int userId;
    private String email;
    private int crudStatus;
    private int serverCategoryId = 0;

    private int goodsToBuyNumber;
    private int orderedGoodsNumber;
    private int goodsNumber;

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public int getOrderedGoodsNumber() {
        return orderedGoodsNumber;
    }

    public void setOrderedGoodsNumber(int orderedGoodsNumber) {
        this.orderedGoodsNumber = orderedGoodsNumber;
    }

    public int getGoodsToBuyNumber() {
        return goodsToBuyNumber;
    }

    public void setGoodsToBuyNumber(int goodsToBuyNumber) {
        this.goodsToBuyNumber = goodsToBuyNumber;
    }

    public Category(){}

    public Category(String categoryName, int color, int icon, int sync, int userId, String email) {
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
        this.sync = sync;
        this.userId = userId;
        this.email = email;
    }

    public Category(int categoryId, String categoryName, int color, int icon, int sync, int userId, String email, int crud, int serverCategoryId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
        this.sync = sync;
        this.userId = userId;
        this.email = email;
        this.crudStatus = crud;
        this.serverCategoryId = serverCategoryId;
    }

    public Category(String categoryName, int color, int icon, int sync, int userId, String email, int serverCategoryId) {
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
        this.sync = sync;
        this.userId = userId;
        this.email = email;
        this.serverCategoryId = serverCategoryId;
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryName, int sync) {
        this.categoryName = categoryName;
        this.sync = sync;
    }

    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Category(int categoryId, String categoryName, int color, int icon) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
    }

    public Category(String categoryName, int color, int icon) {
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
    }

    public Category(int categoryId, String categoryName, int color, int icon, int sync, int userId, String email) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
        this.sync = sync;
        this.userId = userId;
        this.email = email;
    }

    public Category(int categoryId, String categoryName, int color, int icon, int sync) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
        this.sync = sync;
    }

    public Category(String categoryName, int color, int icon, int sync) {
        this.categoryName = categoryName;
        this.color = color;
        this.icon = icon;
        this.sync = sync;
    }

    public int getCrudStatus() {
        return crudStatus;
    }

    public void setCrudStatus(int crudStatus) {
        this.crudStatus = crudStatus;
    }

    public int getServerCategoryId() {
        return serverCategoryId;
    }

    public void setServerCategoryId(int serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
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
    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    @Override
    public String toString() {
        return categoryName;
    }

    public JSONObject toJSONObject(){

        JSONObject JSONCategory = new JSONObject();

        try {
            JSONCategory.put("categoryId", this.categoryId);
            JSONCategory.put("categoryName", this.categoryName);
            JSONCategory.put("color", this.color);
            JSONCategory.put("icon", this.icon);
            JSONCategory.put("sync", this.sync);
            JSONCategory.put("userId", this.userId);
            JSONCategory.put("email", this.email);
            JSONCategory.put("crud_status", this.crudStatus);
            JSONCategory.put("serverCategoryId", this.serverCategoryId);

            return JSONCategory;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray listToJSONArray(List<Category> categories){
        JSONArray jsonCategories = new JSONArray();
        for (int i = 0; i < categories.size(); i++) {
            JSONObject JSONCategory = categories.get(i).toJSONObject();
            jsonCategories.put(JSONCategory);
        }
        return jsonCategories;
    }

}
