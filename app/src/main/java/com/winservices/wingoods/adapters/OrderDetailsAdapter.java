package com.winservices.wingoods.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.winservices.wingoods.models.Good;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Good> goods;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return goods.size();
    }
}
