package com.winservices.wingoods.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.MainActivity;
import com.winservices.wingoods.activities.OrderActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CompleteOrderFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "CompleteOrderFragment";
    private EditText editLocation;
    private ImageButton imgBtnGoogleMaps;
    private Shop shop;
    private CheckBox cbHomeDelivery;
    private RadioGroup rgLocation;
    private EditText editDate;
    private String preparationDate;
    private AutoCompleteTextView editUserAddress;


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
        OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());
        switch (item.getItemId()) {
            case R.id.validateOrder:
                validateOrder();
                break;
            case android.R.id.home:
                orderActivity.setTitle(getString(R.string.order_my_list));
                orderActivity.displayFragment(new OrderFragment(orderActivity.categoriesToOrderAdapter, orderActivity.shop), OrderFragment.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());

        LinearLayoutCompat llDeliveryModule = view.findViewById(R.id.llDeliveryModule);
        cbHomeDelivery = view.findViewById(R.id.cbHomeDelivery);
        final LinearLayoutCompat llAddress = view.findViewById(R.id.llAddress);
        final LinearLayoutCompat llLocation = view.findViewById(R.id.llLocation);
        editUserAddress = view.findViewById(R.id.editUserAddress);
        rgLocation = view.findViewById(R.id.rgLocation);
        editLocation = view.findViewById(R.id.editLocation);
        imgBtnGoogleMaps = view.findViewById(R.id.imgBtnGoogleMaps);
        ImageView imgCalendar = view.findViewById(R.id.imgCalendar);
        editDate = view.findViewById(R.id.editDate);

        editDate.setKeyListener(null);

        UsersDataManager usersDataManager = new UsersDataManager(getContext());
        String[] addresses = usersDataManager.getUserLocations();

        ArrayAdapter<String> macBrandAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_expandable_list_item_1, addresses);
        editUserAddress.setAdapter(macBrandAdapter);

        int maxLength = 70;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        editUserAddress.setFilters(fArray);

        ShopsDataManager shopsDataManager = new ShopsDataManager(getContext());
        final Shop shop = shopsDataManager.getShopById(orderActivity.selectedShopId);

        imgCalendar.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    CompleteOrderFragment.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );

            dpd.setMinDate(now);

            Calendar later = (Calendar) now.clone();
            later.add(Calendar.DAY_OF_MONTH, 20);
            dpd.setMaxDate(later);

            dpd.setDisabledDays(shop.getNotWorkedDays());

            dpd.setLocale(Locale.FRANCE);

            dpd.show(Objects.requireNonNull(getFragmentManager()), "Datepickerdialog");
        });

        if (shop.getIsDelivering() == Shop.IS_DELIVERING) {
            llDeliveryModule.setVisibility(View.VISIBLE);

            //CheckBox Home Delivery checkbox
            cbHomeDelivery.setOnCheckedChangeListener((buttonView, isChecked) -> {
                orderActivity.updateLastLocation();
                if (isChecked) {
                    llAddress.setVisibility(View.VISIBLE);
                    llLocation.setVisibility(View.VISIBLE);
                } else {
                    llAddress.setVisibility(View.GONE);
                    llLocation.setVisibility(View.GONE);
                }
            });

            //Radio group : get location
            refreshLocationUI(imgBtnGoogleMaps);
            final KeyListener keyListener = editLocation.getKeyListener();
            rgLocation.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
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
            });
        } else {
            llDeliveryModule.setVisibility(View.GONE);
        }

    }

    private void refreshLocationUI(ImageButton imgBtnGoogleMaps) {
        final OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());

        orderActivity.getLocation();

        //ImgButton check location on google maps
        imgBtnGoogleMaps.setOnClickListener(v -> {
            orderActivity.updateLastLocation();
            String uri = "geo:" + orderActivity.location + "?q=" + orderActivity.location;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        });
    }

    private boolean formOk(EditText editUserAddress, RadioGroup rg, EditText editDate) {
        if (editUserAddress.getText().toString().isEmpty() || editUserAddress.getText().toString().equals("")) {
            editUserAddress.setError(getString(R.string.address_required));
            return false;
        }

        if (rg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), R.string.location_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editLocation.getText().toString().isEmpty()) {
            editLocation.setError(getString(R.string.location_required));
            return false;
        }

        String regexGps = "^([+-]?\\d+\\.?\\d+)\\s*,\\s*([+-]?\\d+\\.?\\d+)$";
        if (!editLocation.getText().toString().matches(regexGps)) {
            editLocation.setError(getString(R.string.gps_format));
            return false;
        }

        if (editDate.getText().toString().isEmpty()) {
            editDate.setError(getString(R.string.required_date));
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
                    response -> {
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
                                OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());
                                Intent intent = new Intent(orderActivity, MainActivity.class);
                                startActivity(intent);
                                orderActivity.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> dialog.dismiss()
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

            OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());

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
        final OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());

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
        if (editLocation != null && imgBtnGoogleMaps != null) {
            refreshLocationUI(imgBtnGoogleMaps);
        }
    }


    private void validateOrder() {
        final OrderActivity orderActivity = (OrderActivity) Objects.requireNonNull(getActivity());
        if (editDate.getText().toString().isEmpty()) {
            editDate.setError(getString(R.string.required_date));
            return;
        }

        int isToDeliver = 0;
        String userAddress = "";
        String userLocation = orderActivity.location;

        if (shop.getIsDelivering() == Shop.IS_DELIVERING && cbHomeDelivery.isChecked()) {
            if (formOk(editUserAddress, rgLocation, editDate)) {

                if (cbHomeDelivery.isChecked()) isToDeliver = 1;
                userAddress = editUserAddress.getText().toString();
                userLocation = editLocation.getText().toString();

                AlertDialog.Builder builder = buildConfirmationDialog();
                int finalIsToDeliver = isToDeliver;
                String finalUserAddress = userAddress;
                String finalUserLocation = userLocation;

                builder.setPositiveButton("ok", (dialog, id) -> addOrder(getContext(), preparationDate, preparationDate,
                        finalIsToDeliver, finalUserAddress, finalUserLocation));

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        } else {
            AlertDialog.Builder builder = buildConfirmationDialog();
            int finalIsToDeliver = isToDeliver;
            String finalUserAddress = userAddress;
            String finalUserLocation = userLocation;

            builder.setPositiveButton("ok", (dialog, id) -> addOrder(getContext(), preparationDate, preparationDate,
                    finalIsToDeliver, finalUserAddress, finalUserLocation));

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {

        String strMonth = String.valueOf(month+1);
        String strDay = String.valueOf(day);

        if (day < 10) strDay = "0" + strDay;
        if (month < 10) strMonth = "0" + strMonth;

        String selectedDate = strDay + "/" + strMonth + "/" + year;

        editDate.setText(selectedDate);

        preparationDate = formatDate(year, month+1 , day);

    }

    private String formatDate(int year, int month, int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

        String dateInString = year + "-" + month + "-" + day;
        Date date = new Date();
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return UtilsFunctions.dateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    private AlertDialog.Builder buildConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(getString(R.string.confirmation_msg) + " " + shop.getShopName());
        builder.setTitle(R.string.confirmation);
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        return builder;
    }


}
