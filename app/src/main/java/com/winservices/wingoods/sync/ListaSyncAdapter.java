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
import com.winservices.wingoods.dbhelpers.CoUsersDataManager;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.InvitationsDataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.ReceivedInvitation;
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
        String response = null;

        try {

            final String jsonData = getJSONForSync(getContext());
            Log.d(LOG_TAG, "json request : " + jsonData);

            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_SYNC,
                    future,future) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", "" + jsonData);
                    return postData;
                }
            };
            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

            response = future.get(10, TimeUnit.SECONDS);
            Log.d(LOG_TAG, "json response : " + response);
            processSyncResponse(response);

        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }


    private String getJSONForSync(Context context) {

        final JSONObject root = new JSONObject();
        try {

            //Get the current server user id
            UsersDataManager usersDataManager = new UsersDataManager(context);
            int serverUserId = usersDataManager.getCurrentUser().getServerUserId();
            root.put("serverUserId", serverUserId);

            //Not Sync Categories
            CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
            List<Category> notSyncCategories = categoriesDataProvider.getNotSyncCategories();
            JSONArray jsonNotSyncCategories = Category.listToJSONArray(notSyncCategories);
            root.put("jsonNotSyncCategories",jsonNotSyncCategories);

            //Not Sync Goods
           GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
            List<Good> notSyncGoods = goodsDataProvider.getNotSyncGoods(context);
            JSONArray jsonNotSyncGoods = Good.listToJSONArray(notSyncGoods);
            root.put("jsonNotSyncGoods",jsonNotSyncGoods);

            //Not Sync CoUsers
            CoUsersDataManager coUsersDataManager = new CoUsersDataManager(context);
            List<CoUser> notSyncCoUsers = coUsersDataManager.getNotSyncCoUsers();
            JSONArray jsonNotSyncCoUsers = CoUser.listToJSONArray(notSyncCoUsers);
            root.put("jsonNotSyncCoUsers",jsonNotSyncCoUsers);

            //Not Sync Received Invitations (responses)
            InvitationsDataManager invitationsDataManager = new InvitationsDataManager(context);
            List<ReceivedInvitation> notSyncReceivedInvitations = invitationsDataManager.getNotSyncReceivedInvitations();
            JSONArray jsonNotSyncReceivedInvitations = ReceivedInvitation.listToJSONArray(notSyncReceivedInvitations);
            root.put("jsonNotSyncReceivedInvitations",jsonNotSyncReceivedInvitations);

            return root.toString(1);
        } catch (JSONException e) {
            Log.d("LISTA", "Can't format JSON");
        }

        return null;
    }

    private void processSyncResponse(String response) throws JSONException{

        try{
            JSONObject jsonObject = new JSONObject(response);
            boolean error = jsonObject.getBoolean("error");
            String message = jsonObject.getString("message");

            if (error) {
                Log.e(LOG_TAG, "Error : " + message);
            } else {
                JSONArray jsonCategoriesIds = jsonObject.getJSONArray("categoriesIds");
                updateCategories(jsonCategoriesIds);
                Log.d(LOG_TAG, "Sync Categories completed. " + jsonCategoriesIds.length() + " updated.");

                JSONArray jsonGoodsIds = jsonObject.getJSONArray("goodsIds");
                updateGoods(jsonGoodsIds);
                Log.d(LOG_TAG, "Sync Goods completed. " + jsonGoodsIds.length() + " updated.");

                JSONArray jsonCoUsersIds = jsonObject.getJSONArray("coUsersIds");
                updateCoUsers(jsonCoUsersIds);
                Log.d(LOG_TAG, "Sync CoUsers completed. " + jsonCoUsersIds.length() + " updated.");

                JSONArray jsonReceivedInvitationsIds = jsonObject.getJSONArray("receivedInvitationIds");
                updateRIs(jsonReceivedInvitationsIds);
                Log.d(LOG_TAG, "Sync Received Invitations completed. " + jsonReceivedInvitationsIds.length() + " updated.");


            }

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private void updateRIs(JSONArray jsonReceivedInvitationsIds) throws JSONException {
        InvitationsDataManager invitationsDataManager = new InvitationsDataManager(getContext());
        for (int i = 0; i < jsonReceivedInvitationsIds.length(); i++) {
            JSONObject JSONCoUsersIds = jsonReceivedInvitationsIds.getJSONObject(i);
            int receivedInvitationId = JSONCoUsersIds.getInt("receivedInvitationId");

            ReceivedInvitation receivedInvitation = invitationsDataManager.getReceivedInvitationById(receivedInvitationId);
            receivedInvitation.setResponse(CoUser.COMPLETED);
            invitationsDataManager.updateReceivedInvitation(receivedInvitation);

        }
    }

    private void updateCoUsers(JSONArray jsonCoUsersIds) throws JSONException {
        CoUsersDataManager coUsersDataManager = new CoUsersDataManager(getContext());
        for (int i = 0; i < jsonCoUsersIds.length(); i++) {
            JSONObject JSONCoUsersIds = jsonCoUsersIds.getJSONObject(i);
            int deviceCoUserId = JSONCoUsersIds.getInt("deviceCoUserId");
            int serverCoUserId = JSONCoUsersIds.getInt("serverCoUserId");

            CoUser coUser = coUsersDataManager.getCoUserById(deviceCoUserId);
            coUser.setServerCoUserId(serverCoUserId);
            coUser.setSyncStatus(DataBaseHelper.SYNC_STATUS_OK);

            coUsersDataManager.updateCoUser(coUser);

        }
    }

    private void updateGoods(JSONArray jsonGoodsIds) throws JSONException {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(getContext());
        for (int i = 0; i < jsonGoodsIds.length(); i++) {
            JSONObject JSONGoodIds = jsonGoodsIds.getJSONObject(i);
            int deviceGoodId = JSONGoodIds.getInt("deviceGoodId");
            int serverGoodId = JSONGoodIds.getInt("serverGoodId");

            Good good = goodsDataProvider.getGoodById(deviceGoodId);
            good.setServerGoodId(serverGoodId);
            good.setSync(DataBaseHelper.SYNC_STATUS_OK);

            DataManager dataManager = new DataManager(getContext());
            dataManager.updateGood(good);
        }

    }

    private void updateCategories(JSONArray jsonCategoriesIds) throws JSONException {
        DataManager dataManager = new DataManager(getContext());
        for (int i = 0; i < jsonCategoriesIds.length(); i++) {
            JSONObject JSONCategoryIds = jsonCategoriesIds.getJSONObject(i);
            int deviceCategoryId = JSONCategoryIds.getInt("deviceCategoryId");
            int serverCategoryId = JSONCategoryIds.getInt("serverCategoryId");

            CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(getContext());
            Category category = categoriesDataProvider.getCategoryById( deviceCategoryId);

            category.setServerCategoryId(serverCategoryId);
            category.setSync(DataBaseHelper.SYNC_STATUS_OK);
            dataManager.updateCategory(category);
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
