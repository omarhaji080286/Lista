package com.winservices.wingoods.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

import java.util.List;

public class GoodsToOrderAdapter extends RecyclerView.Adapter<GoodItemViewHolder> {

    private List<Good> goodsToOrder;
    private Context context;

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

    }

    @Override
    public int getItemCount() {
        return goodsToOrder.size();
    }

    public List<Good> getGoodsToOrder() {
        return goodsToOrder;
    }
}
