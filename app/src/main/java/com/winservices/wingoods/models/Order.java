package com.winservices.wingoods.models;


import android.content.Context;
import android.util.Log;

import com.winservices.wingoods.R;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Order {

    private static final String TAG = Order.class.getSimpleName();

    public static final int REGISTERED = 1;
    public static final int READ = 2;
    public static final int IN_PREPARATION = 3;
    public static final int AVAILABLE = 4;
    public static final int COMPLETED = 5;
    public static final int NOT_SUPPORTED = 6;

    public final static int IS_TO_DELIVER = 1;
    public final static int IS_TO_COLLECT = 0;

    public static final int ALL = 100;
    public static final int NOT_CLOSED = 101;
    public static final int CLOSED = 102;

    private int serverOrderId;
    private User user;
    private Shop shop;
    private Date creationDate;
    private int orderedGoodsNumber;
    private int statusId;
    private String statusName;
    private String startTime;
    private String endTime;
    private int isToDeliver;
    private String userAddress;
    private String userLocation;

    public String getStatusName(){
        switch (statusId){
            case REGISTERED:
                return  "REGISTERED";
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

    /*public String getDisplayedCollectTime(Context context){
        String day = "empty";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

        Date collectDay = null;
        Date today = null;
        try {
            collectDay = sdf.parse(this.startTime);
            today = sdf.parse(UtilsFunctions.dateToString(Calendar.getInstance().getTime(),"yyyy-MM-dd"));

            long diffMilli = collectDay.getTime() - today.getTime();
            String diff = String.valueOf(TimeUnit.DAYS.convert(diffMilli, TimeUnit.MILLISECONDS)).substring(0,1);

            switch (diff){
                case "0":
                    day = context.getResources().getString(R.string.today);
                    break;
                case "1":
                    day = context.getResources().getString(R.string.tomorrow);
                    break;
                default:
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(collectDay);
                    day = UtilsFunctions.getDayOfWeek(context,cal.get(Calendar.DAY_OF_WEEK)) + " " + UtilsFunctions.to2digits(cal.get(Calendar.DAY_OF_MONTH));
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = this.startTime.substring(11,16) + " - " + this.endTime.substring(11,16);

        Log.d(TAG, "DisplayedCollectTime: " + day + " " + time);

        return day + " " + time;
    }*/

    public String getDisplayedCollectTime(Context context, String dateTime){
        String displayedDate = "empty";
        String day = "empty";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

        Date collectDay;
        Date today;
        try {
            collectDay = sdf.parse(dateTime);
            today = sdf.parse(UtilsFunctions.dateToString(Calendar.getInstance().getTime(),"yyyy-MM-dd"));

            long diffMilli = collectDay.getTime() - today.getTime();
            String diff = String.valueOf(TimeUnit.DAYS.convert(diffMilli, TimeUnit.MILLISECONDS)).substring(0,1);
            Calendar cal = Calendar.getInstance();
            switch (diff){
                case "0":
                    day = context.getResources().getString(R.string.today);
                    displayedDate = day;
                    break;
                case "1":
                    day = context.getResources().getString(R.string.tomorrow);
                    displayedDate = day;
                    break;
                default:
                    cal.setTime(collectDay);
                    day = UtilsFunctions.getDayOfWeek(context,cal.get(Calendar.DAY_OF_WEEK)) + " " + UtilsFunctions.to2digits(cal.get(Calendar.DAY_OF_MONTH));
                    String month = UtilsFunctions.dateToString(UtilsFunctions.stringToDate(dateTime),"MM");
                    displayedDate = day + "/" + month;
                    break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = this.startTime.substring(11,16) + " - " + this.endTime.substring(11,16);

        Log.d(TAG, "DisplayedCollectTime: " + day + " " + time);

        return displayedDate;// + " " + time*
    }



    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public int getIsToDeliver() {
        return isToDeliver;
    }

    public void setIsToDeliver(int isToDeliver) {
        this.isToDeliver = isToDeliver;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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
