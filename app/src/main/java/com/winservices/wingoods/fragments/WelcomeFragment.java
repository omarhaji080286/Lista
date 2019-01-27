package com.winservices.wingoods.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.MainActivity;
import com.winservices.wingoods.activities.MyOrdersActivity;
import com.winservices.wingoods.activities.ProfileActivity;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.utils.NetworkMonitor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    public final static String TAG = WelcomeFragment.class.getSimpleName();

    private ConstraintLayout consLayMyGoods, consLayMyOrders;
    private LinearLayout linlayShops, linlayProfile;
    private TextView txtAvailableOrders;

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
        consLayMyOrders = view.findViewById(R.id.consLayMyOrders);
        linlayShops = view.findViewById(R.id.linlayShops);
        linlayProfile = view.findViewById(R.id.linlayProfile);
        txtAvailableOrders = view.findViewById(R.id.txtAvailableOrders);

        SyncHelper.sync(getContext());

        consLayMyGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        consLayMyOrders.setOnClickListener(new View.OnClickListener() {
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

        linlayProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getAvailableOrdersNum();
    }

    private void getAvailableOrdersNum() {
        if (NetworkMonitor.checkNetworkConnection(getContext())) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_GET_AVAILABLE_ORDERS_NUM,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");

                                if (error) {
                                    //error in server
                                    Log.e(TAG, "onResponse: " + R.string.error);
                                } else {

                                    int availableOrdersNum = jsonObject.getInt("available_orders_num");
                                    if (availableOrdersNum > 0) {
                                        txtAvailableOrders.setText(String.valueOf(availableOrdersNum));
                                        txtAvailableOrders.setVisibility(View.VISIBLE);
                                    } else {
                                        txtAvailableOrders.setVisibility(View.GONE);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onResponse: " + error.getMessage());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> postData = new HashMap<>();
                    UsersDataManager usersDataManager = new UsersDataManager(getContext());
                    postData.put("server_user_id", String.valueOf(usersDataManager.getCurrentUser().getServerUserId()));
                    return postData;
                }
            };

            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }

    }


    private void goToActivity(Intent intent) {
        if (NetworkMonitor.checkNetworkConnection(Objects.requireNonNull(getContext()))) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }
}
