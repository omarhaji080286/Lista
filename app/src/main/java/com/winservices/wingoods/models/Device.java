package com.winservices.wingoods.models;

import com.winservices.wingoods.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class Device {

    private String androidVersion;
    private String device;
    private String model;
    private String product;
    private String brand;
    private String buildNumber;
    private String hardware;
    private String manufacturer;
    private String host;
    private String apiLevel;
    private String osVersion;
    private User deviceUser;
    private int userListaVersionCode;

    public Device() {
        this.androidVersion = android.os.Build.VERSION.RELEASE;
        this.device = android.os.Build.DEVICE;
        this.model = android.os.Build.MODEL;
        this.product = android.os.Build.PRODUCT;
        this.brand = android.os.Build.BRAND;
        this.buildNumber = android.os.Build.DISPLAY ;
        this.hardware = android.os.Build.HARDWARE ;
        this.manufacturer = android.os.Build.MANUFACTURER;
        this.host = android.os.Build.HOST;
        this.apiLevel = String.valueOf(android.os.Build.VERSION.SDK_INT);
        this.osVersion = android.os.Build.VERSION.INCREMENTAL;
        this.userListaVersionCode = BuildConfig.VERSION_CODE;

    }


    public JSONObject toJSONObject(){

        JSONObject JSONDevice = new JSONObject();

        try {
            JSONDevice.put("androidVersion", this.androidVersion);
            JSONDevice.put("device", this.device);
            JSONDevice.put("model", this.model);
            JSONDevice.put("product", this.product);
            JSONDevice.put("brand", this.brand);
            JSONDevice.put("buildNumber", this.buildNumber);
            JSONDevice.put("hardware", this.hardware);
            JSONDevice.put("manufacturer", this.manufacturer);
            JSONDevice.put("host", this.host);
            JSONDevice.put("apiLevel", this.apiLevel);
            JSONDevice.put("osVersion", this.osVersion);
            JSONDevice.put("serverUserId", this.deviceUser.getServerUserId());
            JSONDevice.put("userListaVersionCode", String.valueOf(this.userListaVersionCode));

            return JSONDevice;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public User getDeviceUser() {
        return deviceUser;
    }

    public void setDeviceUser(User deviceUser) {
        this.deviceUser = deviceUser;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public String getDevice() {
        return device;
    }

    public String getModel() {
        return model;
    }

    public String getProduct() {
        return product;
    }

    public String getBrand() {
        return brand;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public String getHardware() {
        return hardware;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getHost() {
        return host;
    }

    public String getApiLevel() {
        return apiLevel;
    }

    public String getOsVersion() {
        return osVersion;
    }
}
