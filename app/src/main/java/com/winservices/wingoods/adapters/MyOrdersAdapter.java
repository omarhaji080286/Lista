package com.winservices.wingoods.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.MyOrdersActivity;
import com.winservices.wingoods.activities.OrderDetailsActivity;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.OrdersDataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.OrderVH;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersAdapter extends RecyclerView.Adapter<OrderVH> {

    private static final String TAG = MyOrdersActivity.class.getSimpleName();
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
        holder.txtShopTypeName.setText(order.getShop().getShopType().getShopTypeName());
        holder.txtOrderId.setText(String.valueOf(order.getServerOrderId()));
        holder.txtOrderedItemsNumber.setText(String.valueOf(order.getOrderedGoodsNumber()));
        holder.txtCollectTime.setText(order.getDisplayedCollectTime(context));
        Bitmap bitmap = Shop.getShopImage(context, order.getShop().getServerShopId());
        holder.imgShop.setImageBitmap(bitmap);

        String path = SharedPrefManager.getInstance(context).getImagePath(ShopType.PREFIX_SHOP_TYPE+order.getShop().getShopType().getServerShopTypeId());
        Bitmap bitmapShopType = UtilsFunctions.getOrientedBitmap(path);
        holder.imgShopType.setImageBitmap(bitmapShopType);


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

        holder.btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        completeOrder(order.getServerOrderId());
                    }
                });
            }
        });



        switch (order.getStatusId()){
            case Order.REGISTERED :
                holder.btnCompleteOrder.setVisibility(View.GONE);
                holder.imgRegistered.setVisibility(View.VISIBLE);
                holder.imgRead.setVisibility(View.VISIBLE);
                holder.imgAvailable.setVisibility(View.VISIBLE);
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked_gray);
                holder.imgAvailable.setImageResource(R.drawable.checked_gray);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtOrderStatus.setText(context.getString(R.string.registered));
                break;
            case Order.READ :
                holder.btnCompleteOrder.setVisibility(View.GONE);
                holder.imgRegistered.setVisibility(View.VISIBLE);
                holder.imgRead.setVisibility(View.VISIBLE);
                holder.imgAvailable.setVisibility(View.VISIBLE);
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked);
                holder.imgAvailable.setImageResource(R.drawable.checked_gray);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtOrderStatus.setText(context.getString(R.string.read));

                break;
            case Order.AVAILABLE :
                holder.btnCompleteOrder.setVisibility(View.GONE);
                holder.imgRegistered.setVisibility(View.VISIBLE);
                holder.imgRead.setVisibility(View.VISIBLE);
                holder.imgAvailable.setVisibility(View.VISIBLE);
                holder.imgRegistered.setImageResource(R.drawable.checked);
                holder.imgRead.setImageResource(R.drawable.checked);
                holder.imgAvailable.setImageResource(R.drawable.checked);
                holder.imgClosedOrNotSuported.setVisibility(View.GONE);
                holder.txtOrderStatus.setText(context.getString(R.string.can_collect));

                break;
            case Order.COMPLETED :
                holder.btnCompleteOrder.setVisibility(View.GONE);
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
                holder.btnCompleteOrder.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void completeOrder(final int serverOrderId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_COMPLETE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                //error in server
                                Log.d(TAG, "onResponse error: " + message);
                            } else {
                                Log.d(TAG, "order completed: ");
                            }
                            SyncHelper.sync(context);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonRequest", "" + getJSONForUpdateOrder(serverOrderId));
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private String getJSONForUpdateOrder(int serverOrderId) {
        final JSONObject root = new JSONObject();
        try {

            root.put("server_order_id", serverOrderId);
            root.put("status_id", Order.COMPLETED);

            return root.toString(1);

        } catch (JSONException e) {
            Log.e(TAG, "Can't format JSON");
        }

        return null;
    }


}
