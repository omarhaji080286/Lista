package com.winservices.wingoods.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.CategoriesToOrderAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, View.OnClickListener {

    private final static String TAG = OrderActivity.class.getSimpleName();
    private CategoriesToOrderAdapter categoriesToOrderAdapter;
    private int selectedShopId;
    private boolean orderInitiated;
    private Context context;
    private RecyclerView rvCategoriesToOrder;
    private Dialog dialog;

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

        rvCategoriesToOrder = findViewById(R.id.rvCategoriesToOrder);
        Button btnOrder = findViewById(R.id.btn_order);
        TextView txtShopName = findViewById(R.id.txt_shop_name);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvCategoriesToOrder);

        selectedShopId = getIntent().getIntExtra(Constants.SELECTED_SHOP_ID, 0);
        Shop shop = getIntent().getParcelableExtra(Constants.SHOP);

        txtShopName.setText(shop.getShopName());

        orderInitiated = getIntent().getBooleanExtra(Constants.ORDER_INITIATED, false);

        loadCategoriesToOrder(shop);

        btnOrder.setOnClickListener(this);

    }



    private void loadCategoriesToOrder(Shop shop) {
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
        List<CategoryGroup> categoriesToOrder = categoriesDataProvider.getCategoriesForOrder(shop);

        categoriesToOrderAdapter = new CategoriesToOrderAdapter(categoriesToOrder, this);

        final int GRID_COLUMN_NUMBER = 3;
        GridLayoutManager glm = new GridLayoutManager(this, GRID_COLUMN_NUMBER);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (categoriesToOrderAdapter.getItemViewType(position)) {
                    case 2:
                        return GRID_COLUMN_NUMBER;
                    default:
                        return 1;
                }
            }
        });

        rvCategoriesToOrder.setLayoutManager(glm);
        rvCategoriesToOrder.setAdapter(categoriesToOrderAdapter);
        expandRecyclerView();
    }

    private void expandRecyclerView() {
        for (int i = categoriesToOrderAdapter.getGroups().size() - 1; i >= 0; i--) {
            if (categoriesToOrderAdapter.isGroupExpanded(i)) {
                return;
            }
            categoriesToOrderAdapter.toggleGroup(i);
        }
    }

    private void addOrder(final Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), context, R.string.Registering_order).create();
            dialog.show();
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
                                    dialog.dismiss();
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
                            dialog.dismiss();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
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
            root.put("statusId", Order.REGISTERED);

            JSONArray jsonGoods = new JSONArray();
            for (int i = 0; i < categoriesToOrderAdapter.getGoodsToOrder().size(); i++) {

                Good good = categoriesToOrderAdapter.getGoodsToOrder().get(i);
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
        for (int i = 0; i < categoriesToOrderAdapter.getGoodsToOrder().size(); i++) {
            Good good = categoriesToOrderAdapter.getGoodsToOrder().get(i);
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
        intent.putExtra(Constants.ORDER_INITIATED,true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CategoriesToOrderAdapter.GoodInOrderVH) {
            categoriesToOrderAdapter.removeChildItem(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_order :
                if (categoriesToOrderAdapter.getGoodsToOrderNumber()>0) {
                    if (categoriesToOrderAdapter.getGoodsToComplete().size()==0){
                        addOrder(context);
                    } else {
                        Toast.makeText(context, "need to complete goods", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.empty_order, Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }
}
