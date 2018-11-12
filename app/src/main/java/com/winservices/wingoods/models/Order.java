package com.winservices.wingoods.models;


import java.util.Date;

public class Order {

    public static final int SENT = 1;
    public static final int READ = 6;
    public static final int IN_PREPARATION = 2;
    public static final int NOT_SUPPORTED = 3;
    public static final int AVAILABLE = 4;
    public static final int COMPLETED = 5;

    private int serverOrderId;
    private User user;
    private Shop shop;
    private Date creationDate;
    private int orderedGoodsNumber;
    private int statusId;

    public String getStatusName(){
        switch (statusId){
            case SENT :
                return  "SENT";
            case READ:
                return "READ";
            case IN_PREPARATION :
                return "IN PREPARATION";
            case NOT_SUPPORTED :
                return "NOT SUPPORTED";
            case AVAILABLE :
                return "AVAILABLE";
            case COMPLETED :
                return "COMPLETED";
            default:
                return "N/A";
        }
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
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
