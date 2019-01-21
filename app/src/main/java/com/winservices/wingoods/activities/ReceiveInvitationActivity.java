package com.winservices.wingoods.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.ReceivedInvitationsAdapter;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.InvitationsDataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.ReceivedInvitation;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiveInvitationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ReceivedInvitationsAdapter mAdapter;
    private List<ReceivedInvitation> invitationsList = new ArrayList<>();
    private TextView txtNoInvitations;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_invitation);

        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), this, R.string.loading).create();
        dialog.show();

        setTitle(getString(R.string.my_invitations));
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = findViewById(R.id.invitations_recyclerview);
        txtNoInvitations = findViewById(R.id.txt_no_invitations);

        UsersDataManager usersDataManager = new UsersDataManager(this);
        User user = usersDataManager.getCurrentUser();

        getInvitations(this, user);


    }

    private void getInvitations(final Context context, final User user) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_GET_INVITATIONS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");

                                if (error) {
                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                } else {

                                    JSONArray invitations = jsonObject.getJSONArray("invitations");

                                    if (invitations.length()==0){
                                        txtNoInvitations.setVisibility(View.VISIBLE);
                                    } else {

                                        for (int j = 0; j < invitations.length(); j = j + 1) {

                                            JSONObject JSONInvitation = invitations.getJSONObject(j);

                                            String senderPhone = JSONInvitation.getString("user_phone");
                                            int serverCoUserId = JSONInvitation.getInt("server_co_user_id");
                                            int serverGroupId = JSONInvitation.getInt("server_group_id");

                                            JSONArray categoriesNames = JSONInvitation.getJSONArray("categories_names");

                                            StringBuilder categories = new StringBuilder();
                                            for (int i = 0; i < categoriesNames.length(); ++i) {
                                                JSONObject categoryJSON = categoriesNames.getJSONObject(i);
                                                categories.append(" - ");
                                                categories.append(categoryJSON.getString("category_name"));
                                                if (i != categoriesNames.length() - 1) {
                                                    categories.append("\n");
                                                }
                                            }

                                            ReceivedInvitation invitation = new ReceivedInvitation(senderPhone, categories.toString(), CoUser.PENDING);
                                            invitation.setServerCoUserId(serverCoUserId);
                                            invitation.setServerGroupId(serverGroupId);

                                            InvitationsDataManager invitationsDataManager = new InvitationsDataManager(context);
                                            invitationsDataManager.addReceivedInvitation(invitation);

                                            invitationsList.add(invitation);

                                        }

                                        mAdapter = new ReceivedInvitationsAdapter(context, invitationsList);

                                        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                        mRecyclerView.setAdapter(mAdapter);
                                    }
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
                            //request failed
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("co_user_phone", "" + user.getUserPhone());
                    return postData;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            // Network Problem
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Synchronizer.synchronizeAll(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home :
                returnToMainActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnToMainActivity(){
        int fragmentId = getIntent().getIntExtra(Constants.SELECTED_FRAGMENT, 101);
        Intent intent = new Intent();
        intent.putExtra(Constants.SELECTED_FRAGMENT, fragmentId);
        setResult(MainActivity.FRAGMENT_REQUEST_CODE,intent);
        this.finish();
    }


}
