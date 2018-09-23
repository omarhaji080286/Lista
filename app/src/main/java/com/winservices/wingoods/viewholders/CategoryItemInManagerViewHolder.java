package com.winservices.wingoods.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.winservices.wingoods.R;


public class CategoryItemInManagerViewHolder extends RecyclerView.ViewHolder {

    private TextView categoryName;
    public ConstraintLayout foreGround;

    public CategoryItemInManagerViewHolder(View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.txt_category_name_in_item);
        foreGround = itemView.findViewById(R.id.category_item_in_manager);
    }

    public void setCategoryName(String name) {
        this.categoryName.setText(name);
    }
}
