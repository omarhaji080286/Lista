package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.database.Cursor;

import com.winservices.wingoods.models.Amount;

import java.util.ArrayList;
import java.util.List;

public class AmountsDataManager {

    private final static String TAG = AmountsDataManager.class.getSimpleName();
    private DataBaseHelper db;
    private Context context;

    public AmountsDataManager(Context context) {
        this.db = DataBaseHelper.getInstance(context);
        this.context = context;
    }


    public List<Amount> getAmounts(int amountTypeId) {
        Cursor cursor = db.getAmounts(amountTypeId);
        List<Amount> amounts = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int amountId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
                String amountValue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_AMOUNT_VALUE));
                //int amountTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_AMOUNT_TYPE_ID));
                String amountTypeName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_AMOUNT_TYPE_NAME));

                Amount amount = new Amount(amountId, amountValue, amountTypeId, amountTypeName);

                amounts.add(amount);
            }
            cursor.close();
        }
        return amounts;
    }

    void insertAmount(Amount amount) {

        if (getAmountById(amount.getAmountId()) == null) {
            db.insertAmount(amount);
        } else {
            db.updateAmount(amount);
        }

    }

    private Amount getAmountById(int amountId) {
        Cursor cursor = db.getAmountById(amountId);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToNext();
        //int amountId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper._ID));
        String amountValue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_AMOUNT_VALUE));
        int amountTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_AMOUNT_TYPE_ID));
        String amountTypeName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_AMOUNT_TYPE_NAME));

        return new Amount(amountId, amountValue, amountTypeId, amountTypeName);
    }


}
