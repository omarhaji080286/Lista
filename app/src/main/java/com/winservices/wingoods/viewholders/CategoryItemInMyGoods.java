package com.winservices.wingoods.viewholders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class CategoryItemInMyGoods extends RecyclerView.ViewHolder {

    public TextView categoryName;
    public ImageView categoryIcon;
    public RelativeLayout container;

    public CategoryItemInMyGoods(View itemView) {
        super(itemView);

        container = itemView.findViewById(R.id.rlayout_category_in_my_goods);
        categoryName = itemView.findViewById(R.id.txt_category_name_in_my_goods);
        categoryIcon = itemView.findViewById(R.id.img_category_icon_in_my_goods);

    }
}
