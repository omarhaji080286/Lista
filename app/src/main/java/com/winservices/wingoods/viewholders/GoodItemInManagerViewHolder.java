package com.winservices.wingoods.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.winservices.wingoods.R;

public class GoodItemInManagerViewHolder extends RecyclerView.ViewHolder {

    private TextView goodName, categoryName;
    private ConstraintLayout foreGround;

    public GoodItemInManagerViewHolder(View itemView) {
        super(itemView);
        goodName = itemView.findViewById(R.id.txt_good_name);
        categoryName = itemView.findViewById(R.id.txt_category_name);
        foreGround = itemView.findViewById(R.id.good_item_in_manager);
    }

    public void setGoodName(String name) {
        this.goodName.setText(name);
    }

    public void setCategoryName(String name) {
        this.categoryName.setText(name);
    }

    public ConstraintLayout getForeGround() {
        return foreGround;
    }
}
