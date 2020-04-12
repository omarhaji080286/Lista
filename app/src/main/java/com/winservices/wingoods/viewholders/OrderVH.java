package com.winservices.wingoods.viewholders;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class OrderVH extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtShopName, txtOrderedItemsNumber, txtDate, txtOrderStatus, txtCollectTime, txtShopTypeName;
    public ImageView imgShop, arrowRight, imgRegistered, imgRead, imgAvailable, imgClosedOrNotSuported, imgShopType, imgDelivery;
    public CardView clContainer;
    public Button btnCompleteOrder;

    public OrderVH(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.txt_order_id);
        txtShopName = itemView.findViewById(R.id.txt_shop_name);
        txtOrderedItemsNumber = itemView.findViewById(R.id.txt_ordered_items_number);
        imgShop= itemView.findViewById(R.id.img_shop);
        arrowRight= itemView.findViewById(R.id.ic_arrow_right_black);
        txtDate = itemView.findViewById(R.id.txt_date);
        txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
        clContainer = itemView.findViewById(R.id.cl_order_container);
        txtCollectTime = itemView.findViewById(R.id.txt_collect_time);
        imgRegistered = itemView.findViewById(R.id.imgRegistered);
        imgRead = itemView.findViewById(R.id.imgRead);
        imgAvailable = itemView.findViewById(R.id.imgAvailable);
        imgClosedOrNotSuported = itemView.findViewById(R.id.imgClosedOrNotSuported);
        btnCompleteOrder = itemView.findViewById(R.id.btnCompleteOrder);
        txtShopTypeName = itemView.findViewById(R.id.txtShopTypeName);
        imgShopType= itemView.findViewById(R.id.imgShopType);
        imgDelivery = itemView.findViewById(R.id.imgDelivery);

    }
}
