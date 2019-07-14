package com.winservices.wingoods.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winservices.wingoods.R;
import com.winservices.wingoods.models.OrderedGood;
import com.winservices.wingoods.viewholders.OrderDetailsVH;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsVH> {

    private Context context;
    private List<OrderedGood> orderedGoods;

    public OrderDetailsAdapter(Context context, List<OrderedGood> goods) {
        this.context = context;
        this.orderedGoods = goods;
    }


    @Override
    public OrderDetailsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_good_in_order_details, parent, false);
        return new OrderDetailsVH(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailsVH holder, int position) {

        OrderedGood orderedGood = orderedGoods.get(position);

        String goodText;
        if (orderedGood.getGoodDesc().equals("")) {
            goodText = orderedGood.getGoodName();
        } else {
            goodText = orderedGood.getGoodName() + orderedGood.getGoodDesc();
        }

        holder.txtGoodName.setText(goodText);

        switch (orderedGood.getStatus()) {
            case OrderedGood.PROCESSED:
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.imgStatus.setImageResource(R.drawable.check);
                break;
            case OrderedGood.NOT_AVAILABLE:
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.imgStatus.setImageResource(R.drawable.cross);
                break;
            default:
                holder.imgStatus.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return orderedGoods.size();
    }
}
