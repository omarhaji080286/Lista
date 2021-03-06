package com.winservices.wingoods.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.winservices.wingoods.R;

public class GoodItemViewHolder extends ChildViewHolder{

    private int goodId;
    private int categoryId;
    private int level;
    private boolean isToBuy;

    public boolean isToBuy() {
        return isToBuy;
    }

    public void setToBuy(boolean toBuy) {
        isToBuy = toBuy;
    }

    public TextView goodName, goodDescription;
    //private RelativeLayout viewBackground;
    public RelativeLayout viewForeground;
    public ImageView cart, cartRemove;

    public GoodItemViewHolder(View itemView) {
        super(itemView);
        goodName = itemView.findViewById(R.id.txt_good_name);
        goodDescription = itemView.findViewById(R.id.txt_good_desc);
        //viewBackground =  itemView.findViewById(R.id.item_good_view_background);
        viewForeground=  itemView.findViewById(R.id.item_good);
        cart = itemView.findViewById(R.id.img_cart_in_good);
        cartRemove = itemView.findViewById(R.id.ic_delete);


    }

    public void setGoodName(String name) {
        goodName.setText(name);
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
