package com.winservices.wingoods.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.SectionPageAdapter;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.fragments.ShopsList;
import com.winservices.wingoods.fragments.ShopsMap;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.models.ShopsFilter;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int serverCategoryIdToOrder;
    private int currentTab = TAB_MAP;
    private ShopsMap shopsMap;
    private ShopsList shopsList;
    private ShopsFilter shopsFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        mViewPager = findViewById(R.id.vp_shops);
        tabLayout = findViewById(R.id.tabs_shops);

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
                currentTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
            }
        });

        Intent intent = getIntent();
        serverCategoryIdToOrder = intent.getIntExtra(Constants.CATEGORY_TO_ORDER, 0);

        if (serverCategoryIdToOrder != 0) {
            initActivityVariables(TAB_LIST, getString(R.string.shop_selection));
        } else {
            initActivityVariables(TAB_MAP, getString(R.string.lista_shops));
        }

        getShops(this);

    }


    private void initActivityVariables(int tab, String activityTitle) {
        this.currentTab = tab;
        setTitle(activityTitle);
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
        tabLayout.getTabAt(0).setIcon(R.drawable.map);
        tabLayout.getTabAt(1).setIcon(R.drawable.list);
    }

    private void setupViewPagerAdapter(ArrayList<Shop> shops) {
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        shopsMap = new ShopsMap();
        shopsList = new ShopsList();
        Bundle mBundle = new Bundle();
        mBundle.putParcelableArrayList(SHOPS_TAG, shops);
        mBundle.putInt(Constants.CATEGORY_TO_ORDER, serverCategoryIdToOrder);
        shopsMap.setArguments(mBundle);
        shopsList.setArguments(mBundle);
        mSectionPageAdapter.addFragment(shopsMap, "");
        mSectionPageAdapter.addFragment(shopsList, "");
        mViewPager.setAdapter(mSectionPageAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(currentTab);
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
                //Write your code if there's no result
            }
        }
    }


    private void returnToMainActivity() {
        int fragmentId = getIntent().getIntExtra(Constants.SELECTED_FRAGMENT, R.id.nav_my_goods);
        Intent intent = new Intent();
        intent.putExtra(Constants.SELECTED_FRAGMENT, fragmentId);
        setResult(MainActivity.FRAGMENT_REQUEST_CODE, intent);
        this.finish();
    }

    private void getShops(final Context context) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_GET_SHOPS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                //error in server
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray JSONShops = jsonObject.getJSONArray("shops");

                                for (int i = 0; i < JSONShops.length(); i++) {
                                    JSONObject JSONShop = JSONShops.getJSONObject(i);

                                    Shop shop = new Shop();

                                    JSONArray JSONDCategories = JSONShop.getJSONArray("d_categories");
                                    List<DefaultCategory> defaultCategories = new ArrayList<>();
                                    for (int j = 0; j < JSONDCategories.length(); j++) {
                                        JSONObject JSONDCategory =  JSONDCategories.getJSONObject(j);
                                        int dCategoryId = JSONDCategory.getInt("d_category_id");
                                        String dCategoryName = JSONDCategory.getString("d_category_name");

                                        DefaultCategory dCategory = new DefaultCategory(dCategoryId, dCategoryName);
                                        defaultCategories.add(dCategory);
                                    }

                                    shop.setDefaultCategories(defaultCategories);

                                    int serverShopTypeId = JSONShop.getInt("server_shop_type_id");
                                    String shopTypeName = JSONShop.getString("shop_type_name");
                                    ShopType shopType = new ShopType(serverShopTypeId, shopTypeName);

                                    int serverCountryId = JSONShop.getInt("server_country_id");
                                    String countryName = JSONShop.getString("country_name");
                                    Country country = new Country(serverCountryId, countryName);

                                    int serverCityId = JSONShop.getInt("server_city_id");
                                    String cityName = JSONShop.getString("city_name");
                                    City city = new City(serverCityId, cityName, country);

                                    shop.setServerShopId(JSONShop.getInt("server_shop_id"));
                                    shop.setShopName(JSONShop.getString("shop_name"));
                                    shop.setShopAdress(JSONShop.getString("shop_adress"));
                                    shop.setShopEmail(JSONShop.getString("shop_email"));
                                    shop.setShopPhone(JSONShop.getString("shop_phone"));
                                    shop.setLongitude(JSONShop.getDouble("longitude"));
                                    shop.setLatitude(JSONShop.getDouble("latitude"));
                                    shop.setShopType(shopType);
                                    shop.setCity(city);
                                    shop.setCountry(country);

                                    String shopImage = JSONShop.getString("shop_image");
                                    storeImageToFile(shopImage, shop.getServerShopId());

                                    shops.add(shop);
                                    shopsFirstList.add(shop);
                                }
                                setupViewPagerAdapter(shops);
                            }

                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //adding coUser failed
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonData", "");
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
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


    private void storeImageToFile(final String shopImage, final int serverShopId) {
        final String file_path = this.getFilesDir().getPath() + "/jpg";
        Thread thread = new Thread(){
            public void run() {
                File dir = new File(file_path);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, "lista_pro_shop_" + serverShopId + ".jpg");
                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(file);
                    Bitmap bitmap = UtilsFunctions.stringToBitmap(shopImage);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SharedPrefManager.getInstance(getApplicationContext()).storeShopImagePath(serverShopId,file.getAbsolutePath());
            }
        };
        thread.run();

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
