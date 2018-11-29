package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.models.Amount;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.Description;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Group;
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

public class Synchronizer {

    private final static String LOG_TAG = "Synchronizer";
    public final static int PERIOD = 15 * 60 * 1000;

    public Context context;

    public Synchronizer(Context context) {
        DataBaseHelper.getInstance(context);
        this.context = context;
    }


    public void sync(){
        Log.d(LOG_TAG, "Starting sync");
        String response;

        try {
            final String jsonData = getJSONForSync(context);
            Log.d(LOG_TAG, "json request : " + jsonData);

            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_SYNC,
                    future,future) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", "" + jsonData);
                    return postData;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

            response = future.get(10, TimeUnit.SECONDS);
            Log.d(LOG_TAG, "json response : " + response);
            processSyncResponse(response);
            sendSyncBroadCast(context);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            Log.d(LOG_TAG, "Sync finished");
        }
    }

    private void sendSyncBroadCast(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_REFRESH_AFTER_SYNC);
        context.sendBroadcast(intent);
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

    private void processSyncResponse(String response) {

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

                //updates amounts
                JSONArray jsonAmountsToSync = jsonObject.getJSONArray("amountsToSync");
                insertAmounts(jsonAmountsToSync);
                Log.d(LOG_TAG, "Sync amounts completed. " + jsonAmountsToSync.length() + " inserted or updated.");

                //updates amounts
                JSONArray jsonDescriptionsToSync = jsonObject.getJSONArray("descriptionsToSync");
                insertDescriptions(jsonDescriptionsToSync);
                Log.d(LOG_TAG, "Sync descriptions completed. " + jsonDescriptionsToSync.length() + " inserted or updated.");

            }

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void insertDescriptions(JSONArray jsonDescriptionsToSync) throws JSONException {

        DescriptionsDataManager descriptionsDataManager = new DescriptionsDataManager(context);

        for (int i = 0; i < jsonDescriptionsToSync.length(); i++) {
            JSONObject JSONDesc = jsonDescriptionsToSync.getJSONObject(i);
            int descId = JSONDesc.getInt("desc_id");
            String descValue = JSONDesc.getString("desc_value");
            int dCategoryId = JSONDesc.getInt("d_category_id");

            Description desc = new Description(descId, descValue, dCategoryId);
            descriptionsDataManager.insertDesc(desc);
        }
    }

    private void insertAmounts(JSONArray jsonAmountsToSync) throws JSONException {

        AmountsDataManager amountsDataManager = new AmountsDataManager(context);

        for (int i = 0; i < jsonAmountsToSync.length(); i++) {
            JSONObject JSONAmount = jsonAmountsToSync.getJSONObject(i);
            int amountId = JSONAmount.getInt("amount_id");
            String amountValue = JSONAmount.getString("amount_value");
            int amountTypeId = JSONAmount.getInt("amount_type_id");
            String amountTypeName = JSONAmount.getString("amount_type_name");

            Amount amount = new Amount(amountId, amountValue, amountTypeId, amountTypeName);
            amountsDataManager.insertAmount(amount);
        }
    }

    private void updateUpdatedGoodsByGroupMembers(JSONArray updateUpdatedGoodsByGroupMembers) throws JSONException {
        DataManager dataManager = new DataManager(context);
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);

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
        DataManager dataManager = new DataManager(context);
        CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);

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
        DataManager dataManager = new DataManager(context);
        UsersDataManager usersDataManager = new UsersDataManager(context);

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
            int dCategoryId = JSONCategory.getInt("d_category_id");

            Category category = new Category(categoryName, color, icon,sync,userId, email, serverCategoryId);
            category.setCrudStatus(crudStatus);
            category.setDCategoryID(dCategoryId);
            dataManager.addCategory(context, category);

        }
    }

    private void updateUpdatedGoods(JSONArray jsonUpdatedGoodsServerIds) throws JSONException {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        DataManager dataManager = new DataManager(context);
        for (int i = 0; i < jsonUpdatedGoodsServerIds.length(); i++) {
            int serverGoodId = jsonUpdatedGoodsServerIds.getInt(i);
            Good good = goodsDataProvider.getGoodByServerGoodId(serverGoodId);
            good.setSync(DataBaseHelper.SYNC_STATUS_OK);
            dataManager.updateGood(good);
        }

    }

    private void updateRIs(JSONArray jsonReceivedInvitationsIds) throws JSONException {
        InvitationsDataManager invitationsDataManager = new InvitationsDataManager(context);
        for (int i = 0; i < jsonReceivedInvitationsIds.length(); i++) {
            JSONObject JSONCoUsersIds = jsonReceivedInvitationsIds.getJSONObject(i);
            int receivedInvitationId = JSONCoUsersIds.getInt("receivedInvitationId");

            ReceivedInvitation receivedInvitation = invitationsDataManager.getReceivedInvitationById(receivedInvitationId);
            receivedInvitation.setResponse(CoUser.COMPLETED);
            invitationsDataManager.updateReceivedInvitation(receivedInvitation);
        }
    }

    private void updateCoUsers(JSONArray jsonCoUsersIds) throws JSONException {
        CoUsersDataManager coUsersDataManager = new CoUsersDataManager(context);
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
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        for (int i = 0; i < jsonGoodsIds.length(); i++) {
            JSONObject JSONGoodIds = jsonGoodsIds.getJSONObject(i);
            int deviceGoodId = JSONGoodIds.getInt("deviceGoodId");
            int serverGoodId = JSONGoodIds.getInt("serverGoodId");

            Good good = goodsDataProvider.getGoodById(deviceGoodId);
            good.setServerGoodId(serverGoodId);
            good.setSync(DataBaseHelper.SYNC_STATUS_OK);

            DataManager dataManager = new DataManager(context);
            dataManager.updateGood(good);
        }

    }

    private void updateCategories(JSONArray jsonCategoriesIds) throws JSONException {
        DataManager dataManager = new DataManager(context);
        for (int i = 0; i < jsonCategoriesIds.length(); i++) {
            JSONObject JSONCategoryIds = jsonCategoriesIds.getJSONObject(i);
            int deviceCategoryId = JSONCategoryIds.getInt("deviceCategoryId");
            int serverCategoryId = JSONCategoryIds.getInt("serverCategoryId");

            CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
            Category category = categoriesDataProvider.getCategoryById(deviceCategoryId);

            category.setServerCategoryId(serverCategoryId);
            category.setSync(DataBaseHelper.SYNC_STATUS_OK);
            dataManager.updateCategory(category);
        }
    }

    public void deleteAllUserDataOnServerAndSyncGroup(final Context context, final User user, final ReceivedInvitation invitation) {
        Log.d(LOG_TAG, Constants.TAG_LISTA+"deleteAllUserDataOnServerAndSyncGroup begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_DELETE_USER_CATEGORIES_AND_GOODS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                //adding coUser succeded
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                //insert group data
                                JSONObject JSONGroup = jsonObject.getJSONObject("group");

                                String groupName = JSONGroup.getString("group_name");
                                String ownerEmail = JSONGroup.getString("owner_email");
                                int serverOwnerId = JSONGroup.getInt("server_owner_id");
                                int serverGroupId = JSONGroup.getInt("server_group_id");
                                int syncStatus = DataBaseHelper.SYNC_STATUS_OK;

                                GroupsDataManager groupsDataManager = new GroupsDataManager(context);
                                Group ownerGroup = groupsDataManager.getGroupByOwnerId(serverOwnerId);
                                if (ownerGroup==null){
                                    Group group = new Group(groupName, ownerEmail, serverOwnerId, serverGroupId, syncStatus);

                                    groupsDataManager.addGroup( group);

                                    ownerGroup = groupsDataManager.getGroupByOwnerId(serverOwnerId);
                                }

                                UsersDataManager usersDataManager = new UsersDataManager(context);
                                User currentUser = usersDataManager.getCurrentUser();
                                currentUser.setGroupId(ownerGroup.getGroupId());
                                usersDataManager.updateUser(currentUser);
                                Toast.makeText(context, R.string.member_of_the_team_now, Toast.LENGTH_SHORT).show();

                            }
                            Log.d(LOG_TAG, Constants.TAG_LISTA+"deleteAllUserDataOnServerAndSyncGroup ends");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //adding coUser failed
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postData = new HashMap<>();
                postData.put("server_user_id", "" + user.getServerUserId());
                postData.put("server_co_user_id", "" + invitation.getServerCoUserId());
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }


}
