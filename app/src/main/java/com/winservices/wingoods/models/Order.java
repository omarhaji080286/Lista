package com.winservices.wingoods.models;


import java.util.Date;

public class Order {

    private int serverOrderId;
    private User user;
    private Shop shop;
    private Date creationDate;
    private int orderedGoodsNumber;
    private String currentStatusName;

    public String getCurrentStatusName() {
        return currentStatusName;
    }

    public void setCurrentStatusName(String currentStatusName) {
        this.currentStatusName = currentStatusName;
    }

    public int getOrderedGoodsNumber() {
        return orderedGoodsNumber;
    }

    public void setOrderedGoodsNumber(int orderedGoodsNumber) {
        this.orderedGoodsNumber = orderedGoodsNumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getServerOrderId() {
        return serverOrderId;
    }

    public void setServerOrderId(int serverOrderId) {
        this.serverOrderId = serverOrderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
