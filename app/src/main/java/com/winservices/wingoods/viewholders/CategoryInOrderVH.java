package com.winservices.wingoods.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;


public class CategoryInOrderVH extends GroupViewHolder {

    public ImageView icon;
    public TextView txtCategoryName;

    public CategoryInOrderVH(View itemView) {
        super(itemView);

        icon = itemView.findViewById(R.id.img_category_icon);
        txtCategoryName = itemView.findViewById(R.id.txt_category_name);

    }
}
