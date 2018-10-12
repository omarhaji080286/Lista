package com.winservices.wingoods.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class OrderVH extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtShopName, txtOrderedItemsNumber, txtDate, txtOrderStatus;
    public ImageView imgShop, arrowRight, imgOrderStatus;
    public ConstraintLayout clContainer;

    public OrderVH(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.txt_order_id);
        txtShopName = itemView.findViewById(R.id.txt_shop_name);
        txtOrderedItemsNumber = itemView.findViewById(R.id.txt_ordered_items_number);
        imgShop= itemView.findViewById(R.id.img_shop);
        arrowRight= itemView.findViewById(R.id.ic_arrow_right_black);
        txtDate = itemView.findViewById(R.id.txt_date);
        txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
        imgOrderStatus = itemView.findViewById(R.id.img_order_status);
        clContainer = itemView.findViewById(R.id.cl_order_container);

    }
}
