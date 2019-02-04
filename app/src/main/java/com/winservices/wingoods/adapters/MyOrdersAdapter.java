package com.winservices.wingoods.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.OrderDetailsActivity;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.OrderVH;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<OrderVH> {

    private Context context;
    private List<Order> orders = new ArrayList<>();

    public MyOrdersAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @Override
    public OrderVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderVH(view);
    }

    @Override
    public void onBindViewHolder(OrderVH holder, int position) {

        final Order order = orders.get(position);

        String dateString = UtilsFunctions.dateToString(order.getCreationDate(), "dd/MM/yyyy HH:mm");

        holder.txtShopName.setText(order.getShop().getShopName());
        holder.txtOrderId.setText(String.valueOf(order.getServerOrderId()));
        holder.txtOrderedItemsNumber.setText(String.valueOf(order.getOrderedGoodsNumber()));
        holder.txtCollectTime.setText(order.getDisplayedCollectTime(context));
        Bitmap bitmap = Shop.getShopImage(context, order.getShop().getServerShopId());
        holder.imgShop.setImageBitmap(bitmap);

        holder.txtDate.setText(dateString);
        holder.txtOrderStatus.setText(order.getStatusName());
        holder.clContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkMonitor.checkNetworkConnection(context)) {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra(Constants.ORDER_ID, order.getServerOrderId());
                    intent.putExtra(Constants.ORDER_STATUS, order.getStatusId());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        switch (order.getStatusId()){
            case Order.REGISTERED :
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked_gray);
                holder.imgAvailable.setImageResource(R.drawable.checked_gray);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtOrderStatus.setText(context.getString(R.string.registered));
                break;
            case Order.READ :
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked);
                holder.imgAvailable.setImageResource(R.drawable.checked_gray);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtOrderStatus.setText(context.getString(R.string.read));

                break;
            case Order.AVAILABLE :
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked);
                holder.imgAvailable.setImageResource(R.drawable.checked);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtOrderStatus.setText(context.getString(R.string.can_collect));

                break;
            case Order.COMPLETED :
                holder.imgRegistered.setVisibility(View.GONE);
                holder.imgRead.setVisibility(View.GONE);
                holder.imgAvailable.setVisibility(View.GONE);
                holder.imgClosedOrNotSuported.setImageResource(R.drawable.completed);
                holder.imgClosedOrNotSuported.setVisibility(View.VISIBLE);
                holder.txtOrderStatus.setText(context.getString(R.string.completed));

                break;
            case Order.NOT_SUPPORTED:
                holder.imgRegistered.setVisibility(View.GONE);
                holder.imgRead.setVisibility(View.GONE);
                holder.imgAvailable.setVisibility(View.GONE);
                holder.imgClosedOrNotSuported.setImageResource(R.drawable.not_supported);
                holder.imgClosedOrNotSuported.setVisibility(View.VISIBLE);
                holder.txtOrderStatus.setText(context.getString(R.string.not_supported));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
