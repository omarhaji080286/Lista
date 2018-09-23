package com.winservices.wingoods.viewholders;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.winservices.wingoods.R;


public class LevelGroupViewHolder extends GroupViewHolder {

    private TextView levelName;
    private LinearLayout linearLayout;
    private ImageView arrow;

    public LevelGroupViewHolder(View itemView) {
        super(itemView);

        levelName = (TextView) itemView.findViewById(R.id.txt_level_name);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.item_level_group);
        arrow = (ImageView) itemView.findViewById(R.id.img_arrow_in_overview);
    }

    public void setLevelName(String name){
        levelName.setText(name);
    }

    public void setBackGround(Drawable drawable){
        linearLayout.setBackground(drawable);
    }

    @Override
    public void expand() {
        super.expand();
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    @Override
    public void collapse() {
        super.collapse();
        RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
