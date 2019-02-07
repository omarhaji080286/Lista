package com.winservices.wingoods.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.OrderActivity;
import com.winservices.wingoods.activities.ShopsActivity;
import com.winservices.wingoods.adapters.DefaultCategoriesAdapter;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Shop;
import com.winservices.wingoods.models.ShopType;
import com.winservices.wingoods.models.ShopsFilter;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.PermissionUtil;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ShopsMap extends Fragment implements OnMapReadyCallback {

    private static final String TAG = ShopsMap.class.getSimpleName();

    private final static int REQUEST_ACCESS_FINE_LOCATION = 101;
    private final static int REQUEST_ACCESS_COARSE_LOCATION = 102;

    private final static int TXT_FINE_LOCATION = 1;
    private final static int TXT_COARSE_LOCATION = 2;

    private PermissionUtil permissionUtil;

    private GoogleMap mGoogleMap;
    private View mView;

    private ArrayList<Shop> shops;

    private TextView shopName, shopType, shopPhone, shopCity;
    private CardView cardViewShop;
    private ImageView shopIcon;
    private Button btnOrder;
    private RecyclerView rvDCategories;
    private DefaultCategoriesAdapter dCategoriesAdapter;

    private boolean orderInitiated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_shops_map, container, false);

        cardViewShop = mView.findViewById(R.id.cardview_shop);
        shopIcon = mView.findViewById(R.id.img_shop_icon);
        shopName = mView.findViewById(R.id.txt_shop_name);
        shopType = mView.findViewById(R.id.txt_shop_type);
        shopPhone = mView.findViewById(R.id.txt_shop_phone);
        shopCity = mView.findViewById(R.id.txt_shop_city);
        btnOrder = mView.findViewById(R.id.btn_order);
        rvDCategories = mView.findViewById(R.id.rv_d_categories);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvDCategories.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false ));

        if (GoogleServicesAvailable()) {
            permissionUtil = new PermissionUtil(getContext());
        }

        MapView mapView = mView.findViewById(R.id.mapview_shops);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }


    public boolean GoogleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getContext());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(getActivity(), isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(getContext(), R.string.error_play_services, Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //Location : Rabat Ville Gare train
        goToLocationZoom(34.016517, -6.835741, 10);
        buildMapsData();
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    private void buildMapsData() {
        if (mGoogleMap != null) {
            if (checkPermission(TXT_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkPermission(TXT_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showPermissionExplanation(TXT_FINE_LOCATION);
                } else if (!permissionUtil.checkPermissionPreference("access_fine_location")) {
                    requestPermission(TXT_FINE_LOCATION);
                    permissionUtil.updatePermissionPreference("access_fine_location");
                } else {
                    goToAppSettings();
                }

            } else {
                mGoogleMap.setMyLocationEnabled(true);

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (cardViewShop.getVisibility() == View.VISIBLE) {
                            UtilsFunctions.collapse(cardViewShop);
                        }
                    }
                });

                mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.item_map_shop_info, null);
                        TextView shopName = v.findViewById(R.id.txt_shop_name);
                        TextView shopType = v.findViewById(R.id.txt_shop_type);
                        ImageView shopIcon = v.findViewById(R.id.img_shop_icon);

                        //LatLng ll = marker.getPosition();
                        shopName.setText(marker.getTitle());
                        shopType.setText(marker.getSnippet());

                        switch (marker.getSnippet()) {
                            case Constants.SHOP_TYPE_1:
                                shopIcon.setImageResource(R.drawable.others);
                                break;
                            case Constants.SHOP_TYPE_2:
                                shopIcon.setImageResource(R.drawable.steak);
                                break;
                            case Constants.SHOP_TYPE_3:
                                shopIcon.setImageResource(R.drawable.fruit);
                                break;
                        }

                        return v;
                    }
                });

                Bundle bundle = getArguments();
                if (bundle != null) {
                    shops = (ArrayList<Shop>) bundle.getSerializable(ShopsActivity.SHOPS_TAG);
                    orderInitiated = bundle.getBoolean(Constants.ORDER_INITIATED);
                }
                addShopsMarkers(this.shops);
            }
        }
    }


    private void setMarkersClickListener(final ArrayList<Shop> shopsWithMarkers) {

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (cardViewShop.getVisibility() == View.GONE) {
                    UtilsFunctions.expand(cardViewShop);
                }

                Shop shop = new Shop();
                for (int i = 0; i < shopsWithMarkers.size(); i++) {
                    if (marker.getId().equals(shopsWithMarkers.get(i).getMarkerId())) {
                        shop = shopsWithMarkers.get(i);
                    }
                }

                dCategoriesAdapter = new DefaultCategoriesAdapter(shop.getDefaultCategories(), getContext());
                rvDCategories.setAdapter(dCategoriesAdapter);

                //dCategoriesAdapter.setDCategories(shop.getDefaultCategories());
                shopName.setText(shop.getShopName());
                shopType.setText(shop.getShopType().getShopTypeName());
                shopPhone.setText(shop.getShopPhone());
                shopCity.setText(shop.getCity().getCityName());

                String imagePath = SharedPrefManager.getInstance(getContext()).getShopImagePath(shop.getServerShopId());
                if (imagePath!=null){
                    Bitmap bitmap = UtilsFunctions.getOrientedBitmap(imagePath);
                    shopIcon.setImageBitmap(bitmap);
                } else {
                    shopIcon.setImageResource(R.drawable.default_shop_image);
                }


                if (orderInitiated && canGetOrder(shop)) {
                    btnOrder.setVisibility(View.VISIBLE);
                    final Shop finalShop = shop;
                    btnOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), OrderActivity.class);
                            intent.putExtra(Constants.ORDER_INITIATED, orderInitiated);
                            intent.putExtra(Constants.SELECTED_SHOP_ID, finalShop.getServerShopId());
                            intent.putExtra(Constants.SHOP, finalShop);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).finish();
                        }
                    });
                } else {
                    btnOrder.setVisibility(View.GONE);
                }


                return false;
            }
        });

    }

    private boolean canGetOrder(Shop shop) {

        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(getContext());
        return (categoriesDataProvider.getCategoriesForOrder(shop).size() > 0);

    }

    /*private void getShopDetails(final int serverShopId) {
        currentShop = new Shop();
        currentShop.setServerShopId(serverShopId);

        final Dialog dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), getContext(), R.string.loading).create();
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_GET_SHOP_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                //error in server
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject JSONShop = jsonObject.getJSONObject("shopDetails");

                                JSONArray JSONCategories = JSONShop.getJSONArray("d_categories");
                                Log.d(TAG, "categories: " + JSONCategories.toString());

                                int serverShopTypeId = JSONShop.getInt("server_shop_type_id");
                                String shopTypeName = JSONShop.getString("shop_type_name");
                                ShopType shopType = new ShopType(serverShopTypeId, shopTypeName);

                                int serverCountryId = JSONShop.getInt("server_country_id");
                                String countryName = JSONShop.getString("country_name");
                                Country country = new Country(serverCountryId, countryName);

                                int serverCityId = JSONShop.getInt("server_city_id");
                                String cityName = JSONShop.getString("city_name");
                                City city = new City(serverCityId, cityName, country);

                                currentShop.setServerShopId(JSONShop.getInt("server_shop_id"));
                                currentShop.setShopName(JSONShop.getString("shop_name"));
                                currentShop.setShopAdress(JSONShop.getString("shop_adress"));
                                currentShop.setShopEmail(JSONShop.getString("shop_email"));
                                currentShop.setShopPhone(JSONShop.getString("shop_phone"));
                                currentShop.setLongitude(JSONShop.getDouble("longitude"));
                                currentShop.setLatitude(JSONShop.getDouble("latitude"));
                                currentShop.setShopType(shopType);
                                currentShop.setCity(city);
                                currentShop.setCountry(country);

                                //String shopImage = JSONShop.getString("shop_image");
                                //SharedPrefManager.storeImageToFile(shopImage, shop.getServerShopId());

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
                        dialog.dismiss();
                        Log.d(TAG, "error on response: " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonRequest", String.valueOf(serverShopId));
                return postData;
            }
        };
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

    }*/

    private void addShopsMarkers(final ArrayList<Shop> shops) {

        for (int i = 0; i < shops.size(); i++) {
            final Shop shop = shops.get(i);
            BitmapDescriptor bitmapDescriptor;

            switch (shop.getShopType().getServerShopTypeId()) {
                case 1:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                    break;
                case 2:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                case 3:
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
                default :
                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

            }

            final MarkerOptions shopOptions = new MarkerOptions()
                    .title(shop.getShopName())
                    .snippet(shop.getShopType().getShopTypeName())
                    .icon(bitmapDescriptor)
                    .position(new LatLng(shop.getLatitude(), shop.getLongitude()));

            Marker marker = mGoogleMap.addMarker(shopOptions);
            shops.get(i).setMarkerId(marker.getId());
        }

        setMarkersClickListener(shops);


    }

    private int checkPermission(int permission) {
        int status = PackageManager.PERMISSION_DENIED;
        switch (permission) {
            case TXT_FINE_LOCATION:
                status = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case TXT_COARSE_LOCATION:
                status = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
                break;
        }
        return status;
    }

    private void requestPermission(int permission) {
        switch (permission) {
            case TXT_FINE_LOCATION:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                break;
            case TXT_COARSE_LOCATION:
                //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                break;
        }
    }

    private void showPermissionExplanation(final int permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (permission == TXT_FINE_LOCATION) {
            builder.setMessage("This app needs to access your location. Please allow.");
            builder.setTitle("Location permission needed..");
        } else if (permission == TXT_COARSE_LOCATION) {
            builder.setMessage("This app needs to access your location. Please allow.");
            builder.setTitle("Location permission needed..");
        }

        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermission(permission);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e(TAG, "entered on request permission");
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildMapsData();
                } else {
                    //permission denied
                    Toast.makeText(getContext(), R.string.error_map, Toast.LENGTH_LONG).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void goToAppSettings() {
        Toast.makeText(getContext(), R.string.allow_location_permission_in_settings, Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        this.startActivity(intent);
    }

    public void setShopNameFilter(ArrayList<Shop> newList) {
        mGoogleMap.clear();
        addShopsMarkers(newList);

    }

    public void setShopsFilter(ShopsFilter shopsFilter) {
        ArrayList<Shop> newList = new ArrayList<>();
        for (int j = 0; j < shopsFilter.getSelectedCities().size(); j++) {
            City city = shopsFilter.getSelectedCities().get(j);
            for (int i = 0; i < shops.size(); i++) {
                Shop shop = shops.get(i);
                if (shop.getCity().getServerCityId() == city.getServerCityId()) {
                    newList.add(shop);
                }
            }
        }
        mGoogleMap.clear();
        addShopsMarkers(newList);
    }

    public void setShops(ArrayList<Shop> shops) {
        mGoogleMap.clear();
        addShopsMarkers(shops);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleMap = null;
        shops = null;

    }
}
