package com.winservices.wingoods.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class CategoryItemInOverviewViewHolder extends RecyclerView.ViewHolder {

    public TextView categoryName, purchasesNb;
    public ImageView caddy;
    public ImageView categoryIcon;
    public RelativeLayout layoutContainer;

    public CategoryItemInOverviewViewHolder(View itemView) {
        super(itemView);

        categoryName = itemView.findViewById(R.id.txt_category_name_in_overview);
        layoutContainer = itemView.findViewById(R.id.layout_category_in_overiew);
        caddy = itemView.findViewById(R.id.img_caddy);
        categoryIcon = itemView.findViewById(R.id.img_category_icon);
        purchasesNb = itemView.findViewById(R.id.txt_purchases_nb);

    }

    public void clearAnimation()
    {
        layoutContainer.clearAnimation();
    }

}

