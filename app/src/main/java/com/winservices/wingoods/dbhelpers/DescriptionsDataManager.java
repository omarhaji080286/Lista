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

    void insertDesc(Description desc) {
        if (getDescById(desc.getDescId()) == null) {
            db.insertDesc(desc);
        } else {
            db.updateDesc(desc);
        }
    }

    private Description getDescById(int descId) {
        Cursor cursor = db.getDescById(descId);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToNext();
        //int descId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String descValue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DESC_VALUE));
        int dCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_D_CATEGORY_ID));

        return new Description(descId, descValue, dCategoryId);
    }

}
