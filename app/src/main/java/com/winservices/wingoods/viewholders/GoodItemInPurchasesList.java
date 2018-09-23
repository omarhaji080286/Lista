package com.winservices.wingoods.viewholders;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.winservices.wingoods.R;

public class GoodItemInPurchasesList extends ChildViewHolder {

    public TextView goodName, goodDesc;
    public CardView container;

    public GoodItemInPurchasesList(View itemView) {
        super(itemView);

        goodName = itemView.findViewById(R.id.txt_good_name);
        goodDesc = itemView.findViewById(R.id.txt_good_desc);
        container = itemView.findViewById(R.id.item_good_in_purchases_list);
    }
}
