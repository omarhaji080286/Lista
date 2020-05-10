package com.winservices.wingoods.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Shop implements Parcelable {

    public static final int IS_DELIVERING_ONLY = 2;
    public static final int IS_DELIVERING = 1;
    public static final int IS_NOT_DELIVERING = 0;
    //public static final String DEFAULT_IMAGE = "defaultImage";
    public static final String PREFIX_SHOP = "shop_";
    public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
    private int serverShopId;
    private String shopName;
    private String shopAdress;
    private String shopEmail;
    private String shopPhone;
    private double longitude;
    private double latitude;
    private ShopType shopType;
    private City city;
    private Country country;
    private String markerId;
    private List<DefaultCategory> defaultCategories;
    private String openingTime;
    private String closingTime;
    private int visibility;
    private int isDelivering;
    private int deliveryDelay;
    private List<WeekDayOff> weekDaysOff;
    private List<DateOff> datesOff;
    private String facebookUrl;
    private String websiteUrl;

    public Shop(Parcel input) {
        this.serverShopId = input.readInt();
        this.shopName = input.readString();
        this.shopAdress = input.readString();
        this.shopEmail = input.readString();
        this.shopPhone = input.readString();
        this.longitude = input.readDouble();
        this.latitude = input.readDouble();
        this.shopType = (ShopType) input.readSerializable();
        this.city = (City) input.readSerializable();
        this.country = (Country) input.readSerializable();
        this.visibility = input.readInt();
        this.isDelivering = input.readInt();
        this.facebookUrl = input.readString();
        this.websiteUrl = input.readString();
        this.defaultCategories = new ArrayList<>();
        input.readTypedList(defaultCategories, DefaultCategory.CREATOR);
        this.weekDaysOff = new ArrayList<>();
        input.readTypedList(weekDaysOff, WeekDayOff.CREATOR);
        this.datesOff = new ArrayList<>();
        input.readTypedList(datesOff, DateOff.CREATOR);

    }

    public Shop() {
    }

    /*public static Bitmap getShopImage(Context context, int serverShopId) {
        String imagePath = SharedPrefManager.getInstance(context).getImagePath(Shop.PREFIX_SHOP + serverShopId);
        if (imagePath != null) {
            return UtilsFunctions.getOrientedBitmap(imagePath);
        }
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_shop_image);
    }*/

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public List<WeekDayOff> getWeekDaysOff() {
        return weekDaysOff;
    }

    public void setWeekDaysOff(List<WeekDayOff> weekDaysOff) {
        this.weekDaysOff = weekDaysOff;
    }

    public List<DateOff> getDatesOff() {
        return datesOff;
    }

    public void setDatesOff(List<DateOff> datesOff) {
        this.datesOff = datesOff;
    }

    public List<DefaultCategory> getDefaultCategories() {
        return defaultCategories;
    }

    public void setDefaultCategories(List<DefaultCategory> defaultCategories) {
        this.defaultCategories = defaultCategories;
    }

    public int getDeliveryDelay() {
        return deliveryDelay;
    }

    public void setDeliveryDelay(int deliveryDelay) {
        this.deliveryDelay = deliveryDelay;
    }

    public int getIsDelivering() {
        return isDelivering;
    }

    public void setIsDelivering(int isDelivering) {
        this.isDelivering = isDelivering;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
    }

    public int getServerShopId() {
        return serverShopId;
    }

    public void setServerShopId(int serverShopId) {
        this.serverShopId = serverShopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAdress() {
        return shopAdress;
    }

    public void setShopAdress(String shopAdress) {
        this.shopAdress = shopAdress;
    }

    public String getShopEmail() {
        return shopEmail;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(serverShopId);
        parcel.writeString(shopName);
        parcel.writeString(shopAdress);
        parcel.writeString(shopEmail);
        parcel.writeString(shopPhone);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeSerializable(shopType);
        parcel.writeSerializable(city);
        parcel.writeSerializable(country);
        parcel.writeInt(visibility);
        parcel.writeInt(isDelivering);
        parcel.writeTypedList(defaultCategories);
        parcel.writeTypedList(weekDaysOff);
        parcel.writeTypedList(datesOff);
        parcel.writeString(facebookUrl);
        parcel.writeString(websiteUrl);

    }

    public Calendar[] getNotWorkedDays() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

        int totalSize;
        int calendarSize = datesOff.size();

        Calendar[] dayOffCalendar = getDayOffCalendar();

        totalSize = calendarSize + dayOffCalendar.length + this.getDeliveryDelay();
        Calendar[] daysOff;
        if (isTodayExcluded()){
            daysOff = new Calendar[totalSize+1];
        } else {
            daysOff = new Calendar[totalSize];
        }


        int i;
        for (i = 0; i < calendarSize; i++) {
            Calendar notWorkedDate;
            Date date;
            try {
                date = sdf.parse(datesOff.get(i).getDateOffValue());
                notWorkedDate = dateToCalendar(date);
                daysOff[i] = notWorkedDate;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        System.arraycopy(dayOffCalendar, 0, daysOff, i, dayOffCalendar.length);

        //exclude delay days
        int k = datesOff.size() + dayOffCalendar.length;
        Calendar c = Calendar.getInstance();
        int delay = this.getDeliveryDelay();
        for (int j = 0; j < delay; j++) {
            daysOff[k] = dateToCalendar(c.getTime());
            c.add(Calendar.DATE, 1);
            k++;
        }

        //check if today must be excluded (2 hours left to shop closing)
        if (isTodayExcluded()){
            daysOff[totalSize] = Calendar.getInstance();
        }

        return daysOff;
    }

    private Calendar[] getDayOffCalendar() {

        List<WeekDayOff> daysOff = this.getWeekDaysOff();

        int totalDaysOff = daysOff.size() * 5;
        Calendar[] daysOffArray = new Calendar[totalDaysOff];

        int j = 0;
        for (int i = 0; i < daysOff.size(); i++) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, daysOff.get(i).getDayOff());
            daysOffArray[j] = dateToCalendar(c.getTime());
            c.add(Calendar.DATE, 7);
            daysOffArray[j + 1] = dateToCalendar(c.getTime());
            c.add(Calendar.DATE, 7);
            daysOffArray[j + 2] = dateToCalendar(c.getTime());
            c.add(Calendar.DATE, 7);
            daysOffArray[j + 3] = dateToCalendar(c.getTime());
            c.add(Calendar.DATE, 7);
            daysOffArray[j + 4] = dateToCalendar(c.getTime());
            j = j + 5;
        }

        return daysOffArray;
    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private boolean isTodayExcluded(){
        int closingHour = Integer.parseInt(this.closingTime.substring(0,2));
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);

        return currentHourIn24Format > closingHour - 2;
    }

}
