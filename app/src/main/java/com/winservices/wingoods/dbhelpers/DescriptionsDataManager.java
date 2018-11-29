package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.Description;

public class DescriptionsDataManager {

    private final static String TAG = AmountsDataManager.class.getSimpleName();
    private DataBaseHelper db;
    private Context context;

    public DescriptionsDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        this.context = context;
    }

    public void insertDesc(Description desc) {
        if (getDescByServerId(desc.getDescId()) == null) {
            db.insertDesc(desc);
        } else {
            db.updateDesc(desc);
        }
    }

    public void insertUserDesc(Description desc){
        if (getUserDescByValue(desc.getDescValue()) == null) {
            db.insertDesc(desc);
        }
    }

    private Description getDescByServerId(int descId) {
        Cursor cursor = db.getDescByServerId(descId);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToNext();
        //int descId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DESC_ID));
        int deviceDescId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String descValue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DESC_VALUE));
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

        Description desc = new Description(descId, descValue, dCategoryId);
        desc.setDeviceDescId(deviceDescId);
        return desc;
    }

    private Description getUserDescByValue(String descValue) {
        Cursor cursor = db.getUserDescByValue(descValue);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToNext();
        int deviceDescId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        int descId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DESC_ID));
        //String descValue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DESC_VALUE));
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

        Description desc = new Description(descId, descValue, dCategoryId);
        desc.setDeviceDescId(deviceDescId);

        return desc;
    }



    public String[] getDescriptions(int dCategoryID) {
        Cursor cursor = db.getDescriptions(dCategoryID);
        String[] descriptions = new String[cursor.getCount()];
        int i = 0;
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int deviceDescId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                int descId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DESC_ID));
                String descValue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DESC_VALUE));
                int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

                Description desc = new Description(descId, descValue, dCategoryId);
                desc.setDeviceDescId(deviceDescId);
                descriptions[i] = desc.getDescValue();
                i = i + 1;
            }
            cursor.close();
        }
        return descriptions;
    }

    public static class InsertDescription implements Runnable{

        private Description description;
        private Context context;

        public InsertDescription(Context context, Description description){
            this.description = description;
            this.context = context;
        }

        @Override
        public void run() {
            DescriptionsDataManager descriptionsDataManager = new DescriptionsDataManager(context);
            descriptionsDataManager.insertUserDesc(description);
        }
    }

}
