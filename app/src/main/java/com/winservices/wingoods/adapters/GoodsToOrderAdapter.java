package com.winservices.wingoods.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

import java.util.List;

public class GoodsToOrderAdapter extends RecyclerView.Adapter<GoodItemViewHolder> {

    private List<Good> goodsToOrder;
    private Context context;
    private int lastPosition = -1;

    public GoodsToOrderAdapter(Context context, List<Good> goodsToOrder) {
        this.goodsToOrder = goodsToOrder;
        this.context = context;
    }

    @Override
    public GoodItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_good, parent, false);
        return new GoodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodItemViewHolder holder, int position) {

        Good good = goodsToOrder.get(position);

        holder.goodName.setText(good.getGoodName());
        holder.goodDescription.setText(good.getGoodDesc());

        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return goodsToOrder.size();
    }

    public List<Good> getGoodsToOrder() {
        return goodsToOrder;
    }

    public void addAdditionalGoods(List<Good> additionalGoods){
        this.goodsToOrder.addAll(additionalGoods);
        notifyDataSetChanged();
    }

    public void remove(int position){
        this.goodsToOrder.remove(position);
        notifyDataSetChanged();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
