package com.winservices.wingoods.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class ShopInListViewHolder extends RecyclerView.ViewHolder {

    public TextView shopName, shopType, shopAdress, shopPhone, shopEmail;
    public RelativeLayout container;

    public ShopInListViewHolder(View itemView) {
        super(itemView);

        container = itemView.findViewById(R.id.rl_container);
        shopName = itemView.findViewById(R.id.txt_shop_name);
        shopType = itemView.findViewById(R.id.txt_shop_type);
        shopAdress = itemView.findViewById(R.id.txt_shop_adress);
        shopPhone = itemView.findViewById(R.id.txt_shop_phone);
        shopEmail = itemView.findViewById(R.id.txt_shop_email);

    }
}
