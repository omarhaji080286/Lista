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

    public Synchronizer(Context context) {
        DataBaseHelper.getInstance(context);

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


}
