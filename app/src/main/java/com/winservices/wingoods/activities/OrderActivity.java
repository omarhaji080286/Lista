package com.winservices.wingoods.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.AdditionalGoodsAdapter;
import com.winservices.wingoods.adapters.GoodsToOrderAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private final static String TAG = "OrderActivity";
    private RecyclerView rvGoodsToOrder;
    private Button btnAddGood, btnOrder;
    private GoodsToOrderAdapter goodsToOrderAdapter;
    private int selectedShopId;
    private int serverCategoryIdToOrder;
    private Dialog dialog;
    private Context context;
    private ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setTitle(getString(R.string.order_my_list));
        context = this;

        if (getSupportActionBar()!=null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        container = findViewById(R.id.cl_container);
        rvGoodsToOrder = findViewById(R.id.rv_goods_to_order);
        btnAddGood = findViewById(R.id.btn_add_good);
        btnOrder = findViewById(R.id.btn_order);

        selectedShopId = getIntent().getIntExtra(Constants.SELECTED_SHOP_ID, 0);

        serverCategoryIdToOrder = getIntent().getIntExtra(Constants.CATEGORY_TO_ORDER, 0);

        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(this);
        List<Good> goodsToOrder = goodsDataProvider.getGoodsToOrderByServerCategoryId(serverCategoryIdToOrder);
        goodsDataProvider.closeDB();

        goodsToOrderAdapter = new GoodsToOrderAdapter(this, goodsToOrder);

        GridLayoutManager glm = new GridLayoutManager(this, 3);
        rvGoodsToOrder.setLayoutManager(glm);
        rvGoodsToOrder.setAdapter(goodsToOrderAdapter);
        rvGoodsToOrder.setHasFixedSize(true);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), context, R.string.Registering_order).create();
                dialog.show();
                addOrder(context);
            }
        });

        btnAddGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context)
                        .inflate(R.layout.fragment_additional_goods_to_order, container, false);

                Button btnCancel = mView.findViewById(R.id.btn_cancel);
                Button btnAddAdditionalGood = mView.findViewById(R.id.btn_add_additional_good);
                RecyclerView rvAdditionalGoodsToOrder = mView.findViewById(R.id.rv_additional_goods_to_order);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
                List<CategoryGroup> groups = categoriesDataProvider.getAdditionalGoodsList(serverCategoryIdToOrder);
                categoriesDataProvider.closeDB();
                AdditionalGoodsAdapter additionalGoodsAdapter = new AdditionalGoodsAdapter(groups, context);

                LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                rvAdditionalGoodsToOrder.setLayoutManager(llm);
                rvAdditionalGoodsToOrder.setAdapter(additionalGoodsAdapter);
                for (int i = additionalGoodsAdapter.getGroups().size()-1; i >=0 ; i--) {
                    if(additionalGoodsAdapter.isGroupExpanded(i)){
                        return;
                    }
                    additionalGoodsAdapter.toggleGroup(i);
                }


                btnAddAdditionalGood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(OrderActivity.this, "add good to the order", Toast.LENGTH_SHORT).show();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

    }

    private void addOrder(final Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_ADD_ORDER,
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
                                    dialog.dismiss();
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    updateOrderedGoods();
                                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //error
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", "" + getJSONForAddOrder());
                    postData.put("language", "" + Locale.getDefault().getLanguage());
                    return postData;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private String getJSONForAddOrder(){
        final JSONObject root = new JSONObject();
        try {

            UsersDataManager usersDataManager = new UsersDataManager(this);
            User currentUser = usersDataManager.getCurrentUser();
            usersDataManager.closeDB();

            root.put("serverUserId", currentUser.getServerUserId() );
            root.put("serverShopId", selectedShopId);
            root.put("currentStatusName", "CREATED");


            JSONArray jsonGoods = new JSONArray();
            for (int i = 0; i < goodsToOrderAdapter.getGoodsToOrder().size(); i++) {

                Good good = goodsToOrderAdapter.getGoodsToOrder().get(i);
                good.setServerCategoryId(this);
                JSONObject JSONGood = good.toJSONObject();
                jsonGoods.put(JSONGood);
            }
            root.put("jsonGoodsToOrder", jsonGoods);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d(TAG, "Can't format JSON");
        }

        return null;
    }

    private void updateOrderedGoods() {
        DataManager dataManager = new DataManager(this);
        for (int i = 0; i < goodsToOrderAdapter.getGoodsToOrder().size(); i++) {
            Good good = goodsToOrderAdapter.getGoodsToOrder().get(i);
            good.setIsOrdered(1);
            dataManager.updateGood(good);
        }
        dataManager.closeDB();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home :
                goToShopsActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToShopsActivity(){
        Intent intent = new Intent(OrderActivity.this, ShopsActivity.class);
        intent.putExtra(Constants.CATEGORY_TO_ORDER,serverCategoryIdToOrder);
        startActivity(intent);
        finish();
    }

}
