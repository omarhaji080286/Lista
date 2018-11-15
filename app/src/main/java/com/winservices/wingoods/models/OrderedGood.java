package com.winservices.wingoods.models;

public class OrderedGood {

    public final static int UNPROCESSED = 0;
    public final static int PROCESSED = 1;
    public final static int NOT_AVAILABLE = 2;

    private int serverOrderedGoodId;
    private int serverGoodId;
    private int serverCategoryId;
    private int serverOrderId;
    private String goodName;
    private String goodDesc;
    private int serverShopId;
    private int serverUserId;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getServerUserId() {
        return serverUserId;
    }

    public void setServerUserId(int serverUserId) {
        this.serverUserId = serverUserId;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public int getServerOrderedGoodId() {
        return serverOrderedGoodId;
    }

    public void setServerOrderedGoodId(int serverOrderedGoodId) {
        this.serverOrderedGoodId = serverOrderedGoodId;
    }

    public int getServerGoodId() {
        return serverGoodId;
    }

    public void setServerGoodId(int serverGoodId) {
        this.serverGoodId = serverGoodId;
    }

    public int getServerCategoryId() {
        return serverCategoryId;
    }

    public void setServerCategoryId(int serverCategoryId) {
        this.serverCategoryId = serverCategoryId;
    }

    public int getServerOrderId() {
        return serverOrderId;
    }

    public void setServerOrderId(int serverOrderId) {
        this.serverOrderId = serverOrderId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodDesc() {
        return goodDesc;
    }

    public void setGoodDesc(String goodDesc) {
        this.goodDesc = goodDesc;
    }
}
