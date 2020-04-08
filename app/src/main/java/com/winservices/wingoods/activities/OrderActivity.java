package com.winservices.wingoods.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.CategoriesToOrderAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.ShopsDataManager;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.CategoryGroup;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.RecyclerItemTouchHelper;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class OrderActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private final static String TAG = OrderActivity.class.getSimpleName();
    private CategoriesToOrderAdapter categoriesToOrderAdapter;
    private int selectedShopId;
    private RecyclerView rvCategoriesToOrder;
    private GridLayoutManager glm;
    private String[] collectTimes;
    private Location lastLocation;
    private String location;
    private EditText editLocation;
    private ImageButton imgBtnGoogleMaps;

    @Override
    protected void onResume() {
        super.onResume();

        if (editLocation!=null && imgBtnGoogleMaps !=null){
            refreshLocationUI(editLocation, imgBtnGoogleMaps);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        updateLastLocation();

        setTitle(getString(R.string.order_my_list));

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rvCategoriesToOrder = findViewById(R.id.rvCategoriesToOrder);
        TextView txtShopName = findViewById(R.id.txt_shop_name);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvCategoriesToOrder);

        selectedShopId = getIntent().getIntExtra(Constants.SELECTED_SHOP_ID, 0);
        Shop shop = getIntent().getParcelableExtra(Constants.SHOP);

        txtShopName.setText(shop.getShopName());

        loadCategoriesToOrder(shop);

    }

    private void sendOrder() {
        if (categoriesToOrderAdapter.getGoodsToOrderNumber() > 0) {
            if (categoriesToOrderAdapter.getGoodsToComplete().size() == 0) {
                completeOrderData();
            } else {
                Toast.makeText(this, R.string.set_descriptions, Toast.LENGTH_SHORT).show();
                List<Good> goodsToComplete = categoriesToOrderAdapter.getGoodsToComplete();
                for (int i = 0; i < goodsToComplete.size(); i++) {
                    Good good = goodsToComplete.get(i);
                    int position = categoriesToOrderAdapter.getGoodPosition(good.getGoodId());
                    animateItem(position);
                }
            }
        } else {
            Toast.makeText(this, R.string.empty_order, Toast.LENGTH_SHORT).show();
        }
    }

    private void completeOrderData() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = LayoutInflater.from(this)
                .inflate(R.layout.fragment_complete_order_data, null, false);

        final NumberPicker pickerDay = mView.findViewById(R.id.pickerDay);
        final NumberPicker pickerTime = mView.findViewById(R.id.pickerTime);

        final CheckBox cbHomeDelivery = mView.findViewById(R.id.cbHomeDelivery);
        final LinearLayoutCompat llAddress = mView.findViewById(R.id.llAddress);
        final LinearLayoutCompat llLocation = mView.findViewById(R.id.llLocation);
        final EditText editUserAddress = mView.findViewById(R.id.editUserAddress);
        final RadioGroup rgLocation = mView.findViewById(R.id.rgLocation);
        editLocation = mView.findViewById(R.id.editLocation);
        imgBtnGoogleMaps = mView.findViewById(R.id.imgBtnGoogleMaps);

        Button btnSubmit = mView.findViewById(R.id.btnSubmit);

        ShopsDataManager shopsDataManager = new ShopsDataManager(this);
        final Shop shop = shopsDataManager.getShopById(selectedShopId);

        //Prepare collect time
        final String[] days = getDays(shop.getClosingTime());
        pickerDay.setMinValue(0);
        pickerDay.setMaxValue(days.length - 1);
        pickerDay.setDisplayedValues(days);

        collectTimes = getCollectTimes(days[0], shop.getOpeningTime(), shop.getClosingTime());

        pickerTime.setMinValue(0);
        pickerTime.setMaxValue(collectTimes.length - 1);
        pickerTime.setDisplayedValues(collectTimes);

        mBuilder.setView(mView);
        final AlertDialog dialogTime = mBuilder.create();
        dialogTime.setTitle(R.string.validate_order_form);
        dialogTime.show();

        pickerDay.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                collectTimes = getCollectTimes(days[newVal], shop.getOpeningTime(), shop.getClosingTime());
                pickerTime.setDisplayedValues(null);
                pickerTime.setMaxValue(collectTimes.length - 1);
                pickerTime.setDisplayedValues(collectTimes);
            }
        });

        //CheckBox Home Delivery checkbox
        cbHomeDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateLastLocation();
                if (isChecked){
                    llAddress.setVisibility(View.VISIBLE);
                    llLocation.setVisibility(View.VISIBLE);
                } else {
                    llAddress.setVisibility(View.GONE);
                    llLocation.setVisibility(View.GONE);
                }
            }
        });

        //Radio group : get location
        refreshLocationUI(editLocation, imgBtnGoogleMaps);
        final KeyListener keyListener = editLocation.getKeyListener();
        rgLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioGetMyLocation:
                        refreshLocationUI(editLocation, imgBtnGoogleMaps);
                        editLocation.setText(location);
                        editLocation.setKeyListener(null);
                        editLocation.setError(null);
                        imgBtnGoogleMaps.setVisibility(View.GONE);
                        break;
                    case R.id.radioSetLocationManually:
                        updateLastLocation();
                        editLocation.setKeyListener(keyListener);
                        editLocation.setText("");
                        imgBtnGoogleMaps.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        //Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (formOk(editUserAddress, rgLocation)){
                    dialogTime.dismiss();

                    String startTime = getStartTime(pickerDay.getValue(), collectTimes[pickerTime.getValue()]);
                    String endTime = getEndTime(pickerDay.getValue(), collectTimes[pickerTime.getValue()]);

                    int isToDeliver = 0;
                    if (cbHomeDelivery.isChecked()) isToDeliver = 1;
                    addOrder(OrderActivity.this, startTime, endTime,
                            isToDeliver, editUserAddress.getText().toString(), editLocation.getText().toString());
                }
            }
        });
    }

    private boolean formOk(EditText editUserAddress, RadioGroup rg){
        if (editUserAddress.getText().toString().isEmpty()){
            editUserAddress.setError(getString(R.string.address_required));
            return false;
        }

        if (rg.getCheckedRadioButtonId()==-1){
            Toast.makeText(this, R.string.location_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editLocation.getText().toString().isEmpty()){
            editLocation.setError(getString(R.string.location_required));
            return false;
        }

        String regexGps = "^([+-]?\\d+\\.?\\d+)\\s*,\\s*([+-]?\\d+\\.?\\d+)$";
        if (!editLocation.getText().toString().matches(regexGps)){
            editLocation.setError(getString(R.string.gps_format));
            return false;
        }

        return true;
    }

    private void refreshLocationUI(final EditText editLocation, ImageButton imgBtnGoogleMaps){
        getLocation();

        //ImgButton check location on google maps
        imgBtnGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLastLocation();
                String uri = "geo:"+ location;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getApplicationContext().startActivity(intent);
            }
        });
    }

    private String getStartTime(int pickerDayValue, String pickerTimeDisplayedValue) {
        String day;
        String startTime;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, pickerDayValue);
        day = UtilsFunctions.dateToString(cal.getTime(), "yyyy-MM-dd");
        startTime = day + " " + pickerTimeDisplayedValue.substring(0, 5);

        Log.d(TAG, "start time: " + startTime);

        return startTime;
    }

    private String getEndTime(int pickerDayValue, String pickerTimeDisplayedValue) {
        String day;
        String startTime;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, pickerDayValue);
        day = UtilsFunctions.dateToString(cal.getTime(), "yyyy-MM-dd");
        startTime = day + " " + pickerTimeDisplayedValue.substring(8, 13);

        Log.d(TAG, "end Time: " + startTime);

        return startTime;
    }

    private String[] getDays(String closingTime) {
        final String[] weekdays = getResources().getStringArray(R.array.days);
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String[] days = new String[7];

        if (Integer.parseInt(closingTime.substring(0, 2)) <= hourOfDay + 2) {
            days[0] = getResources().getString(R.string.tomorrow);

            for (int i = 1; i < days.length; i++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, i);
                int dayNumber = cal.get(Calendar.DAY_OF_WEEK);
                days[i] = UtilsFunctions.getDayOfWeek(this, dayNumber ) + " " + UtilsFunctions.to2digits(cal.get(Calendar.DAY_OF_MONTH));
            }

        } else {
            days[0] = getResources().getString(R.string.today);
            days[1] = getResources().getString(R.string.tomorrow);

            for (int i = 2; i < days.length; i++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, i);
                int dayNumber = cal.get(Calendar.DAY_OF_WEEK);
                days[i] = UtilsFunctions.getDayOfWeek(this, dayNumber ) + " " + UtilsFunctions.to2digits(cal.get(Calendar.DAY_OF_MONTH));
            }
        }

        return days;
    }

    private String[] getCollectTimes(String pickerDayDisplayedValue, String openingTime, String closingTime) {

        String[] times;

        int minStart;
        int maxEnd;

        try {
            minStart = Integer.parseInt(openingTime.substring(0, 2));
            maxEnd = Integer.parseInt(closingTime.substring(0, 2));
        } catch (NumberFormatException e) {
            minStart = 9;
            maxEnd = 23;
        }


        if (pickerDayDisplayedValue.equals(getResources().getString(R.string.today))) {
            Calendar rightNow = Calendar.getInstance();
            int start = Math.max(rightNow.get(Calendar.HOUR_OF_DAY) + 2, minStart);
            int end = start + 1;

            times = new String[maxEnd - start];
            times[0] = UtilsFunctions.to2digits(start) + ":00 - " + UtilsFunctions.to2digits(end) + ":00";
            for (int i = 1; i < times.length; i++) {
                start = start + 1;
                end = end + 1;
                times[i] = UtilsFunctions.to2digits(start) + ":00 - " + UtilsFunctions.to2digits(end) + ":00";
            }

        } else {
            times = new String[maxEnd - minStart];
            for (int i = 0; i < times.length; i++) {
                times[i] = UtilsFunctions.to2digits(i + minStart) + ":00 - " + UtilsFunctions.to2digits(i + minStart + 1) + ":00";
            }

        }

        return times;
    }


    private void animateItem(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                View v = glm.findViewByPosition(position);
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                if (v != null) {
                    v.startAnimation(anim);
                }

            }
        }, 50);
    }

    private void updateLastLocation() {
        new Thread(new Runnable() {
            public void run() {
                getLocation();
            }
        }).start();
    }

    private void loadCategoriesToOrder(Shop shop) {
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(this);
        List<CategoryGroup> categoriesToOrder = categoriesDataProvider.getCategoriesForOrder(shop);

        categoriesToOrderAdapter = new CategoriesToOrderAdapter(categoriesToOrder, this);

        final int GRID_COLUMN_NUMBER = 3;
        glm = new GridLayoutManager(this, GRID_COLUMN_NUMBER);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (categoriesToOrderAdapter.getItemViewType(position)) {
                    case 2:
                        return GRID_COLUMN_NUMBER;
                    default:
                        return 1;
                }
            }
        });

        rvCategoriesToOrder.setLayoutManager(glm);
        rvCategoriesToOrder.setAdapter(categoriesToOrderAdapter);
        expandRecyclerView();
    }

    private void expandRecyclerView() {
        for (int i = categoriesToOrderAdapter.getGroups().size() - 1; i >= 0; i--) {
            if (categoriesToOrderAdapter.isGroupExpanded(i)) {
                return;
            }
            categoriesToOrderAdapter.toggleGroup(i);
        }
    }

    private void addOrder(final Context context, final String startTime, final String endTime,
                          final int isToDeliver, final String userAddress, final String userLocation) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            final Dialog dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), context, R.string.Registering_order).create();
            dialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_ADD_ORDER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");
                                String message = jsonObject.getString("message");
                                if (error) {
                                    //error in server
                                    dialog.dismiss();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    updateOrderedGoods();
                                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dialog.dismiss();
                                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", "" + getJSONForAddOrder(startTime, endTime, isToDeliver, userAddress, userLocation));
                    postData.put("language", "" + Locale.getDefault().getLanguage());
                    return postData;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private String getJSONForAddOrder(String startTime, String endTime,
                                      int isToDeliver, String userAddress, String userLocation) {
        final JSONObject root = new JSONObject();
        try {

            UsersDataManager usersDataManager = new UsersDataManager(this);
            User currentUser = usersDataManager.getCurrentUser();

            root.put("serverUserId", currentUser.getServerUserId());
            root.put("serverShopId", selectedShopId);
            root.put("statusId", Order.REGISTERED);
            root.put("startTime", startTime);
            root.put("endTime", endTime);
            root.put("isToDeliver", String.valueOf(isToDeliver));
            root.put("userAddress", userAddress);
            root.put("userLocation", userLocation);

            JSONArray jsonGoods = new JSONArray();
            for (int i = 0; i < categoriesToOrderAdapter.getGoodsToOrder().size(); i++) {

                Good good = categoriesToOrderAdapter.getGoodsToOrder().get(i);
                JSONObject JSONGood = good.toJSONObject();
                jsonGoods.put(JSONGood);
            }
            root.put("jsonGoodsToOrder", jsonGoods);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d(TAG, "Can't format JSON");
        }

        return null;
    }

    private void updateOrderedGoods() {
        DataManager dataManager = new DataManager(this);
        for (int i = 0; i < categoriesToOrderAdapter.getGoodsToOrder().size(); i++) {
            Good good = categoriesToOrderAdapter.getGoodsToOrder().get(i);
            good.setIsOrdered(Good.IS_ORDERED);
            good.setSync(DataBaseHelper.SYNC_STATUS_FAILED);
            dataManager.updateGood(good);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                goToShopsActivity();
                break;
            case R.id.sendOrder:
                sendOrder();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToShopsActivity() {
        Intent intent = new Intent(OrderActivity.this, ShopsActivity.class);
        intent.putExtra(Constants.ORDER_INITIATED, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CategoriesToOrderAdapter.GoodInOrderVH) {
            categoriesToOrderAdapter.removeChildItem(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getLocation() {

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful() && task.getResult() != null) {
                    lastLocation = task.getResult();
                    location = String.valueOf(lastLocation.getLatitude()).substring(0,9) + ", " + String.valueOf(lastLocation.getLongitude()).substring(0,9);
                    Log.d(TAG, "onComplete:success  " + lastLocation.toString());
                } else {
                    Log.d(TAG, "onComplete:exception  " + task.getException());
                }
            }
        });

    }


}
