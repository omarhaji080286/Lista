package com.winservices.wingoods.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Color;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.Icon;
import com.winservices.wingoods.utils.NetworkMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener{

    TextView signUpTxt;
    EditText emailEdit, passEdit, userNameEdit;
    Button loginBtn;
    ProgressBar progressBar;
    public SharedPreferences appPreferences;
    boolean isAppInstalled = false;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;

    private CallbackManager callbackManager;

    private DataBaseHelper newDB;

    //test commit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //Create the Data Base
         newDB = new DataBaseHelper(this);

        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        createShortcut();

        signUpTxt = findViewById(R.id.signUpText);
        userNameEdit = findViewById(R.id.edit_user_name);
        emailEdit = findViewById(R.id.emailEdit);
        passEdit = findViewById(R.id.passEdit);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.loginProgressBar);

        signUpTxt.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        //FaceBook Sign in code
        //FacebookSdk.sdkInitialize(this);
        LoginButton fbLoginButton = findViewById(R.id.facebook_sign_in_btn);
        fbLoginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        signInWithFB();

        //Google sign in code

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        startMainActivity();

    }


    private void startMainActivity(){

        UsersDataManager usersDataManager = new UsersDataManager(this);
        User currentUser = usersDataManager.getCurrentUser();
        usersDataManager.closeDB();

        if (currentUser!=null) {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.signUpText :
                if (signUpTxt.getText().toString().equals(getString(R.string.already_have_account_log_in))){
                    signUpTxt.setText(getString(R.string.new_here_sign_up));
                    userNameEdit.setVisibility(View.GONE);
                    loginBtn.setText(getString(R.string.log_in));
                } else {
                    signUpTxt.setText(getString(R.string.already_have_account_log_in));
                    userNameEdit.setVisibility(View.VISIBLE);
                    loginBtn.setText(getString(R.string.sign_up));
                }

                break;
            case R.id.loginBtn :
                if (loginBtn.getText().toString().equals(getString(R.string.log_in))){
                    authentificateUser();
                } else {
                    registerUser();
                }

                break;
            case R.id.google_sign_in_button :
                googleSignIn();
                break;
        }

    }


    private void registerUser() {
        String userName = userNameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();

        if (userName.isEmpty()) {
            userNameEdit.setError(getString(R.string.user_name_required));
            userNameEdit.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEdit.setError(getString(R.string.emailRequired));
            emailEdit.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError(getString(R.string.emailNotValid));
            emailEdit.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            passEdit.setError(getString(R.string.passwordRequired));
            passEdit.requestFocus();
            return;
        }

        if (pass.length() < Constants.MIN_LENGHT_PASSWORD) {
            passEdit.setError(getString(R.string.minLenghtPassword) + " " + Constants.MIN_LENGHT_PASSWORD);
            passEdit.requestFocus();
            return;
        }

        //insert the user in the DB
        User user = new User(email, pass, userName);
        user.setSignUpType(DataBaseHelper.LISTA);
        progressBar.setVisibility(View.VISIBLE);
        registerUserToAppServer(this, user);

    }

    private void signInWithFB() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getUserDetailsFromFB(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        //Toast.makeText(AuthActivity.this, "login cancelled ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(AuthActivity.this, "login error : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        //Facebook returned result
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void getUserDetailsFromFB(AccessToken accessToken) {

        GraphRequest req=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                //Toast.makeText(getApplicationContext(),"graph request completed",Toast.LENGTH_SHORT).show();
                try{
                    String email =  object.getString("email");
                    /*String birthday = object.getString("birthday");
                    String gender = object.getString("gender");*/
                    String name = object.getString("name");
                    /*String id = object.getString("id");
                    String photourl =object.getJSONObject("picture").getJSONObject("data").getString("url");*/

                    UsersDataManager usersDataManager = new UsersDataManager(getApplicationContext());
                    User currentUser = usersDataManager.getUserByEmail( email, DataBaseHelper.FACEBOOK);
                    usersDataManager.closeDB();

                    if (currentUser!=null){
                        authentificateUserOnServer(getApplicationContext(), currentUser);
                    } else {
                        User userToRegiter = new User(email, "facebook", name);
                        userToRegiter.setSignUpType(DataBaseHelper.FACEBOOK);
                        registerUserToAppServer(getApplicationContext(), userToRegiter);
                    }

                }catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(),"request error : "+e.getMessage(),Toast.LENGTH_SHORT).show();

                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
        req.setParameters(parameters);
        req.executeAsync();
    }




    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null) {
                String personName = account.getDisplayName();
                //String personGivenName = account.getGivenName();
                //String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                //String personId = account.getId();
                //Uri personPhoto = account.getPhotoUrl();
                UsersDataManager usersDataManager = new UsersDataManager(this);
                User currentUser = usersDataManager.getUserByEmail(personEmail, DataBaseHelper.GOOGLE);
                usersDataManager.closeDB();
                if (currentUser!=null){
                    authentificateUserOnServer(this, currentUser);
                } else {
                    User userToRegiter = new User(personEmail, "google", personName );
                    userToRegiter.setSignUpType(DataBaseHelper.GOOGLE);
                    registerUserToAppServer(this, userToRegiter);
                }


            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            //Toast.makeText(this, "Error exception = " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }


    void authentificateUser(){
        String email = emailEdit.getText().toString().trim();
        String pass = passEdit.getText().toString().trim();

        if (email.isEmpty()) {
            emailEdit.setError(getString(R.string.emailRequired));
            emailEdit.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError(getString(R.string.emailNotValid));
            emailEdit.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            passEdit.setError(getString(R.string.passwordRequired));
            passEdit.requestFocus();
            return;
        }

        if (pass.length() < Constants.MIN_LENGHT_PASSWORD) {
            passEdit.setError(getString(R.string.minLenghtPassword) + " " + Constants.MIN_LENGHT_PASSWORD);
            passEdit.requestFocus();
            return;
        }

        User user = new User(email, pass);
        user.setSignUpType(DataBaseHelper.LISTA);
        progressBar.setVisibility(View.VISIBLE);
        authentificateUserOnServer(getApplicationContext(), user);
    }

    private void authentificateUserOnServer(final Context context, final User user){
        if (NetworkMonitor.checkNetworkConnection(context)) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_LOGIN_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");
                                String message = jsonObject.getString("message");
                                if (error) {
                                    //login FAILED
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    //login success
                                    int serverUserId = jsonObject.getInt("server_user_id");
                                    user.setServerUserId(serverUserId);
                                    UsersDataManager usersDataManager = new UsersDataManager(context);
                                    usersDataManager.updateLastLoggedIn(serverUserId);
                                    usersDataManager.closeDB();
                                    startActivity(new Intent(context, MainActivity.class));
                                    finish();
                                }
                                progressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Registering FAILED
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("email", "" + user.getEmail());
                    postData.put("password", "" + user.getPassword());
                    postData.put("sign_up_type", "" + user.getSignUpType());
                    return postData;
                }
            };
            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

        } else {
            // Network Problem
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void createShortcut() {

        isAppInstalled = appPreferences.getBoolean(Constants.SP_IS_APP_INSTALLED, false);

        if (!isAppInstalled) {
            //  create shortcut
            Intent shortcutIntent = new Intent(getApplicationContext(), AuthActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource
                    .fromContext(getApplicationContext(), R.drawable.logo));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);

            //Make preference true
            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putBoolean(Constants.SP_IS_APP_INSTALLED, true);
            editor.apply();
        }
    }


    public void registerUserToAppServer(final Context context, final User userToRegister) {

        if (NetworkMonitor.checkNetworkConnection(context)) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_ADD_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");
                                String message = jsonObject.getString("message");
                                progressBar.setVisibility(View.GONE);
                                if (error) {
                                    //Registring FAILED
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    //Registering OK
                                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                                    int serverUserId = jsonObject.getInt("server_user_id");
                                    userToRegister.setServerUserId(serverUserId);
                                    userToRegister.setLastLoggedIn(DataBaseHelper.IS_LOGGED_IN);
                                    UsersDataManager usersDataManager = new UsersDataManager(context);
                                    if (usersDataManager.addUser(userToRegister) == Constants.SUCCESS) {
                                        Toast.makeText(context, R.string.user_registred_ok, Toast.LENGTH_SHORT).show();
                                        usersDataManager.updateLastLoggedIn(serverUserId);
                                        addDefaultItems(jsonObject);
                                        finish();
                                        startActivity(new Intent(context, MainActivity.class));
                                    }
                                    usersDataManager.closeDB();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Registering FAILED
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("email", "" + userToRegister.getEmail());
                    postData.put("password", "" + userToRegister.getPassword());
                    postData.put("user_name", "" + userToRegister.getUserName());
                    postData.put("sign_up_type", "" + userToRegister.getSignUpType());
                    return postData;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

        } else {
            // Network Problem
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void addDefaultItems(JSONObject jsonObject){

        UsersDataManager usersDataManager = new UsersDataManager(this);
        User currentUser = usersDataManager.getCurrentUser();
        usersDataManager.closeDB();

        try {
            JSONArray categoriesJSONArray = jsonObject.getJSONArray("default_categories");

            for (int i = 0; i < categoriesJSONArray.length(); i++) {
                //insert category
                JSONObject JSONCategory = categoriesJSONArray.getJSONObject(i);

                String categoryName = JSONCategory.getString("category_name");
                int crudStatus = JSONCategory.getInt("crud_status");
                int sync = DataBaseHelper.SYNC_STATUS_FAILED;
                String email = currentUser.getEmail();
                int userId = currentUser.getUserId();
                int serverCategoryId = 0;
                Icon icon = new Icon(this);
                int color = Color.getRandomColor(this);

                int iconId = icon.getIcon(crudStatus);

                Category category = new Category(categoryName, color, iconId, sync, userId, email, serverCategoryId);
                category.setCrudStatus(crudStatus);

                DataManager dataManager = new DataManager(this);
                dataManager.addCategory(this, category);
                dataManager.closeDB();

            }

            JSONArray goodsJSONArray = jsonObject.getJSONArray("default_goods");

            for (int i = 0; i < goodsJSONArray.length(); i++) {
                //insert good
                JSONObject JsonGood = goodsJSONArray.getJSONObject(i);

                String goodName = JsonGood.getString("good_name");
                int quantityLevelId = JsonGood.getInt("quantity_level");
                int isToBuyInt = JsonGood.getInt("is_to_buy");
                int crudStatus = JsonGood.getInt("crud_status");
                boolean isToBuy = (isToBuyInt == 1);
                String goodDesc = "";
                int sync = DataBaseHelper.SYNC_STATUS_FAILED;
                String email = currentUser.getEmail();
                int serverGoodId = 0;
                int serverCategoryId = 0;

                CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(this);
                Category category = categoriesDataProvider.getCategoryByCrud(crudStatus);
                categoriesDataProvider.closeDB();

                int categoryId = category.getCategoryId();

                Good good = new Good(goodName, categoryId, quantityLevelId, isToBuy, sync, email, serverGoodId, serverCategoryId);
                good.setCrudStatus(0);
                good.setGoodDesc(goodDesc);

                DataManager dataManager = new DataManager(this);
                dataManager.addGood(good);

                category.setCrudStatus(0);

                dataManager.updateCategory(category);
                dataManager.closeDB();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newDB.close();
    }
}
