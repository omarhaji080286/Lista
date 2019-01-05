package com.winservices.wingoods.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.MainActivity;
import com.winservices.wingoods.activities.MyOrdersActivity;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.utils.NetworkMonitor;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    public final static String TAG = WelcomeFragment.class.getSimpleName();

    ConstraintLayout consLayMyGoods;
    LinearLayout linlayMyOrders, linlayShops;

    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        consLayMyGoods = view.findViewById(R.id.consLayMyGoods);
        linlayMyOrders = view.findViewById(R.id.linlayMyOrders);
        linlayShops = view.findViewById(R.id.linlayShops);


        consLayMyGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        linlayMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(new Intent(getActivity(), MyOrdersActivity.class));
            }
        });

        linlayShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(new Intent(getActivity(), ShopsActivity.class));
            }
        });


    }

    private void goToActivity(Intent intent){
        if (NetworkMonitor.checkNetworkConnection(Objects.requireNonNull(getContext()))){
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }
}
