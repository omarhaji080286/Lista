package com.winservices.wingoods.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.adapters.ShopsListAdapter;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopsFilter;
import com.winservices.wingoods.utils.Constants;

import java.util.ArrayList;


public class ShopsList extends Fragment {

    private static final String TAG = "ShopsList";

    private TextView message;
    private RecyclerView shopsRecyclerView;
    private ArrayList<Shop> shops;
    public ShopsListAdapter adapter;
    private int serverCategoryIdToOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shops_list, container, false);

        message = view.findViewById(R.id.txt_message);
        shopsRecyclerView = view.findViewById(R.id.rv_shops);
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
            serverCategoryIdToOrder = getArguments().getInt(Constants.CATEGORY_TO_ORDER);
        }

        if (shops.size()==0){
            message.setVisibility(View.VISIBLE);
            shopsRecyclerView.setVisibility(View.GONE);
        } else {
            adapter = new ShopsListAdapter(getContext(), shops, serverCategoryIdToOrder);

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

}
