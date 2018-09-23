package com.winservices.wingoods.viewholders;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;

public class CategoryItemInPurchasesList extends GroupViewHolder {

    public TextView categoryName;
    public LinearLayout container;
    public ImageView icCategory;
    public ImageView arrow;

    public CategoryItemInPurchasesList(View itemView) {
        super(itemView);

        icCategory = itemView.findViewById(R.id.ic_category_in_purchases_list);
        categoryName = itemView.findViewById(R.id.txt_category_name);
        container = itemView.findViewById(R.id.item_category_in_purchases_list);
        arrow = itemView.findViewById(R.id.img_arrow_in_purchases_list);

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
