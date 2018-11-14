package com.winservices.wingoods.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.MyOrdersAdapter;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrdersActivity extends AppCompatActivity {

    private final String TAG = "MyOrdersActivity";
    private RecyclerView rvOrders;
    private MyOrdersAdapter myOrdersAdapter;
    private List<Order> orders;
    private Dialog dialog;
    private TextView txtNoOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        setTitle(getString(R.string.my_orders));
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvOrders = findViewById(R.id.rv_orders);
        txtNoOrders = findViewById(R.id.txt_no_orders);

        orders = new ArrayList<>();

        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), this, R.string.loading).create();
        dialog.show();
        getOrders(this);
    }

    private void setRecyclerViewOrders() {
        myOrdersAdapter = new MyOrdersAdapter(this, orders);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvOrders.setLayoutManager(llm);
        rvOrders.setAdapter(myOrdersAdapter);
    }

    private void getOrders(final Context context) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_GET_ORDERS,
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
                                JSONArray JSONOrders = jsonObject.getJSONArray("orders");

                                for (int i = 0; i < JSONOrders.length(); i++) {
                                    JSONObject JSONShop = JSONOrders.getJSONObject(i);

                                    int serverShopTypeId = JSONShop.getInt("server_shop_type_id");
                                    String shopTypeName = JSONShop.getString("shop_type_name");
                                    ShopType shopType = new ShopType(serverShopTypeId, shopTypeName);

                                    Shop shop = new Shop();
                                    shop.setServerShopId(JSONShop.getInt("server_shop_id"));
                                    shop.setShopName(JSONShop.getString("shop_name"));
                                    shop.setShopAdress(JSONShop.getString("shop_adress"));
                                    shop.setShopEmail(JSONShop.getString("shop_email"));
                                    shop.setShopPhone(JSONShop.getString("shop_phone"));
                                    shop.setLongitude(JSONShop.getDouble("longitude"));
                                    shop.setLatitude(JSONShop.getDouble("latitude"));

                                    shop.setShopType(shopType);

                                    String creationDateString = JSONShop.getString("creation_date");

                                    Date date = UtilsFunctions.stringToDate(creationDateString);

                                    Order order = new Order();
                                    order.setServerOrderId(JSONShop.getInt("server_order_id"));
                                    order.setCreationDate(date);
                                    order.setOrderedGoodsNumber(JSONShop.getInt("ordered_goods_number"));
                                    order.setStatusId(JSONShop.getInt("status_id"));
                                    order.setShop(shop);

                                    orders.add(order);

                                }

                                if (orders.size()>0) {
                                    setRecyclerViewOrders();
                                } else {
                                    txtNoOrders.setVisibility(View.VISIBLE);
                                }

                                dialog.dismiss();

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
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonData", ""+getJSONForGetOrders() );
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private String getJSONForGetOrders(){
        final JSONObject root = new JSONObject();
        try {

            UsersDataManager usersDataManager = new UsersDataManager(this);
            User currentUser = usersDataManager.getCurrentUser();

            root.put("serverUserId", currentUser.getServerUserId() );

            return root.toString(1);

        } catch (JSONException e) {
            Log.e(TAG, "Can't format JSON");
        }

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
