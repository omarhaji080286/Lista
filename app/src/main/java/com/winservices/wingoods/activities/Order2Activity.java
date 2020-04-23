package com.winservices.wingoods.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.CategoriesToOrderAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.fragments.CompleteOrderFragment;
import com.winservices.wingoods.fragments.OrderFragment;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.utils.Constants;

import java.util.List;

public class Order2Activity extends AppCompatActivity {

    private final static String TAG = Order2Activity.class.getSimpleName();
    private String currentFragTag = "none";
    public CategoriesToOrderAdapter categoriesToOrderAdapter;
    public int selectedShopId;
    private RecyclerView rvCategoriesToOrder;
    private GridLayoutManager glm;
    private String[] collectTimes;
    private Location lastLocation;
    public String location;
    private EditText editLocation;
    private ImageButton imgBtnGoogleMaps;
    private boolean isPickerStartingTomorrow = false;
    public Shop shop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order2);

        updateLastLocation();
        setTitle(getString(R.string.order_my_list));

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        selectedShopId = getIntent().getIntExtra(Constants.SELECTED_SHOP_ID, 0);
        shop = getIntent().getParcelableExtra(Constants.SHOP);

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(this);
        List<CategoryGroup> categoriesToOrder = categoriesDataProvider.getCategoriesForOrder(shop);
        categoriesToOrderAdapter = new CategoriesToOrderAdapter(categoriesToOrder, this);

        displayFragment(new OrderFragment(categoriesToOrderAdapter, shop), OrderFragment.TAG );

    }

    public void displayFragment(Fragment fragment, String tag) {
        if (currentFragTag.equals(tag)) return;
        currentFragTag = tag;
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameOrder2Activity, fragment, tag)
                .commit();

    }

    public void updateLastLocation() {
        new Thread(new Runnable() {
            public void run() {
                getLocation();
            }
        }).start();
    }

    public void getLocation() {

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful() && task.getResult() != null) {
                    try {
                        lastLocation = task.getResult();

                        int latitudeLength = String.valueOf(lastLocation.getLatitude()).length();
                        int longitudeLength = String.valueOf(lastLocation.getLongitude()).length();

                        location = String.valueOf(lastLocation.getLatitude()).substring(0,latitudeLength-1) + ", " + String.valueOf(lastLocation.getLongitude()).substring(0,longitudeLength-1);
                        Log.d(TAG, "onComplete:success  " + lastLocation.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "onComplete:exception  " + task.getException());
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        OrderFragment oFragment = (OrderFragment) manager.findFragmentByTag(OrderFragment.TAG);
        if (oFragment != null) {
            Intent intent = new Intent(Order2Activity.this, ShopsActivity.class);
            intent.putExtra(Constants.ORDER_INITIATED, true);
            startActivity(intent);
            finish();
        }
    }


}
