package com.winservices.wingoods.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
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
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ListaSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = ListaSyncAdapter.class.getSimpleName();
    private static final int SYNC_INTERVAL = 60 * 15;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private ContentResolver mContentResolver;

    ListaSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");
        String response;

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
            sendSyncBroadCast(getContext());

        } catch (InterruptedException | ExecutionException | JSONException | TimeoutException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            Log.d(LOG_TAG, "Sync finished");
        }

    }

    private String getJSONForSync(Context context) {

        final JSONObject root = new JSONObject();
        try {

            //Get the current server user id
            UsersDataManager usersDataManager = new UsersDataManager(context);
            User user = usersDataManager.getCurrentUser();
            int serverUserId = user.getServerUserId();
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

            //TODO updated Categories (not Sync yet)
            /*List<Category> updatedCategories = categoriesDataProvider.getUpdatedCategories();
            JSONArray jsonUpdatedCategories = Category.listToJSONArray(updatedCategories);
            root.put("jsonUpdatedCategories",jsonUpdatedCategories);*/

            //updated Goods (not Sync yet)
            List<Good> notSyncUpdatedGoods = goodsDataProvider.getUpdatedGoods(context);
            JSONArray jsonNotSyncUpdatedGoods = Good.listToJSONArray(notSyncUpdatedGoods);
            root.put("jsonNotSyncUpdatedGoods",jsonNotSyncUpdatedGoods);

            //get the current user server group id (0 if does not have a group)
            int serverGroupId = usersDataManager.getCurrentUser().getServerGroupId();
            root.put("serverGroupId", serverGroupId);

            //Sync Group Data : getting Excluded Categories from Sync
            JSONArray excludedServerCategoriesIds = new JSONArray();
            List<Category> categories = categoriesDataProvider.getExcludedCategoriesFromSync();
            for (int i = 0; i < categories.size(); i++) {
                excludedServerCategoriesIds.put(categories.get(i).getServerCategoryId());
            }
            root.put("excludedServerCategoriesIds", excludedServerCategoriesIds);

            //Sync Group Data : getting Excluded goods from Sync
            JSONArray excludedServerGoodsIds = new JSONArray();
            List<Good> goods = goodsDataProvider.getExcludedGoodsFromSync();
            for (int i = 0; i < goods.size(); i++) {
                excludedServerGoodsIds.put(goods.get(i).getServerGoodId());
            }
            root.put("excludedServerGoodsIds", excludedServerGoodsIds);

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

                //updates categories with their server ids
                JSONArray jsonCategoriesIds = jsonObject.getJSONArray("categoriesIds");
                updateCategories(jsonCategoriesIds);
                Log.d(LOG_TAG, "Sync Categories completed. " + jsonCategoriesIds.length() + " updated.");

                //updates goods with their server ids
                JSONArray jsonGoodsIds = jsonObject.getJSONArray("goodsIds");
                updateGoods(jsonGoodsIds);
                Log.d(LOG_TAG, "Sync Goods completed. " + jsonGoodsIds.length() + " updated.");

                //updates coUsers withe their server ids
                JSONArray jsonCoUsersIds = jsonObject.getJSONArray("coUsersIds");
                updateCoUsers(jsonCoUsersIds);
                Log.d(LOG_TAG, "Sync CoUsers completed. " + jsonCoUsersIds.length() + " updated.");

                //updates received invitations status to "completed"
                JSONArray jsonReceivedInvitationsIds = jsonObject.getJSONArray("receivedInvitationIds");
                updateRIs(jsonReceivedInvitationsIds);
                Log.d(LOG_TAG, "Sync Received Invitations completed. " + jsonReceivedInvitationsIds.length() + " updated.");

                //updates goods status to sync ok
                JSONArray jsonUpdatedGoodsServerIds = jsonObject.getJSONArray("updatedGoodsServerIds");
                updateUpdatedGoods(jsonUpdatedGoodsServerIds);
                Log.d(LOG_TAG, "Sync Updated Goods completed. " + jsonUpdatedGoodsServerIds.length() + " updated.");

                //TODO - updates categories status to sync ok

                //inserts categories relative to the group
                JSONArray jsonGroupCategoriesToSync = jsonObject.getJSONArray("groupCategoriesToSync");
                insertCategories(jsonGroupCategoriesToSync);
                Log.d(LOG_TAG, "Sync Categories completed. " + jsonGroupCategoriesToSync.length() + " inserted.");

                //inserts Goods relative to the group
                JSONArray jsonGroupGoodsToSync = jsonObject.getJSONArray("groupGoodsToSync");
                insertGoods(jsonGroupGoodsToSync);
                Log.d(LOG_TAG, "Sync goods completed. " + jsonGroupGoodsToSync.length() + " inserted.");

                //updates Goods goods Data relative to the group
                JSONArray jsonUpdatedGoodsByGroupMembers = jsonObject.getJSONArray("updatedGoodsByGroupMembers");
                updateUpdatedGoodsByGroupMembers(jsonUpdatedGoodsByGroupMembers);
                Log.d(LOG_TAG, "Sync updated goods by group members completed. " + jsonUpdatedGoodsByGroupMembers.length() + " updated.");

            }

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void updateUpdatedGoodsByGroupMembers(JSONArray updateUpdatedGoodsByGroupMembers) throws JSONException {
        DataManager dataManager = new DataManager(getContext());
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(getContext());

        for (int i = 0; i < updateUpdatedGoodsByGroupMembers.length(); i++) {
            JSONObject JSONGood = updateUpdatedGoodsByGroupMembers.getJSONObject(i);
            Good good = goodsDataProvider.getGoodByServerGoodId(JSONGood.getInt("serverGoodId"));
            good.setGoodName(JSONGood.getString("goodName"));
            good.setQuantityLevelId(JSONGood.getInt("quantityLevel"));
            good.setToBuy((JSONGood.getInt("isToBuy")==1));
            good.setSync(JSONGood.getInt("syncStatus"));
            good.setEmail(JSONGood.getString("email"));
            good.setServerCategoryId(JSONGood.getInt("serverCategoryId"));
            good.setCrudStatus(JSONGood.getInt("crudStatus"));
            good.setIsOrdered(JSONGood.getInt("isOrdered"));

            dataManager.updateGood(good);
        }
    }

    private void insertGoods(JSONArray jsonGroupGoodsToSync) throws JSONException {
        DataManager dataManager = new DataManager(getContext());
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(getContext());

        for (int i = 0; i < jsonGroupGoodsToSync.length(); i++) {
            JSONObject JSONGood = jsonGroupGoodsToSync.getJSONObject(i);
            String goodName = JSONGood.getString("goodName");
            String goodDesc = JSONGood.getString("goodDesc");
            int isToBuy = JSONGood.getInt("isToBuy");
            int sync = DataBaseHelper.SYNC_STATUS_OK;
            String email = JSONGood.getString("email");
            int crudStatus = JSONGood.getInt("crudStatus");
            int serverGoodId = JSONGood.getInt("serverGoodId");
            int isOrdered = JSONGood.getInt("isOrdered");
            int serverCategoryId = JSONGood.getInt("serverCategoryId");
            int quantityLevelId = 0;
            int categoryId = categoriesDataProvider.getCategoryByServerCategoryId(serverCategoryId).getCategoryId();

            Good good = new Good(goodName, categoryId, quantityLevelId, (isToBuy==1),
                                            sync, email, serverGoodId, serverCategoryId);
            good.setGoodDesc(goodDesc);
            good.setIsOrdered(isOrdered);
            good.setCrudStatus(crudStatus);

            dataManager.addGood(good);

        }
    }

    private void insertCategories(JSONArray jsonGroupCategoriesToSync) throws JSONException {
        DataManager dataManager = new DataManager(getContext());
        UsersDataManager usersDataManager = new UsersDataManager(getContext());

        for (int i = 0; i < jsonGroupCategoriesToSync.length(); i++) {
            JSONObject JSONCategory = jsonGroupCategoriesToSync.getJSONObject(i);
            int serverCategoryId = JSONCategory.getInt("serverCategoryId");
            String categoryName = JSONCategory.getString("categoryName");
            int color = JSONCategory.getInt("categoryColor");
            int icon = JSONCategory.getInt("categoryIcon");
            int sync = DataBaseHelper.SYNC_STATUS_OK;
            String email = JSONCategory.getString("email");
            int crudStatus = JSONCategory.getInt("crudStatus");
            int userId = usersDataManager.getCurrentUser().getUserId();

            Category category = new Category(categoryName, color, icon,sync,userId, email, serverCategoryId);
            category.setCrudStatus(crudStatus);
            dataManager.addCategory(getContext(), category);

        }
    }

    private void updateUpdatedGoods(JSONArray jsonUpdatedGoodsServerIds) throws JSONException {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(getContext());
        DataManager dataManager = new DataManager(getContext());
        for (int i = 0; i < jsonUpdatedGoodsServerIds.length(); i++) {
            int serverGoodId = jsonUpdatedGoodsServerIds.getInt(i);
            Good good = goodsDataProvider.getGoodByServerGoodId(serverGoodId);
            good.setSync(DataBaseHelper.SYNC_STATUS_OK);
            dataManager.updateGood(good);
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
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }

    private void sendSyncBroadCast(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_REFRESH_AFTER_SYNC);
        context.sendBroadcast(intent);
    }

}
