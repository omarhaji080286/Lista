package com.winservices.wingoods.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.MyOrdersAdapter;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.OrdersDataManager;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private static final String ORDERS_TYPE = "orders_type";
    private final String TAG = "MyOrdersActivity";
    private FloatingActionButton fabAddOrder;
    private RecyclerView rvOrders;
    private MyOrdersAdapter myOrdersAdapter;
    private List<Order> orders;
    private TextView txtNoOrders;
    private OrdersDataManager ordersDataManager;
    private SyncReceiverMyOrders syncReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        setTitle(getString(R.string.my_orders));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        syncReceiver = new SyncReceiverMyOrders();

        rvOrders = findViewById(R.id.rv_orders);
        txtNoOrders = findViewById(R.id.txt_no_orders);
        fabAddOrder = findViewById(R.id.fab_add_order);

        orders = new ArrayList<>();
        ordersDataManager = new OrdersDataManager(this);
        orders.addAll(ordersDataManager.getOrders(Order.NOT_CLOSED));

        myOrdersAdapter = new MyOrdersAdapter(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvOrders.setLayoutManager(llm);
        rvOrders.setAdapter(myOrdersAdapter);

        myOrdersAdapter.setOrders(orders);
        updateMessageVisibility();

        initFabAddOrder();

    }

    private void updateMessageVisibility() {
        if (orders.size() == 0) {
            txtNoOrders.setVisibility(View.VISIBLE);
        } else {
            txtNoOrders.setVisibility(View.GONE);
        }
    }

    private void initFabAddOrder() {
        fabAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isThereGoodToBuy()) {
                    Intent intent = new Intent(MyOrdersActivity.this, ShopsActivity.class);
                    intent.putExtra(Constants.ORDER_INITIATED, true);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_items_to_buy, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isThereGoodToBuy() {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(this);
        int goodsToBuyNb = goodsDataProvider.getGoodsToBuyNb();
        return (goodsToBuyNb > 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuOngoingOrders:
                orders.clear();
                orders.addAll(ordersDataManager.getOrders(Order.NOT_CLOSED));
                myOrdersAdapter.setOrders(orders);
                updateMessageVisibility();
                break;
            case R.id.menuClosedOrders:
                orders.clear();
                orders.addAll(ordersDataManager.getOrders(Order.CLOSED));
                myOrdersAdapter.setOrders(orders);
                updateMessageVisibility();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(syncReceiver, new IntentFilter(Constants.ACTION_REFRESH_AFTER_SYNC));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncReceiver);
    }

    public class SyncReceiverMyOrders extends BroadcastReceiver {

        private Handler handler; // Handler used to execute code on the UI thread

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Post the UI updating code to our Handler

            this.handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {

                    orders.clear();
                    orders.addAll(ordersDataManager.getOrders(Order.NOT_CLOSED));
                    myOrdersAdapter.setOrders(orders);
                    updateMessageVisibility();

                    Log.d(TAG, "Sync BroadCast received");
                }
            });
        }
    }


}
