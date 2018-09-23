package com.winservices.wingoods.models;

import java.io.Serializable;

public class ShopType implements Serializable {

    private int serverShopTypeId;
    private String shopTypeName;

    public ShopType(int serverShopTypeId, String shopTypeName) {
        this.serverShopTypeId = serverShopTypeId;
        this.shopTypeName = shopTypeName;
    }

    public int getServerShopTypeId() {
        return serverShopTypeId;
    }

    public void setServerShopTypeId(int serverShopTypeId) {
        this.serverShopTypeId = serverShopTypeId;
    }

    public String getShopTypeName() {
        return shopTypeName;
    }

    public void setShopTypeName(String shopTypeName) {
        this.shopTypeName = shopTypeName;
    }
}
