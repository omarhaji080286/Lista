package com.winservices.wingoods.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.GoodsToOrderAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;

import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView rvGoodsToOrder;
    private Button btnSelectShops, btnAddGood, btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setTitle(getString(R.string.order_my_list));

        if (getSupportActionBar()!=null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rvGoodsToOrder = findViewById(R.id.rv_goods_to_order);
        btnSelectShops = findViewById(R.id.btn_select_shop);
        btnAddGood = findViewById(R.id.btn_add_good);
        btnOrder = findViewById(R.id.btn_order);

        int serverCategoryIdToOrder = getIntent().getIntExtra(Constants.CATEGORY_TO_ORDER, 0);

        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(this);
        List<Good> goodsToOrder = goodsDataProvider.getGoodsToOrderByServerCategoryId(serverCategoryIdToOrder);
        goodsDataProvider.closeDB();

        GoodsToOrderAdapter goodsToOrderAdapter = new GoodsToOrderAdapter(this, goodsToOrder);

        GridLayoutManager glm = new GridLayoutManager(this, 3);
        rvGoodsToOrder.setLayoutManager(glm);
        rvGoodsToOrder.setAdapter(goodsToOrderAdapter);
        rvGoodsToOrder.setHasFixedSize(true);

        btnSelectShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });

    }

    private void createOrder() {


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home :
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
