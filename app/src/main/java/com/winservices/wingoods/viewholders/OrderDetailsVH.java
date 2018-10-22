package com.winservices.wingoods.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.winservices.wingoods.R;

public class OrderDetailsVH extends RecyclerView.ViewHolder {

    public TextView txtGoodName;

    public OrderDetailsVH(View itemView) {
        super(itemView);

        txtGoodName = itemView.findViewById(R.id.txt_good_name);
    }
}
