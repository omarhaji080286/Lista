package com.winservices.wingoods.viewholders;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;

public class CategoryGroupViewHolder extends GroupViewHolder {

    public TextView categoryName, goodsToBuyNumber;
    public ImageView arrow;
    public LinearLayout container;
    public ImageView categoryIcon, caddy;

    public CategoryGroupViewHolder(View itemView) {
        super(itemView);

        categoryName = itemView.findViewById(R.id.txt_category_name);
        arrow = itemView.findViewById(R.id.img_arrow_in_category);
        container = itemView.findViewById(R.id.item_category_group);
        categoryIcon = itemView.findViewById(R.id.ic_category);
        goodsToBuyNumber = itemView.findViewById(R.id.txt_goods_to_buy_number);
        caddy = itemView.findViewById(R.id.ic_caddy);
    }

    public void setCategoryName(String name) {
        categoryName.setText(name);
    }

    public void setArrow(ImageView arrow) {
        this.arrow = arrow;
    }

    @Override
    public void expand() {
        super.expand();
        RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    @Override
    public void collapse() {
        super.collapse();
        RotateAnimation rotate = new RotateAnimation(360, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

}
