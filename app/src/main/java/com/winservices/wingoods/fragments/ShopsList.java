package com.winservices.wingoods.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.MainActivity;
import com.winservices.wingoods.activities.MyOrdersActivity;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.adapters.ShopsListAdapter;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;
import java.util.Objects;


public class ShopsList extends Fragment {

    private static final String TAG = "ShopsList";

    private TextView message;
    private RecyclerView shopsRecyclerView;
    private ArrayList<Shop> shops;
    public ShopsListAdapter adapter;
    private boolean orderInitiated;
    private FloatingActionButton fabAddOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shops_list, container, false);

        message = view.findViewById(R.id.txt_message);
        shopsRecyclerView = view.findViewById(R.id.rv_shops);
        fabAddOrder = view.findViewById(R.id.fab_add_order);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = getArguments();
        shops = new ArrayList<>();
        if (bundle != null) {
            shops = (ArrayList<Shop>) getArguments().getSerializable(ShopsActivity.SHOPS_TAG);
            orderInitiated = getArguments().getBoolean(Constants.ORDER_INITIATED);
            if (orderInitiated) {
                fabAddOrder.hide();
            } else {
                fabAddOrder.show();
                initFabAddOrder();
            }
        }

        if (shops.size()==0){
            message.setVisibility(View.VISIBLE);
            shopsRecyclerView.setVisibility(View.GONE);
        } else {
            adapter = new ShopsListAdapter(getContext(), shops, orderInitiated);

            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            shopsRecyclerView.setLayoutManager(llm);
            shopsRecyclerView.setAdapter(adapter);

            message.setVisibility(View.GONE);
            shopsRecyclerView.setVisibility(View.VISIBLE);

        }
    }

    public void setShopNameFilter(ArrayList<Shop> newList){
        adapter.setShopNameFilter(newList);
    }

    private void initFabAddOrder() {
        fabAddOrder.setOnClickListener(view -> {
            ShopsActivity shopsActivity = (ShopsActivity) Objects.requireNonNull(getActivity());
            Intent intent = new Intent(shopsActivity, MainActivity.class);
            startActivity(intent);
            shopsActivity.finish();
        });
    }

}
