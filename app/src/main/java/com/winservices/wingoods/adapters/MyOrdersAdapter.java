package com.winservices.wingoods.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.OrderedGood;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.OrderVH;

import org.json.JSONArray;
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
    public void onBindViewHolder( OrderVH holder, int position) {

        final Order order = orders.get(position);

        holder.txtShopName.setText(order.getShop().getShopName());
        holder.txtShopTypeName.setText(order.getShop().getShopType().getShopTypeName());
        holder.txtOrderId.setText(String.valueOf(order.getServerOrderId()));
        holder.txtOrderedItemsNumber.setText(String.valueOf(order.getOrderedGoodsNumber()));
        holder.txtCollectTime.setText(order.getDisplayedCollectTime(context, order.getStartTime()));
        Bitmap bitmap = Shop.getShopImage(context, order.getShop().getServerShopId());
        holder.imgShop.setImageBitmap(bitmap);

        String path = SharedPrefManager.getInstance(context).getImagePath(ShopType.PREFIX_SHOP_TYPE+order.getShop().getShopType().getServerShopTypeId());
        Bitmap bitmapShopType = UtilsFunctions.getPNG(path);
        holder.imgShopType.setImageBitmap(bitmapShopType);

        String dateString = UtilsFunctions.dateToString(order.getCreationDate(), "yyyy-MM-dd HH:mm");
        holder.txtDate.setText(order.getDisplayedCollectTime(context, dateString));

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
                orders.remove(order);
                notifyDataSetChanged();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getOrderedGoods(context, order.getServerOrderId());
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
                if (order.getIsToDeliver()==Order.IS_TO_COLLECT){
                    holder.txtOrderStatus.setText(context.getString(R.string.can_collect));
                } else {
                    holder.txtOrderStatus.setText(R.string.delivery_status);
                }

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

    private void updateGoods(List<OrderedGood> orderedGoods) {

        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        DataManager dataManager = new DataManager(context);

        for (int i = 0; i < orderedGoods.size(); i++) {

            OrderedGood orderedGood = orderedGoods.get(i);

            Log.d(TAG, "Id: " + orderedGood.getServerGoodId() +
                    " - Name: " + orderedGood.getGoodName() +
                    " - status: " + orderedGood.getStatus());

            Good good = goodsDataProvider.getGoodByServerGoodId(orderedGood.getServerGoodId());
            good.setIsOrdered(Good.IS_NOT_ORDERED);
            good.setSync(DataBaseHelper.SYNC_STATUS_FAILED);

            dataManager.updateGood(good);

        }

        SyncHelper.sync(context);

    }


    private void getOrderedGoods(final Context context, final int serverOrderId) {

        final List<OrderedGood> orderedGoods = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_GET_ORDERED_GOODS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                //error in server
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray JSONOrderedGoods = jsonObject.getJSONArray("orderedGoods");

                                for (int i = 0; i < JSONOrderedGoods.length(); i++) {
                                    JSONObject JSONOrderedGood = JSONOrderedGoods.getJSONObject(i);

                                    OrderedGood orderedGood = new OrderedGood();
                                    orderedGood.setServerOrderId(JSONOrderedGood.getInt("server_ordered_good_id"));
                                    orderedGood.setGoodDesc(JSONOrderedGood.getString("good_desc"));
                                    orderedGood.setGoodName(JSONOrderedGood.getString("good_name"));
                                    orderedGood.setServerCategoryId(JSONOrderedGood.getInt("server_category_id"));
                                    orderedGood.setServerGoodId(JSONOrderedGood.getInt("server_good_id"));
                                    orderedGood.setServerShopId(JSONOrderedGood.getInt("server_shop_id"));
                                    orderedGood.setServerUserId(JSONOrderedGood.getInt("server_user_id"));
                                    orderedGood.setStatus(JSONOrderedGood.getInt("status"));

                                    orderedGoods.add(orderedGood);

                                }

                                updateGoods(orderedGoods);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //adding coUser failed
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonData", "" + getJSONForGetOrders(serverOrderId));
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private String getJSONForGetOrders(int serverOrderId) {
        final JSONObject root = new JSONObject();
        try {

            root.put("serverOrderId", serverOrderId);

            return root.toString(1);

        } catch (JSONException e) {
            Log.e(TAG, "Can't format JSON");
        }

        return null;
    }


}
