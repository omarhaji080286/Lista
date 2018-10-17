package com.winservices.wingoods.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.BuildConfig;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ListaSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = ListaSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    ContentResolver mContentResolver;

    public ListaSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        mContentResolver.delete(Good.GoodEntry.CONTENT_URI, null, null);
        Log.d(LOG_TAG, "Starting sync");
        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String forecastJsonStr = null;

        final String FORECAST_BASE_URL = "http://lista.onlinewebshop.net/webservices/getGoodsSyncTest.php";

        try {

            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    FORECAST_BASE_URL,
                    future,future) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", "" + getJSONForSync(getContext()));
                    return postData;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

            forecastJsonStr = future.get(10, TimeUnit.SECONDS);
            getGoodsData(forecastJsonStr);

        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }


    private String getJSONForSync(Context context) {

        final JSONObject root = new JSONObject();
        try {

            JSONArray categoriesServerIds = new JSONArray();
            CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
            List<Category> categories = categoriesDataProvider.getExcludedCategoriesFromSync();

            for (int i = 0; i < categories.size(); i++) {
                categoriesServerIds.put(categories.get(i).getServerCategoryId());
            }
            root.put("categoriesServerIds", categoriesServerIds);

            JSONArray goodsServerIds = new JSONArray();
            GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
            List<Good> goods = goodsDataProvider.getExcludedGoodsFromSync();
            for (int i = 0; i < goods.size(); i++) {
                goodsServerIds.put(goods.get(i).getServerGoodId());
            }
            root.put("goodsServerIds", goodsServerIds);

            UsersDataManager usersDataManager = new UsersDataManager(context);
            root.put("serverUserId", usersDataManager.getCurrentUser().getServerUserId());

            return root.toString(1);
        } catch (JSONException e) {
            Log.d("LISTA", "Can't format JSON");
        }

        return null;
    }

    private void getGoodsData(String forecastJsonStr) throws JSONException{

        try{

            JSONObject jsonObject = new JSONObject(forecastJsonStr);
            JSONArray goodsJSONArray = jsonObject.getJSONArray("goods");

            JSONArray JSONGoods = jsonObject.getJSONArray("goods");

            for (int i = 0; i < JSONGoods.length(); i++) {
                JSONObject JSONGood= JSONGoods.getJSONObject(i);

                ContentValues goodsValues = new ContentValues();

                goodsValues.put(Good.GoodEntry.COLUMN_CATEGORY_ID, 2);
                goodsValues.put(Good.GoodEntry.COLUMN_CRUD_STATUS, JSONGood.getInt("crud_status"));
                goodsValues.put(Good.GoodEntry.COLUMN_EMAIL, JSONGood.getString("email"));
                goodsValues.put(Good.GoodEntry.COLUMN_GOOD_NAME, JSONGood.getString("good_name"));
                goodsValues.put(Good.GoodEntry.COLUMN_IS_ORDERED, JSONGood.getInt("is_ordered"));
                goodsValues.put(Good.GoodEntry.COLUMN_IS_TO_BUY, JSONGood.getInt("is_to_buy"));
                goodsValues.put(Good.GoodEntry.COLUMN_QUANTITY_LEVEL,JSONGood.getInt("quantity_level") );
                goodsValues.put(Good.GoodEntry.COLUMN_SERVER_GOOD_ID, JSONGood.getInt("server_good_id"));
                goodsValues.put(Good.GoodEntry.COLUMN_SYNC_STATUS,DataBaseHelper.SYNC_STATUS_OK );

                mContentResolver.insert(Good.GoodEntry.CONTENT_URI, goodsValues);
            }

            Log.d(LOG_TAG, "Sync completed. " + JSONGoods.length() + " Inserted.");
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
        /*} else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }*/
    }

    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context){

        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name),context.getString(R.string.sync_account_type) );
        if (null==accountManager.getPassword(newAccount)){
            if (!accountManager.addAccountExplicitly(newAccount,"",null)){
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context){
        ListaSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        //syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }


}
