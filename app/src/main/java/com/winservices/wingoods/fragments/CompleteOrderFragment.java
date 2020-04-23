package com.winservices.wingoods.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.MainActivity;
import com.winservices.wingoods.activities.Order2Activity;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.ShopsDataManager;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Order;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CompleteOrderFragment extends Fragment {

    public static final String TAG = "CompleteOrderFragment";
    private EditText editLocation;
    private ImageButton imgBtnGoogleMaps;
    private boolean isPickerStartingTomorrow = false;
    private String[] collectTimes;
    private Shop shop;
    private NumberPicker pickerDay;
    private NumberPicker pickerTime;
    private CheckBox cbHomeDelivery;
    private EditText editUserAddress;
    private RadioGroup rgLocation;

    CompleteOrderFragment(Shop shop) {
        this.shop = shop;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_complete_order_data, container, false);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.validate_order_frag_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Order2Activity orderActivity = (Order2Activity) Objects.requireNonNull(getActivity());
        switch (item.getItemId()) {
            case R.id.validateOrder:
                validateOrder();
                break;
            case android.R.id.home :
                orderActivity.setTitle(getString(R.string.order_my_list));
                orderActivity.displayFragment(new OrderFragment(orderActivity.categoriesToOrderAdapter, orderActivity.shop), OrderFragment.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Order2Activity orderActivity = (Order2Activity) Objects.requireNonNull(getActivity());

        pickerDay = view.findViewById(R.id.pickerDay);
        pickerTime = view.findViewById(R.id.pickerTime);

        LinearLayoutCompat llDeliveryModule = view.findViewById(R.id.llDeliveryModule);
        cbHomeDelivery = view.findViewById(R.id.cbHomeDelivery);
        final LinearLayoutCompat llAddress = view.findViewById(R.id.llAddress);
        final LinearLayoutCompat llLocation = view.findViewById(R.id.llLocation);
        editUserAddress = view.findViewById(R.id.editUserAddress);
        rgLocation = view.findViewById(R.id.rgLocation);
        editLocation = view.findViewById(R.id.editLocation);
        imgBtnGoogleMaps = view.findViewById(R.id.imgBtnGoogleMaps);

        ShopsDataManager shopsDataManager = new ShopsDataManager(getContext());
        final Shop shop = shopsDataManager.getShopById(orderActivity.selectedShopId);

        //Prepare collect time
        final String[] days = getDays(shop.getClosingTime());
        pickerDay.setMinValue(0);
        pickerDay.setMaxValue(days.length - 1);
        pickerDay.setDisplayedValues(days);
        pickerDay.setValue(0);

        collectTimes = getCollectTimes(days[0], shop.getOpeningTime(), shop.getClosingTime());

        pickerTime.setMinValue(0);
        pickerTime.setMaxValue(collectTimes.length - 1);
        pickerTime.setDisplayedValues(collectTimes);

        /*mBuilder.setView(view);
        final AlertDialog dialogTime = mBuilder.create();
        dialogTime.setTitle(R.string.validate_order_form);
        dialogTime.show();*/

        pickerDay.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                collectTimes = getCollectTimes(days[newVal], shop.getOpeningTime(), shop.getClosingTime());
                pickerTime.setDisplayedValues(null);
                pickerTime.setMaxValue(collectTimes.length - 1);
                pickerTime.setDisplayedValues(collectTimes);
            }
        });

        if (shop.getIsDelivering()==Shop.IS_DELIVERING){
            llDeliveryModule.setVisibility(View.VISIBLE);

            //CheckBox Home Delivery checkbox
            cbHomeDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    orderActivity.updateLastLocation();
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
            refreshLocationUI(imgBtnGoogleMaps);
            final KeyListener keyListener = editLocation.getKeyListener();
            rgLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.radioGetMyLocation:
                            refreshLocationUI(imgBtnGoogleMaps);
                            editLocation.setText(orderActivity.location);
                            editLocation.setKeyListener(null);
                            editLocation.setError(null);
                            imgBtnGoogleMaps.setVisibility(View.GONE);
                            break;
                        case R.id.radioSetLocationManually:
                            orderActivity.updateLastLocation();
                            editLocation.setKeyListener(keyListener);
                            editLocation.setText("");
                            imgBtnGoogleMaps.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
        } else {
            llDeliveryModule.setVisibility(View.GONE);
        }

    }

    private String[] getDays(String closingTime) {
        //final String[] weekdays = getResources().getStringArray(R.array.days);
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String[] days = new String[7];

        if (Integer.parseInt(closingTime.substring(0, 2)) <= hourOfDay + 2) {
            days[0] = getResources().getString(R.string.tomorrow);
            isPickerStartingTomorrow = true;

            for (int i = 1; i < days.length; i++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, i);
                int dayNumber = cal.get(Calendar.DAY_OF_WEEK);
                days[i] = UtilsFunctions.getDayOfWeek(getContext(), dayNumber+1 ) + " " + UtilsFunctions.to2digits(cal.get(Calendar.DAY_OF_MONTH)+1);
            }

        } else {
            days[0] = getResources().getString(R.string.today);
            days[1] = getResources().getString(R.string.tomorrow);
            isPickerStartingTomorrow = false;

            for (int i = 2; i < days.length; i++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, i);
                int dayNumber = cal.get(Calendar.DAY_OF_WEEK);
                days[i] = UtilsFunctions.getDayOfWeek(getContext(), dayNumber ) + " " + UtilsFunctions.to2digits(cal.get(Calendar.DAY_OF_MONTH));
            }
        }

        return days;
    }

    private String getStartTime(int pickerDayValue, String pickerTimeDisplayedValue, boolean isPickerStartingWithTomorrow) {
        String day;
        String startTime;

        Calendar cal = Calendar.getInstance();
        if (isPickerStartingWithTomorrow){
            cal.add(Calendar.DATE, pickerDayValue+1);
        } else {
            cal.add(Calendar.DATE, pickerDayValue);
        }

        day = UtilsFunctions.dateToString(cal.getTime(), "yyyy-MM-dd");
        startTime = day + " " + pickerTimeDisplayedValue.substring(0, 5);

        Log.d(TAG, "start time: " + startTime);

        return startTime;
    }

    private String getEndTime(int pickerDayValue, String pickerTimeDisplayedValue, boolean isPickerStartingWithTomorrow) {
        String day;
        String startTime;

        Calendar cal = Calendar.getInstance();
        if (isPickerStartingWithTomorrow){
            cal.add(Calendar.DATE, pickerDayValue+1);
        } else {
            cal.add(Calendar.DATE, pickerDayValue);
        }
        day = UtilsFunctions.dateToString(cal.getTime(), "yyyy-MM-dd");
        startTime = day + " " + pickerTimeDisplayedValue.substring(8, 13);

        Log.d(TAG, "end Time: " + startTime);

        return startTime;
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

    private void refreshLocationUI(ImageButton imgBtnGoogleMaps){
        final Order2Activity orderActivity = (Order2Activity) Objects.requireNonNull(getActivity());

        orderActivity.getLocation();

        //ImgButton check location on google maps
        imgBtnGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderActivity.updateLastLocation();
                String uri = "geo:"+ orderActivity.location+"?q="+orderActivity.location;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    private boolean formOk(EditText editUserAddress, RadioGroup rg){
        if (editUserAddress.getText().toString().isEmpty() || editUserAddress.getText().toString().equals("") ){
            editUserAddress.setError(getString(R.string.address_required));
            return false;
        }

        if (rg.getCheckedRadioButtonId()==-1){
            Toast.makeText(getContext(), R.string.location_required, Toast.LENGTH_SHORT).show();
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
                                    Order2Activity orderActivity = (Order2Activity) Objects.requireNonNull(getActivity());
                                    Intent intent = new Intent(orderActivity, MainActivity.class);
                                    startActivity(intent);
                                    orderActivity.finish();
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

            UsersDataManager usersDataManager = new UsersDataManager(getContext());
            User currentUser = usersDataManager.getCurrentUser();

            root.put("serverUserId", currentUser.getServerUserId());
            root.put("serverShopId", shop.getServerShopId());
            root.put("statusId", Order.REGISTERED);
            root.put("startTime", startTime);
            root.put("endTime", endTime);
            root.put("isToDeliver", String.valueOf(isToDeliver));
            root.put("userAddress", userAddress);
            root.put("userLocation", userLocation);

            Order2Activity orderActivity = (Order2Activity) Objects.requireNonNull(getActivity());


            JSONArray jsonGoods = new JSONArray();
            for (int i = 0; i < orderActivity.categoriesToOrderAdapter.getGoodsToOrder().size(); i++) {
                Good good = orderActivity.categoriesToOrderAdapter.getGoodsToOrder().get(i);
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
        final Order2Activity orderActivity = (Order2Activity) Objects.requireNonNull(getActivity());

        DataManager dataManager = new DataManager(getContext());
        for (int i = 0; i < orderActivity.categoriesToOrderAdapter.getGoodsToOrder().size(); i++) {
            Good good = orderActivity.categoriesToOrderAdapter.getGoodsToOrder().get(i);
            good.setIsOrdered(Good.IS_ORDERED);
            good.setSync(DataBaseHelper.SYNC_STATUS_FAILED);
            dataManager.updateGood(good);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (editLocation!=null && imgBtnGoogleMaps !=null){
            refreshLocationUI(imgBtnGoogleMaps);
        }
    }


    private void validateOrder(){
        final Order2Activity orderActivity = (Order2Activity) Objects.requireNonNull(getActivity());

        String startTime = getStartTime(pickerDay.getValue(), collectTimes[pickerTime.getValue()], isPickerStartingTomorrow);
        String endTime = getEndTime(pickerDay.getValue(), collectTimes[pickerTime.getValue()], isPickerStartingTomorrow);
        int isToDeliver = 0;
        String userAddress = "";
        String userLocation = orderActivity.location;

        if (shop.getIsDelivering()==Shop.IS_DELIVERING && cbHomeDelivery.isChecked()){
            if (formOk(editUserAddress, rgLocation)){

                if (cbHomeDelivery.isChecked()) isToDeliver = 1;
                userAddress = editUserAddress.getText().toString();
                userLocation = editLocation.getText().toString();

                addOrder(getContext(), startTime, endTime,
                        isToDeliver, userAddress, userLocation);

            }
        } else {
            addOrder(getContext(), startTime, endTime,
                    isToDeliver, userAddress, userLocation);
        }
    }
}
