package com.winservices.wingoods.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.SectionPageAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.ShopsDataManager;
import com.winservices.wingoods.fragments.ShopsList;
import com.winservices.wingoods.fragments.ShopsMap;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopsFilter;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final String SHOPS_TAG = "shops";
    public static final int REQUEST_FOR_FILTERS = 101;
    private static final String TAG = "ShopsActivity";
    private final static int TAB_MAP = 0;
    private final static int TAB_LIST = 1;
    TabLayout tabLayout;
    private ViewPager mViewPager;
    private SectionPageAdapter mSectionPageAdapter;
    private ArrayList<Shop> shops, shopsFirstList;
    private Dialog dialog;
    private boolean orderInitiated;
    private ShopsMap shopsMap;
    private ShopsList shopsList;
    private ShopsFilter shopsFilter;
    private LinearLayout llFooter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        mViewPager = findViewById(R.id.vp_shops);
        tabLayout = findViewById(R.id.tabs_shops);

        setTitle(R.string.lista_shops);

        setDialog(R.string.loading);

        shops = new ArrayList<>();
        shopsFirstList = new ArrayList<>();
        shopsFilter = new ShopsFilter();

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Intent intent = getIntent();
        orderInitiated = intent.getBooleanExtra(Constants.ORDER_INITIATED, false);

        getShops();

    }


    private void setDialog(int msgId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.progress, null);
        TextView msg = view.findViewById(R.id.txt_msg_progress);
        msg.setText(getResources().getString(msgId));
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }


    private void setupTabIcons() {
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.map);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.list);
    }

    private void setupViewPagerAdapter(ArrayList<Shop> shops) {
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        shopsMap = new ShopsMap();
        shopsList = new ShopsList();
        Bundle mBundle = new Bundle();
        mBundle.putParcelableArrayList(SHOPS_TAG, shops);
        mBundle.putBoolean(Constants.ORDER_INITIATED, orderInitiated);
        shopsMap.setArguments(mBundle);
        shopsList.setArguments(mBundle);
        mSectionPageAdapter.addFragment(shopsMap, "");
        mSectionPageAdapter.addFragment(shopsList, "");
        mViewPager.setAdapter(mSectionPageAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
        setupTabIcons();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_FOR_FILTERS) {
            if (resultCode == Activity.RESULT_OK) {
                shopsFilter = (ShopsFilter) data.getSerializableExtra("filter");
                shopsList.adapter.setShops(shopsFirstList);
                shopsMap.setShops(shopsFirstList);
                if (shopsFilter.isEnable()) {
                    shopsList.adapter.setShopsFilter(shopsFilter);
                    shopsMap.setShopsFilter(shopsFilter);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: NO RESULT");
            }
        }
    }

    private void returnToMainActivity() {

        if (orderInitiated){
            Intent intent = new Intent();
            int fragmentId = getIntent().getIntExtra(Constants.SELECTED_FRAGMENT, 101);
            intent.putExtra(Constants.SELECTED_FRAGMENT, fragmentId);
            setResult(MainActivity.FRAGMENT_REQUEST_CODE, intent);
        } else {
            Intent intent = new Intent(ShopsActivity.this, LauncherActivity.class);
            startActivity(intent);
        }
        this.finish();

    }

    private void getShops(){

        ShopsDataManager shopsDataManager = new ShopsDataManager(this);
        List<Shop> shopsFromDB = shopsDataManager.getAllShops();

        for (int i = 0; i < shopsFromDB.size(); i++) {
            Shop shop = shopsFromDB.get(i);
            if (orderInitiated){
                if (canGetOrder(shop)) {
                    shops.add(shop);
                    shopsFirstList.add(shop);
                }
            } else {
                shops.add(shop);
                shopsFirstList.add(shop);
            }
        }
        dialog.dismiss();

        if (shops.size() > 0) {
            setupViewPagerAdapter(shops);
        } else {
            setDialogNoShops();
        }


    }


    private void setDialogNoShops() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Objects.requireNonNull(this));
        builder.setMessage(R.string.no_shops_referenced);
        builder.setTitle(R.string.referenced_shops);

        builder.setNegativeButton(R.string.got_it, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shops_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem filterMenuItem = menu.findItem(R.id.item_filter_shops);
        FrameLayout filterRootView = (FrameLayout) filterMenuItem.getActionView();
        //ImageView img = filterRootView.findViewById(R.id.filter);
        filterRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(filterMenuItem);
            }
        });

        final MenuItem searchMenuItem = menu.findItem(R.id.item_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        //ImageView img = rootView.findViewById(R.id.search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(searchMenuItem);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                UtilsFunctions.hideKeyboard(getApplicationContext(), searchView);
                return false;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.item_filter_shops:
                //TODO
                //Activity for filtering the list using different creterias
                if (NetworkMonitor.checkNetworkConnection(this)) {
                    Intent filtersIntent = new Intent(ShopsActivity.this, FilterShopsActivity.class);
                    filtersIntent.putExtra("shopsFilter", shopsFilter);
                    filtersIntent.putExtra("shopsNumber", shopsFirstList.size());
                    startActivityForResult(filtersIntent, REQUEST_FOR_FILTERS);
                } else {
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }

                break;
            case android.R.id.home:
                returnToMainActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Shop> newList = new ArrayList<>();
        for (Shop shop : shops) {
            String shopName = shop.getShopName().toLowerCase();
            if (shopName.contains(newText)) {
                newList.add(shop);
            }
        }
        //Updating ths Shops List and the Map's Markers
        shopsList.setShopNameFilter(newList);
        shopsMap.setShopNameFilter(newList);
        return true;
    }


    private boolean canGetOrder(Shop shop) {

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(this);
        return (categoriesDataProvider.getCategoriesForOrder(shop).size() > 0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shopsMap = null;
        shopsList = null;
        shops = null;
        shopsFirstList = null;
        shopsFilter = null;

    }
}
