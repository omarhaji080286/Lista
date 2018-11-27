package com.winservices.wingoods.models;

public class Amount {

    public static final int WEIGHT = 1;
    public static final int VOLUME = 2;
    public static final int CURRENCY = 3;
    public static final int ALL = 4;

    private int amountId;
    private String amountValue;
    private int amountTypeId;
    private String amountTypeName;

    public Amount(int amountId, String amountValue, int amountTypeId, String amountTypeName) {
        this.amountId = amountId;
        this.amountValue = amountValue;
        this.amountTypeId = amountTypeId;
        this.amountTypeName = amountTypeName;
    }

    public int getAmountId() {
        return amountId;
    }

    public void setAmountId(int amountId) {
        this.amountId = amountId;
    }

    public String getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(String amountValue) {
        this.amountValue = amountValue;
    }

    public int getAmountTypeId() {
        return amountTypeId;
    }

    public void setAmountTypeId(int amountTypeId) {
        this.amountTypeId = amountTypeId;
    }

    public String getAmountTypeName() {
        return amountTypeName;
    }

    public void setAmountTypeName(String amountTypeName) {
        this.amountTypeName = amountTypeName;
    }
}
