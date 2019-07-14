package com.winservices.wingoods.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class ShopInListViewHolder extends RecyclerView.ViewHolder {

    public TextView shopName, shopType, shopPhone, city;
    public LinearLayout container;
    public Button btnOrder;
    public ImageView imgShopIcon;
    public RecyclerView rvDCategories;

    public ShopInListViewHolder(View itemView) {
        super(itemView);

        container = itemView.findViewById(R.id.ll_container);
        imgShopIcon = itemView.findViewById(R.id.img_shop_icon);
        rvDCategories = itemView.findViewById(R.id.rv_d_categories);
        shopName = itemView.findViewById(R.id.txt_shop_name);
        shopType = itemView.findViewById(R.id.txt_shop_type);
        shopPhone = itemView.findViewById(R.id.txt_shop_phone);
        city = itemView.findViewById(R.id.txt_shop_city);
        btnOrder = itemView.findViewById(R.id.btn_order);

    }
}
