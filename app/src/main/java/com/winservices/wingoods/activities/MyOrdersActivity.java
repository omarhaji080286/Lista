package com.winservices.wingoods.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.MyOrdersAdapter;
import com.winservices.wingoods.dbhelpers.OrdersDataManager;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private final String TAG = "MyOrdersActivity";
    private FloatingActionButton fabAddOrder;
    private MyOrdersAdapter myOrdersAdapter;
    private List<Order> orders;
    private TextView txtNoOrders;
    private OrdersDataManager ordersDataManager;
    private SyncReceiverMyOrders syncReceiver;
    private boolean syncTriggeredByUser = false;
    private int orderStatus = Order.NOT_CLOSED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        setTitle(getString(R.string.my_orders));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        syncReceiver = new SyncReceiverMyOrders();

        RecyclerView rvOrders = findViewById(R.id.rv_orders);
        txtNoOrders = findViewById(R.id.txt_no_orders);
        fabAddOrder = findViewById(R.id.fab_add_order);

        orders = new ArrayList<>();
        ordersDataManager = new OrdersDataManager(this);
        orders.addAll(ordersDataManager.getOrders(orderStatus));

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
        fabAddOrder.setOnClickListener(view -> {
            Intent intent = new Intent(MyOrdersActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
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
            case R.id.sync:
                if (NetworkMonitor.checkNetworkConnection(this)) {
                    syncTriggeredByUser = true;
                    SyncHelper.sync(this);
                } else {
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuOngoingOrders:
                orders.clear();
                orderStatus = Order.NOT_CLOSED;
                orders.addAll(ordersDataManager.getOrders(orderStatus));
                myOrdersAdapter.setOrders(orders);
                setTitle(getString(R.string.my_orders));
                updateMessageVisibility();

                break;
            case R.id.menuClosedOrders:
                orders.clear();
                orderStatus = (Order.CLOSED);
                orders.addAll(ordersDataManager.getOrders(orderStatus));
                myOrdersAdapter.setOrders(orders);
                setTitle(getString(R.string.closed_orders_title));
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

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Handler used to execute code on the UI thread
            Handler handler = new Handler();
            handler.post(() -> {

                orders.clear();
                orders.addAll(ordersDataManager.getOrders(orderStatus));
                myOrdersAdapter.setOrders(orders);
                updateMessageVisibility();

                if (syncTriggeredByUser) {
                    Toast.makeText(context, R.string.sync_finished, Toast.LENGTH_SHORT).show();
                    syncTriggeredByUser = false;
                }

                Log.d(TAG, "Sync BroadCast received");
            });
        }
    }


}
