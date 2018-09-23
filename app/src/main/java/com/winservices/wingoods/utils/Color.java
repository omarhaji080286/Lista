package com.winservices.wingoods.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.winservices.wingoods.R;

import java.util.Random;

public class Color {

public static int getRandomColor(Context context){

    //Getting the color resource id
    TypedArray ta = context.getResources().obtainTypedArray(R.array.colors);
    int[] colors = new int[ta.length()];
    for (int i = 0; i < ta.length(); i++) {
        colors[i] = ta.getResourceId(i,R.color.deepOrange);
    }
    ta.recycle();

    Random r = new Random();
    int Low = 0;
    int High = colors.length-1;
    int randomColorIndex = r.nextInt(High-Low) + Low;

    return colors[randomColorIndex];
}

}
