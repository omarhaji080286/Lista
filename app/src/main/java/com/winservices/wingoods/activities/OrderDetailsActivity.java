package com.winservices.wingoods.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.OrderDetailsAdapter;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.OrderedGood;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "OrderDetailsActivity";
    private RecyclerView rvOrderDetails;
    private List<OrderedGood> orderedGoods;
    private Dialog dialog;
    private int serverOrderId;
    private int orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        setTitle(getString(R.string.order_details));

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rvOrderDetails = findViewById(R.id.rv_order_goods);

        orderedGoods = new ArrayList<>();

        serverOrderId = getIntent().getIntExtra(Constants.ORDER_ID, 0);
        orderStatus = getIntent().getIntExtra(Constants.ORDER_STATUS, 0);

        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), this, R.string.loading).create();
        dialog.show();
        getOrderedGoods(this);


    }

    private void completeOrder(final int serverOrderId) {
        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), this, R.string.loading).create();
        dialog.show();
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
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Order completed", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
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
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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

    private void setRecyclerViewOrderedGoods() {
        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(this, orderedGoods);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvOrderDetails.setLayoutManager(llm);
        rvOrderDetails.setAdapter(orderDetailsAdapter);
    }

    private void getOrderedGoods(final Context context) {

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

                                setRecyclerViewOrderedGoods();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                isYourOrderComplete();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        isYourOrderComplete();
    }

    private void isYourOrderComplete() {
        if ( orderStatus == Order.AVAILABLE ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(this));
            builder.setMessage(R.string.did_you_get_your_order);
            builder.setTitle(R.string.order_completed);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    completeOrder(serverOrderId);
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton(R.string.not_yet, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    finish();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderedGoods = null;
    }

}
