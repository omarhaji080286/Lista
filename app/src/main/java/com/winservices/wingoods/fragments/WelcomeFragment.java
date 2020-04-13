package com.winservices.wingoods.fragments;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.BuildConfig;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.MainActivity;
import com.winservices.wingoods.activities.MyOrdersActivity;
import com.winservices.wingoods.activities.ProfileActivity;
import com.winservices.wingoods.activities.ReceiveInvitationActivity;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.InvitationsDataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.RemoteConfigParams;
import com.winservices.wingoods.services.DeviceInfoService;
import com.winservices.wingoods.utils.AnimationManager;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.PermissionUtil;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.winservices.wingoods.utils.PermissionUtil.TXT_CAMERA;
import static com.winservices.wingoods.utils.PermissionUtil.TXT_NOTIFICATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    public final static String TAG = WelcomeFragment.class.getSimpleName();

    private ConstraintLayout consLayMyGoods, consLayMyOrders;
    private LinearLayout linlayShops, linlayProfile;
    private TextView txtAvailableOrders, txtItemsToBuyNum, txtWelcome1, txtWelcome2;
    private SyncReceiverWelcome syncReceiver;
    private ImageView imgInvitation, imgShare, imgGooglePlay;

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

        syncReceiver = new SyncReceiverWelcome();

        consLayMyGoods = view.findViewById(R.id.consLayMyGoods);
        consLayMyOrders = view.findViewById(R.id.consLayMyOrders);
        linlayShops = view.findViewById(R.id.linlayShops);
        linlayProfile = view.findViewById(R.id.linlayProfile);
        txtAvailableOrders = view.findViewById(R.id.txtAvailableOrders);
        imgInvitation = view.findViewById(R.id.imgInvitation);
        imgShare = view.findViewById(R.id.imgShare);
        imgGooglePlay = view.findViewById(R.id.imgGooglePlay);
        txtItemsToBuyNum = view.findViewById(R.id.txtItemsToBuyNum);
        txtWelcome1 = view.findViewById(R.id.txtWelcome1);
        txtWelcome2 = view.findViewById(R.id.txtWelcome2);


        SyncHelper.sync(getContext());

        /*PermissionUtil permissionUtil = new PermissionUtil(Objects.requireNonNull(getContext()));
        if (permissionUtil.checkPermission(TXT_NOTIFICATION) != PackageManager.PERMISSION_GRANTED) {
            permissionUtil.requestPermission(TXT_NOTIFICATION, getActivity());
        }*/

        DeviceInfoService deviceInfoService = new DeviceInfoService(getContext());
        deviceInfoService.run();

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

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAppStoreLink();
            }
        });

        RemoteConfigParams rcp = new RemoteConfigParams(getContext());
        try {
            JSONObject jsonObject = new JSONObject(rcp.getAppMessages());
            txtWelcome1.setText(jsonObject.getString("welcome1"));
            txtWelcome2.setText(jsonObject.getString("welcome2"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    private void goToMarket() {
        Intent intent;
        final String appPackageName = Objects.requireNonNull(getContext()).getPackageName();
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        } catch (ActivityNotFoundException anfe) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }

        PackageManager manager = Objects.requireNonNull(getContext()).getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            //Then there is an Application(s) can handle your intent
            Log.d(TAG, "Notification intent ok");
        } else {
            //No Application can handle your intent
            Log.d(TAG, "Notification intent NOT ok");
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private boolean isInvitationPending() {
        InvitationsDataManager invitationsDataManager = new InvitationsDataManager(getContext());
        return invitationsDataManager.isInvitationPending();

    }

    private void shareAppStoreLink() {

        RemoteConfigParams rcp = new RemoteConfigParams(getContext());
        String mainMessage = Objects.requireNonNull(getContext()).getResources().getString(R.string.share_message);
        try {
            JSONObject jsonObject = new JSONObject(rcp.getAppMessages());
            mainMessage = jsonObject.getString("shareMessage");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String listaLink = "https://play.google.com/store/apps/details?id=com.winservices.wingoods";
        String subject = "avec Lista, les courses deviennent fun";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        String body = mainMessage +
                "\n" +
                listaLink;
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.lista)));

    }

    @Override
    public void onResume() {
        super.onResume();
        UtilsFunctions.hideKeyboard(getContext(), imgShare);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                manageGooglePlayIcon();
                manageInvitationIcon();
                getAvailableOrdersNum();
                getItemsToBuyNum();

                Log.d(TAG, "Icons handled");
            }
        });

        Objects.requireNonNull(getActivity()).registerReceiver(syncReceiver, new IntentFilter(Constants.ACTION_REFRESH_AFTER_SYNC));

    }

    private void getItemsToBuyNum() {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(getContext());
        int itemsToBuyNum = goodsDataProvider.getGoodsToBuyNb();

        if (itemsToBuyNum > 0){
            txtItemsToBuyNum.setVisibility(View.VISIBLE);
            txtItemsToBuyNum.setText(String.valueOf(itemsToBuyNum));
        } else {
            txtItemsToBuyNum.setVisibility(View.GONE);
        }
    }

    private void getAvailableOrdersNum() {
        if (NetworkMonitor.checkNetworkConnection(Objects.requireNonNull(getContext()))) {

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
                protected Map<String, String> getParams() {
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

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getActivity()).unregisterReceiver(syncReceiver);
    }

    private void manageGooglePlayIcon() {

        RemoteConfigParams rcp = new RemoteConfigParams(getContext());
        int googlePlayVersion = rcp.getGooglePlayVersion();

        int userVersion = BuildConfig.VERSION_CODE;
        if (googlePlayVersion > userVersion) {
            imgGooglePlay.setVisibility(View.VISIBLE);
            AnimationManager am = new AnimationManager(getContext());
            am.animateItem(imgGooglePlay, R.anim.blink, 1000);
            imgGooglePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMarket();
                }
            });
        } else {
            imgGooglePlay.setVisibility(View.GONE);
        }

    }

    private void manageInvitationIcon() {

        if (isInvitationPending()) {

            imgInvitation.setVisibility(View.VISIBLE);

            imgInvitation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToActivity(new Intent(getActivity(), ReceiveInvitationActivity.class));
                }
            });

            AnimationManager am = new AnimationManager(getContext());
            am.animateItem(imgInvitation, R.anim.blink, 1000);

        } else {
            imgInvitation.setVisibility(View.GONE);
        }
    }

    public class SyncReceiverWelcome extends BroadcastReceiver {

        private Handler handler; // Handler used to execute code on the UI thread

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Post the UI updating code to our Handler

            this.handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {

                    getAvailableOrdersNum();
                    getItemsToBuyNum();

                    Log.d(TAG, "Sync BroadCast received");
                }
            });
        }
    }

}
