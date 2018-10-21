package com.winservices.wingoods.dbhelpers;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Synchronizer {

    private final static String TAG = "Synchronizer";
    public final static int PERIOD = 15 * 60 * 1000;
    private final static int SYNC_PROGRESS_MAX = 8;
    public boolean syncFinished;
    private int syncProgress;
    private Context context;

    public Synchronizer(Context context) {
        this.syncFinished = false;
        this.syncProgress = 0;
        this.context = context;
        DataBaseHelper.getInstance(context);

    }

    private void synchronizeGoods(final Context context, final List<Good> goods) {

        Log.d(TAG, Constants.TAG_LISTA+"synchronizeGoods begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_ADD_GOODS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray JSONGoodsIds = jsonObject.getJSONArray("goodsIds");
                                for (int i = 0; i < JSONGoodsIds.length(); i++) {
                                    JSONObject JSONGoodIds = JSONGoodsIds.getJSONObject(i);
                                    int deviceGoodId = JSONGoodIds.getInt("deviceGoodId");
                                    int serverGoodId = JSONGoodIds.getInt("serverGoodId");

                                    GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
                                    Good good = goodsDataProvider.getGoodById(deviceGoodId);

                                    good.setServerGoodId(serverGoodId);
                                    good.setSync(DataBaseHelper.SYNC_STATUS_OK);

                                    DataManager dataManager = new DataManager(context);
                                    dataManager.updateGood(good);

                                }

                            }
                            syncProgress++;
                            Log.e(TAG,"sync progress = " + syncProgress);
                            Log.d(TAG, Constants.TAG_LISTA+"synchronizeGoods ends");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonData", getJSONForSynchronizeGoods(context, goods));
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private String getJSONForSynchronizeGoods(Context context, final List<Good> goods) {

        final JSONObject root = new JSONObject();
        try {

            UsersDataManager usersDataManager = new UsersDataManager(context);
            int serverUserId = usersDataManager.getCurrentUser().getServerUserId();

            root.put("currentServerUserId", serverUserId);

            JSONArray jsonGoods = new JSONArray();
            for (int i = 0; i < goods.size(); i++) {

                JSONObject JSONGood = goods.get(i).toJSONObject();
                jsonGoods.put(JSONGood);
            }
            root.put("jsonGoods", jsonGoods);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d(TAG, "Can't format JSON");
        }

        return null;
    }



    private void synchronizeCoUser(final Context context, final CoUser coUser) {

        Log.d(TAG, Constants.TAG_LISTA+"synchronizeCoUser begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_ADD_CO_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            //String message = jsonObject.getString("message");
                            if (!error) {
                                //adding coUser succeded
                                CoUsersDataManager coUsersDataManager = new CoUsersDataManager(context);
                                boolean res = coUsersDataManager.updateCoUserAfterSync(coUser.getCoUserId(),
                                                                                        DataBaseHelper.SYNC_STATUS_OK,
                                                                                        jsonObject.getInt("server_co_user_id"));
                                if (!res){
                                    Log.d(TAG, "Error while updating couser");
                                }


                            }
                            syncProgress++;
                            Log.e(TAG,"sync progress = " + syncProgress);
                            Log.d(TAG, Constants.TAG_LISTA+"synchronizeCoUser ends");

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
            protected Map<String, String> getParams() throws AuthFailureError {
                UsersDataManager usersDataManager = new UsersDataManager(context);

                Map<String, String> postData = new HashMap<>();
                postData.put("device_co_user_id", "" + coUser.getCoUserId());
                postData.put("co_user_email", "" + coUser.getCoUserEmail());
                postData.put("email", "" + coUser.getEmail());
                postData.put("sign_up_type", "" + usersDataManager.getCurrentUser().getSignUpType());

                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }

    private void sendCoUserResponseToServer(final Context context, final ReceivedInvitation invitation) {
        Log.d(TAG, Constants.TAG_LISTA+"sendCoUserResponseToServer begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_UPDATE_CO_USER_RESPONSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            if (!error) {
                                //update invitation in SQLite
                                invitation.setResponse(CoUser.COMPLETED);
                                InvitationsDataManager invitationsDataManager = new InvitationsDataManager(context);
                                invitationsDataManager.updateReceivedInvitation( invitation);
                            }
                            syncProgress++;
                            Log.e(TAG,"sync progress = " + syncProgress);
                            Log.d(TAG, Constants.TAG_LISTA+"sendCoUserResponseToServer ends");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //updating CoUser failed
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                UsersDataManager usersDataManager = new UsersDataManager(context);
                Map<String, String> postData = new HashMap<>();
                postData.put("server_co_user_id", "" + invitation.getServerCoUserId());
                postData.put("confirmation_status", "" + invitation.getResponse());
                postData.put("server_user_id", "" + usersDataManager.getCurrentUser().getServerUserId());
                postData.put("server_group_id", "" + invitation.getServerGroupId());
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }


    private void synchronizeGroupData(final Context context, final User user) {

        Log.d(TAG, Constants.TAG_LISTA+"synchronizeGroupData begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_GET_GROUP_DATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");

                            if (!error) {
                                //Add Data imported

                                JSONArray categoriesJSONArray = jsonObject.getJSONArray("categories");

                                for (int i = 0; i < categoriesJSONArray.length(); i++) {
                                    //insert category
                                    JSONArray categoryJSONArray = categoriesJSONArray.getJSONArray(i);

                                    int serverCategoryId = categoryJSONArray.getJSONObject(0).getInt("server_category_id");
                                    String categoryName = categoryJSONArray.getJSONObject(1).getString("category_name");
                                    int color = categoryJSONArray.getJSONObject(2).getInt("category_color");
                                    int icon = categoryJSONArray.getJSONObject(3).getInt("category_icon");
                                    int sync = DataBaseHelper.SYNC_STATUS_OK;                                    String email = categoryJSONArray.getJSONObject(5).getString("email");
                                    int crudStatus = categoryJSONArray.getJSONObject(8).getInt("crud_status");

                                    UsersDataManager usersDataManager = new UsersDataManager(context);
                                    int userId = usersDataManager.getCurrentUser().getUserId();

                                    Category category = new Category(categoryName, color, icon, sync, userId, email, serverCategoryId);
                                    category.setCrudStatus(crudStatus);

                                    DataManager dataManager = new DataManager(context);
                                    dataManager.addCategory(context, category);

                                }

                                JSONArray goodsJSONArray = jsonObject.getJSONArray("goods");

                                for (int i = 0; i < goodsJSONArray.length(); i++) {
                                    //insert good
                                    JSONArray goodJSONArray = goodsJSONArray.getJSONArray(i);

                                    int serverGoodId = goodJSONArray.getJSONObject(0).getInt("server_good_id");
                                    String goodName = goodJSONArray.getJSONObject(1).getString("good_name");
                                    int quantityLevelId = goodJSONArray.getJSONObject(2).getInt("quantity_level");
                                    int isToBuyInt = goodJSONArray.getJSONObject(3).getInt("is_to_buy");
                                    boolean isToBuy = (isToBuyInt == 1);
                                    String email = goodJSONArray.getJSONObject(7).getString("email");
                                    int serverCategoryId = goodJSONArray.getJSONObject(8).getInt("server_category_id");
                                    int crudStatus = goodJSONArray.getJSONObject(9).getInt("crud_status");
                                    String goodDesc = goodJSONArray.getJSONObject(10).getString("good_desc");
                                    int sync = DataBaseHelper.SYNC_STATUS_OK;
                                    int isOrdered = goodJSONArray.getJSONObject(11).getInt("is_ordered");

                                    CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
                                    Category category = categoriesDataProvider.getCategoryByServerCategoryIdAndUserId(serverCategoryId, user.getUserId() );

                                    Good good = new Good(goodName, category.getCategoryId(), quantityLevelId, isToBuy, sync, email, serverGoodId, serverCategoryId);
                                    good.setCrudStatus(crudStatus);
                                    good.setGoodDesc(goodDesc);
                                    good.setIsOrdered(isOrdered);

                                    DataManager dataManager = new DataManager(context);
                                    dataManager.addGood( good);

                                }

                            }
                            syncProgress++;
                            Log.e(TAG,"sync progress = " + syncProgress);
                            Log.d(TAG, Constants.TAG_LISTA+"synchronizeGroupData ends");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error process
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("server_group_id", "" + user.getServerGroupId());
                postData.put("jsonData", "" + getJSONForSynchronizeGroupData(context));
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }

    private String getJSONForSynchronizeGroupData(Context context) {

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


    public void deleteAllUserDataOnServerAndSyncGroup(final Context context, final User user, final ReceivedInvitation invitation) {
        Log.d(TAG, Constants.TAG_LISTA+"deleteAllUserDataOnServerAndSyncGroup begins");

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
                            Log.d(TAG, Constants.TAG_LISTA+"deleteAllUserDataOnServerAndSyncGroup ends");

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("server_user_id", "" + user.getServerUserId());
                postData.put("server_co_user_id", "" + invitation.getServerCoUserId());
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }


    private void updateCategoriesAndGoodsOnServer(final Context context,
                                                         final List<Category> updatedCategories,
                                                         final List<Good> updatedGoods) {

        Log.d(TAG, Constants.TAG_LISTA+"updateCategoriesAndGoodsOnServer begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_UPDATE_CATEGORIES_AND_GOODS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");

                            if (error) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {

                                DataManager dataManager = new DataManager(context);
                                for (int i = 0; i <updatedCategories.size() ; i++) {
                                    Category category = updatedCategories.get(i);
                                    category.setSync(DataBaseHelper.SYNC_STATUS_OK);
                                    dataManager.updateCategory(category);
                                }

                                for (int i = 0; i < updatedGoods.size(); i++) {
                                    Good good = updatedGoods.get(i);
                                    good.setSync(DataBaseHelper.SYNC_STATUS_OK);
                                    dataManager.updateGood(good);
                                }

                            }
                            syncProgress++;
                            Log.e(TAG,"sync progress = " + syncProgress);
                            Log.d(TAG, Constants.TAG_LISTA+"updateCategoriesAndGoodsOnServer ends");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error process
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonData", "" + getJSONForUpdateCategoriesAndGoodsOnServer(context, updatedCategories, updatedGoods ));
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }


    private String getJSONForUpdateCategoriesAndGoodsOnServer(Context context, final List<Category> updatedCategories,
                                                                      final List<Good> updatedGoods) {

        final JSONObject root = new JSONObject();
        try {
            UsersDataManager usersDataManager = new UsersDataManager(context);
            root.put("currentServerUserId", usersDataManager.getCurrentUser().getServerUserId());

            JSONArray jsonUpdatedCategories = new JSONArray();
            for (int i = 0; i < updatedCategories.size(); i++) {

                JSONObject JSONCategory = updatedCategories.get(i).toJSONObject();
                jsonUpdatedCategories.put(JSONCategory);
            }
            root.put("jsonUpdatedCategories", jsonUpdatedCategories);


            JSONArray jsonUpdatedGoods = new JSONArray();
            for (int i = 0; i < updatedGoods.size(); i++) {

                JSONObject JSONGood= updatedGoods.get(i).toJSONObject();
                jsonUpdatedGoods.put(JSONGood);
            }
            root.put("jsonUpdatedGoods", jsonUpdatedGoods);


            return root.toString(1);

        } catch (JSONException e) {
            Log.d("LISTA", "Can't format JSON");
        }

        return null;
    }


    private void updateCategoriesAndGoodsFromServer(final Context context, final User user) {

        Log.d(TAG, Constants.TAG_LISTA+"updateCategoriesAndGoodsFromServer begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_UPDATE_CATEGORIES_AND_GOODS_FROM_SERVER,
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
                                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                JSONArray JSONCategories = jsonObject.getJSONArray("categories");

                                for (int i = 0; i < JSONCategories.length(); i++) {
                                    JSONObject JSONCategory = JSONCategories.getJSONObject(i);

                                    int serverCategoryId = JSONCategory.getInt("server_category_id");

                                    CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
                                    Category category = categoriesDataProvider.getCategoryByServerCategoryIdAndUserId(serverCategoryId, user.getUserId());

                                    category.setCategoryName(JSONCategory.getString("category_name"));
                                    category.setColor(JSONCategory.getInt("category_color"));
                                    category.setIcon(JSONCategory.getInt("category_icon"));
                                    category.setEmail(JSONCategory.getString("email"));
                                    category.setCrudStatus(JSONCategory.getInt("crud_status"));
                                    category.setSync(DataBaseHelper.SYNC_STATUS_OK);

                                    DataManager dataManager = new DataManager(context);
                                    dataManager.updateCategory(category);

                                }

                                JSONArray JSONGoods = jsonObject.getJSONArray("goods");

                                for (int i = 0; i < JSONGoods.length(); i++) {
                                    JSONObject JSONGood= JSONGoods.getJSONObject(i);

                                    int serverGoodId = JSONGood.getInt("server_good_id");

                                    GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
                                    Good good = goodsDataProvider.getGoodByServerGoodId(serverGoodId);

                                    good.setGoodName(JSONGood.getString("good_name"));
                                    good.setQuantityLevelId(JSONGood.getInt("quantity_level"));
                                    good.setToBuy((JSONGood.getInt("is_to_buy")==1));
                                    good.setEmail(JSONGood.getString("email"));
                                    good.setCrudStatus(JSONGood.getInt("crud_status"));
                                    good.setSync(DataBaseHelper.SYNC_STATUS_OK);
                                    good.setIsOrdered(JSONGood.getInt("is_ordered"));

                                    CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
                                    Category category = categoriesDataProvider.getCategoryByServerCategoryIdAndUserId( JSONGood.getInt("server_category_id"), user.getUserId());

                                    good.setCategoryId(category.getCategoryId());

                                    DataManager dataManager = new DataManager(context);
                                    dataManager.updateGood(good);
                                }


                            }

                            syncProgress++;
                            Log.e(TAG,"sync progress = " + syncProgress);
                            //sendSyncBroadCast(context);

                            Log.d(TAG, Constants.TAG_LISTA+"updateCategoriesAndGoodsFromServer ends");

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonData", "" + getJSONForUpdateCategoriesAndGoodsFromServer(user.getServerUserId()));
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }

    private static String getJSONForUpdateCategoriesAndGoodsFromServer(int serverUserId) {

        final JSONObject root = new JSONObject();
        try {

            root.put("currentServerUserId", serverUserId);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d("LISTA", "Can't format JSON");
        }

        return null;
    }

    private void synchronizeCategories(final Context context,
                                                         final List<Category> categories) {

        Log.d(TAG, Constants.TAG_LISTA+"synchronizeCategories begins");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_ADD_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");

                            if (error) {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            } else {

                                JSONArray JSONCategoriesIds = jsonObject.getJSONArray("categoriesIds");
                                for (int i = 0; i < JSONCategoriesIds.length(); i++) {
                                    JSONObject JSONCategoryIds = JSONCategoriesIds.getJSONObject(i);
                                    int deviceCategoryId = JSONCategoryIds.getInt("deviceCategoryId");
                                    int serverCategoryId = JSONCategoryIds.getInt("serverCategoryId");

                                    CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
                                    Category category = categoriesDataProvider.getCategoryById( deviceCategoryId);

                                    category.setServerCategoryId(serverCategoryId);
                                    category.setSync(DataBaseHelper.SYNC_STATUS_OK);

                                    DataManager dataManager = new DataManager(context);
                                    dataManager.updateCategory(category);

                                }

                                syncProgress++;
                                Log.e(TAG,"sync progress = " + syncProgress);

                                syncGoods(context);
                                Log.d(TAG, Constants.TAG_LISTA+"synchronizeCategories ends");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error process
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("jsonData", "" + getJSONForSynchronizeCategories(context, categories));
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

    }


    private static String getJSONForSynchronizeCategories(Context context, final List<Category> categories) {

        final JSONObject root = new JSONObject();
        try {
            UsersDataManager usersDataManager = new UsersDataManager(context);
            int serverUserId = usersDataManager.getCurrentUser().getServerUserId();
            root.put("currentServerUserId", serverUserId);

            JSONArray jsonUpdatedCategories = new JSONArray();
            for (int i = 0; i < categories.size(); i++) {

                JSONObject JSONCategory = categories.get(i).toJSONObject();
                jsonUpdatedCategories.put(JSONCategory);
            }
            root.put("jsonCategories", jsonUpdatedCategories);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d(TAG, "Can't format JSON");
        }

        return null;
    }


    public void synchronizeAll()  {

        Log.d(TAG, Constants.TAG_LISTA+"synchronizeAll begins");

        if (NetworkMonitor.checkNetworkConnection(context)) {

           Thread thread = new Thread() {
                @Override
                public void run() {

                    CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(context);
                    /*List<Category> notSyncCategories = categoriesDataProvider.getNotSyncCategories();

                    if (notSyncCategories.size() > 0) {
                        synchronizeCategories(context, notSyncCategories);
                    } else {
                        syncGoods(context);
                    }*/

                   /* CoUsersDataManager coUsersDataManager = new CoUsersDataManager(context);
                    List<CoUser> notSyncCoUsers = coUsersDataManager.getNotSyncCoUsers();

                    for (int i = 0; i < notSyncCoUsers.size(); i++) {
                        synchronizeCoUser(context, notSyncCoUsers.get(i));
                    }*/

                    //InvitationsDataManager invitationsDataManager = new InvitationsDataManager(context);
                    //List<ReceivedInvitation> notSyncResponses = invitationsDataManager.getNotSyncResponses();

                   /* for (int i = 0; i < notSyncResponses.size(); i++) {
                        sendCoUserResponseToServer(context, notSyncResponses.get(i));
                    }*/

                    UsersDataManager usersDataManager = new UsersDataManager(context);
                    User currentUser = usersDataManager.getCurrentUser();
                    /*Group currentGroup = currentUser.getGroup(context);
                    if (currentGroup != null && currentGroup.getSyncStatus() == DataBaseHelper.SYNC_STATUS_FAILED) {
                        synchronizeGroup(context, currentGroup);
                    }*/

                    /*if (currentUser.getServerGroupId() != 0) {
                        synchronizeGroupData(context, currentUser);
                    }*/

                    /*List<Category> updatedCategories = categoriesDataProvider.getUpdatedCategories();

                    GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
                    List<Good> updatedGoods = goodsDataProvider.getUpdatedGoods(context);

                    if (updatedCategories.size() > 0 || updatedGoods.size() > 0) {
                        updateCategoriesAndGoodsOnServer(context, updatedCategories, updatedGoods);
                    }*/

                    updateCategoriesAndGoodsFromServer(context, currentUser);

                }
            };

            thread.start();

        }

        Log.d(TAG, Constants.TAG_LISTA+"synchronizeAll ends");


    }

    private void syncGoods(Context context){
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(context);
        List<Good> notSyncGoods = goodsDataProvider.getNotSyncGoods(context);
        if (notSyncGoods.size()>0) {
            synchronizeGoods(context, notSyncGoods);
        }
    }

    private void sendSyncBroadCast(Context context){
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_REFRESH_AFTER_SYNC);
        context.sendBroadcast(intent);
    }



}
