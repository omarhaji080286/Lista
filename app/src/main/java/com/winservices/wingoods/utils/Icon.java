package com.winservices.wingoods.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.winservices.wingoods.R;

public class Icon {

    private Context context;

    public Icon(Context context) {
        this.context = context;
    }

    public int getIcon(int crudStatus){
        int iconId;
        TypedArray ta = context.getResources().obtainTypedArray(R.array.icons);
        int[] icons = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            icons[i] = ta.getResourceId(i,R.drawable.fruit);
        }
        ta.recycle();

        int position = crudStatus/10 - 1;

        return icons[position];

    }

}
