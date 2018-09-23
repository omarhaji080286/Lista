package com.winservices.wingoods.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class CityInFilterShopsViewHolder extends RecyclerView.ViewHolder {

    public TextView cityName;
    public CheckBox checkBoxCity;

    public CityInFilterShopsViewHolder(View itemView) {
        super(itemView);

        cityName = itemView.findViewById(R.id.txt_city_name);
        checkBoxCity = itemView.findViewById(R.id.cb_city);

    }
}
