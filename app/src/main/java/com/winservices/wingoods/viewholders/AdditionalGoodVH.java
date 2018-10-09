package com.winservices.wingoods.viewholders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.winservices.wingoods.R;


public class AdditionalGoodVH extends ChildViewHolder {

    public CheckBox cbAdditionalGood;

    public AdditionalGoodVH(View itemView) {
        super(itemView);

        cbAdditionalGood = itemView.findViewById(R.id.cb_additional_good);

    }
}
