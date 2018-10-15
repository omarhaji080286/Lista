package com.winservices.wingoods.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.CoUsersDataManager;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.GroupsDataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.Synchronizer;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.CoUser;
import com.winservices.wingoods.models.Group;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InviteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InviteActivity";

    ImageButton btnSendIntivation;
    EditText editEmailInvitation, editGroupName;
    TextView txtMembersList, txtMembers;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        setTitle(getString(R.string.invite_friends));
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSendIntivation = findViewById(R.id.btnSendIntivation);
        editGroupName = findViewById(R.id.editGroupName);
        editEmailInvitation = findViewById(R.id.editEmailInvitation);
        txtMembersList = findViewById(R.id.txtMembersList);
        txtMembers = findViewById(R.id.txtMembers);



        btnSendIntivation.setOnClickListener(this);

        UsersDataManager usersDataManager = new UsersDataManager(this);
        user = usersDataManager.getCurrentUser();

        Group group = user.getGroup(this);
        if ( group != null){
            editGroupName.setText(group.getGroupName());
        }

        displayTeamMembers(user);

    }

    private void displayTeamMembers(User user){
        if (user.getServerGroupId()!=0){
            txtMembers.setVisibility(View.VISIBLE);
            txtMembersList.setVisibility(View.VISIBLE);
            editGroupName.setEnabled(false);

            SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String members = appPreferences.getString(Constants.TEAM_MEMBERS, "Team not created yet");
            txtMembersList.setText(members);
            getTeamMembers(this, user);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSendIntivation:

                String groupName = editGroupName.getText().toString().trim();

                if (groupName.isEmpty()) {
                    editGroupName.setError(getString(R.string.team_name_required));
                    editGroupName.requestFocus();
                    return;
                }

                String coUserEmail = editEmailInvitation.getText().toString().trim();

                if (coUserEmail.isEmpty()) {
                    editEmailInvitation.setError(getString(R.string.emailRequired));
                    editEmailInvitation.requestFocus();
                    return;
                }

                if (coUserEmail.equals(user.getEmail())) {
                    editEmailInvitation.setError(getString(R.string.cant_invite_your_self));
                    editEmailInvitation.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(coUserEmail).matches()) {
                    editEmailInvitation.setError(getString(R.string.emailNotValid));
                    editEmailInvitation.requestFocus();
                    return;
                }

                if (user.getGroup(this)==null && user.getServerGroupId()==0) {
                    createGroupAndsendInvitation();
                } else {
                    addCoUserInvitation(editEmailInvitation.getText().toString());
                }

                UtilsFunctions.hideKeyboard(this, view);
                break;
        }

    }

    private void addCoUserInvitation(String coUserEmail) {

        CoUser coUser = new CoUser(coUserEmail, user.getUserId(), user.getEmail(),
                CoUser.HAS_NOT_RESPONDED, CoUser.PENDING, DataBaseHelper.SYNC_STATUS_FAILED);

        CoUsersDataManager coUsersDataManager = new CoUsersDataManager(this);
        int res1 = coUsersDataManager.addCoUser(this, coUser);

        switch (res1) {
            case Constants.SUCCESS:
                Synchronizer sync = new Synchronizer();
                sync.synchronizeAll(this);
                Toast.makeText(this, R.string.invitation_sent, Toast.LENGTH_SHORT).show();
                break;
            case Constants.DATAEXISTS:
                editEmailInvitation.setError(getString(R.string.invitation_already_sent));
                editEmailInvitation.requestFocus();
                break;
            case Constants.ERROR:
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void createGroupAndsendInvitation(){
        Group groupToAdd = new Group(editGroupName.getText().toString(), user.getEmail(), user.getServerUserId());

        GroupsDataManager groupsDataManager = new GroupsDataManager(this);
        int res0 = groupsDataManager.addGroup(groupToAdd);

        switch (res0) {
            case Constants.SUCCESS:
                editGroupName.setEnabled(false);
                UsersDataManager usersDataManager = new UsersDataManager(this);
                User currentUser = usersDataManager.getCurrentUser();
                Group group = groupsDataManager.getGroupByOwnerId(currentUser.getServerUserId() );
                currentUser.setGroupId(group.getGroupId());
                usersDataManager.updateUser(currentUser);
                addCoUserInvitation(editEmailInvitation.getText().toString());
                break;
            case Constants.DATAEXISTS:
                Toast.makeText(this, R.string.team_already_created, Toast.LENGTH_SHORT).show();
                break;
            case Constants.ERROR:
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                break;
        }

    }


    private void getTeamMembers(final Context context, final User user) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_GET_TEAM_MEMBERS,
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
                                    JSONArray JSONTeamMembers = jsonObject.getJSONArray("teamMembers");
                                    StringBuilder membersList = new StringBuilder();
                                    for (int i = 0; i < JSONTeamMembers.length(); i++) {
                                        JSONObject JSONTeamMember = JSONTeamMembers.getJSONObject(i);

                                        String memberUserName = JSONTeamMember.getString("user_name");
                                        String memberEmail = JSONTeamMember.getString("email");

                                        membersList.append(" - ");
                                        membersList.append(memberUserName);
                                        membersList.append(" ( ");
                                        membersList.append(memberEmail);
                                        membersList.append(" ) ");
                                        membersList.append("\n");
                                    }
                                    txtMembersList.setText(membersList);
                                    updateMemebersInSharedPreferences(membersList.toString());
                                }
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
                    postData.put("jsonData", "" + getJSONForGetTeamMembers(user.getServerUserId()));
                    return postData;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        }
    }

    private String getJSONForGetTeamMembers(int serverUserId){
        final JSONObject root = new JSONObject();
        try {

            root.put("currentServerUserId", serverUserId);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d(TAG, "Can't format JSON");
        }

        return null;
    }

    private void updateMemebersInSharedPreferences(String members){

        SharedPreferences appPreferences;
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString(Constants.TEAM_MEMBERS, members);
        editor.apply();

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
        int fragmentId = getIntent().getIntExtra(Constants.SELECTED_FRAGMENT, R.id.nav_my_goods);
        Intent intent = new Intent();
        intent.putExtra(Constants.SELECTED_FRAGMENT, fragmentId);
        setResult(MainActivity.FRAGMENT_REQUEST_CODE,intent);
        this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataBaseHelper.closeDB();
    }

}
