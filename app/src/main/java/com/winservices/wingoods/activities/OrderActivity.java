package com.winservices.wingoods.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.RecyclerItemTouchHelper;
import com.winservices.wingoods.utils.UtilsFunctions;
import com.winservices.wingoods.viewholders.GoodItemViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, View.OnClickListener {

    private final static String TAG = "OrderActivity";
    private GoodsToOrderAdapter goodsToOrderAdapter;
    private int selectedShopId;
    private int serverCategoryIdToOrder;
    private Dialog dialog;
    private Context context;
    private List<CategoryGroup> groupsAdditionalGoods;
    private AdditionalGoodsAdapter additionalGoodsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setTitle(getString(R.string.order_my_list));
        context = this;

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ConstraintLayout container = findViewById(R.id.cl_container);
        RecyclerView rvGoodsToOrder = findViewById(R.id.rv_goods_to_order);
        Button btnAddGood = findViewById(R.id.btn_add_good);
        Button btnOrder = findViewById(R.id.btn_order);
        TextView txtShopName = findViewById(R.id.txt_shop_name);

        selectedShopId = getIntent().getIntExtra(Constants.SELECTED_SHOP_ID, 0);
        Shop shop = getIntent().getParcelableExtra(Constants.SHOP);

        txtShopName.setText(shop.getShopName());

        serverCategoryIdToOrder = getIntent().getIntExtra(Constants.CATEGORY_TO_ORDER, 0);

        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(this);
        List<Good> goodsToOrder = goodsDataProvider.getGoodsToOrderByServerCategoryId(serverCategoryIdToOrder);

        goodsToOrderAdapter = new GoodsToOrderAdapter(this, goodsToOrder);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvGoodsToOrder);

        GridLayoutManager glm = new GridLayoutManager(this, 3);
        rvGoodsToOrder.setLayoutManager(glm);
        rvGoodsToOrder.setAdapter(goodsToOrderAdapter);
        rvGoodsToOrder.setHasFixedSize(true);

        btnOrder.setOnClickListener(this);

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        groupsAdditionalGoods = categoriesDataProvider.getAdditionalGoodsList(0);
        removeGoodsFromList(serverCategoryIdToOrder);

        additionalGoodsAdapter = new AdditionalGoodsAdapter(groupsAdditionalGoods, context);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        final View mView = LayoutInflater.from(context)
                .inflate(R.layout.fragment_additional_goods_to_order, container, false);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCancelable(false);

        btnAddGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupsAdditionalGoods.size()>0) {

                    Button btnCancel = mView.findViewById(R.id.btn_cancel);
                    Button btnAddAdditionalGood = mView.findViewById(R.id.btn_add_additional_good);
                    RecyclerView rvAdditionalGoodsToOrder = mView.findViewById(R.id.rv_additional_goods_to_order);
                    TextView txtNoItems = mView.findViewById(R.id.txt_No_items);

                    if (groupsAdditionalGoods.size()==1 && groupsAdditionalGoods.get(0).getItems().size()==0){
                        txtNoItems.setVisibility(View.VISIBLE);
                        btnAddAdditionalGood.setVisibility(View.GONE);
                    } else {
                        txtNoItems.setVisibility(View.GONE);
                        btnAddAdditionalGood.setVisibility(View.VISIBLE);
                    }

                    dialog.show();

                    LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    rvAdditionalGoodsToOrder.setLayoutManager(llm);
                    rvAdditionalGoodsToOrder.setAdapter(additionalGoodsAdapter);
                    for (int i = additionalGoodsAdapter.getGroups().size() - 1; i >= 0; i--) {
                        if (additionalGoodsAdapter.isGroupExpanded(i)) {
                            return;
                        }
                        additionalGoodsAdapter.toggleGroup(i);
                    }

                    btnAddAdditionalGood.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (additionalGoodsAdapter.getSelectedAdditionalGoods().size() > 0) {
                                dialog.dismiss();
                                goodsToOrderAdapter.addAdditionalGoods(additionalGoodsAdapter.getSelectedAdditionalGoods());
                                removeSelectedAdditionalGoods();
                                additionalGoodsAdapter.selectedAdditionalGoods.clear();
                            } else {
                                Toast.makeText(OrderActivity.this, R.string.no_item_selected, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    Toast.makeText(context, R.string.no_additional_items, Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    private void removeGoodsFromList(int serverCategoryIdToOrder) {

        for (int i = 0; i < groupsAdditionalGoods.size(); i++) {
            CategoryGroup categoryGroup = groupsAdditionalGoods.get(i);
            if (categoryGroup.getServerCategoryId()==serverCategoryIdToOrder){
                groupsAdditionalGoods.get(i).clear();
            }
        }

    }

    public void removeSelectedAdditionalGoods() {
        List<CategoryGroup> groups = new ArrayList<>();
        groups.addAll(groupsAdditionalGoods);
        List<Good> goodsToRemove = additionalGoodsAdapter.getSelectedAdditionalGoods();
        for (int i = 0; i < groups.size(); i++) {
            for (int k = 0; k < goodsToRemove.size(); k++) {
                Good goodToRemove = goodsToRemove.get(k);
                groupsAdditionalGoods.get(i).removeItem(goodToRemove);
            }
        }
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

            root.put("serverUserId", currentUser.getServerUserId() );
            root.put("serverShopId", selectedShopId);
            root.put("statusId", Order.SENT);


            JSONArray jsonGoods = new JSONArray();
            for (int i = 0; i < goodsToOrderAdapter.getGoodsToOrder().size(); i++) {

                Good good = goodsToOrderAdapter.getGoodsToOrder().get(i);
                //good.setServerCategoryId(this);
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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        goodsToOrderAdapter.remove(position);

        if (viewHolder instanceof GoodItemViewHolder) {
            GoodItemViewHolder item = (GoodItemViewHolder) viewHolder;

            GoodsDataProvider goodsDataProvider = new GoodsDataProvider(this);
            Good goodToInsert = goodsDataProvider.getGoodById(item.getGoodId());
            additionalGoodsAdapter.insertGood(goodToInsert);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        goodsToOrderAdapter = null;
        groupsAdditionalGoods = null;
        additionalGoodsAdapter = null;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_order :

                if (goodsToOrderAdapter.getGoodsToOrder().size()>0) {
                    view.setEnabled(false);
                    dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), context, R.string.Registering_order).create();
                    dialog.show();
                    addOrder(context);
                } else {
                    Toast.makeText(context, R.string.empty_order, Toast.LENGTH_SHORT).show();
                }
            break;

        }
    }
}
