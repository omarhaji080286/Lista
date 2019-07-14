package com.winservices.wingoods.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.winservices.wingoods.R;

public class OrderDetailsVH extends RecyclerView.ViewHolder {

    public TextView txtGoodName;
    public ImageView imgStatus;

    public OrderDetailsVH(View itemView) {
        super(itemView);

        txtGoodName = itemView.findViewById(R.id.txt_good_name);
        imgStatus = itemView.findViewById(R.id.img_status);
    }
}
