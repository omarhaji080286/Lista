package com.winservices.wingoods.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.LauncherActivity;
import com.winservices.wingoods.dbhelpers.CategoriesDataProvider;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.DefaultCategory;
import com.winservices.wingoods.models.Good;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Color;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    public static final String TAG = SignUpFragment.class.getSimpleName();

    private Dialog dialog;
    private FirebaseAuth firebaseAuth;
    private String codeSent;
    private EditText editPhone, editVerifCode;
    private Button btnContinue, btnSignUp;
    private TextView txtDescription;
    private LinearLayout linlayPhoneCointaner;


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), "Phone Authentication failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        editPhone = view.findViewById(R.id.editPhone);
        btnContinue = view.findViewById(R.id.btnContinue);
        editVerifCode = view.findViewById(R.id.editVerifCode);
        btnSignUp = view.findViewById(R.id.btnNext);
        txtDescription = view.findViewById(R.id.txtDescription);
        linlayPhoneCointaner = view.findViewById(R.id.linLayPhoneContainer);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkMonitor.checkNetworkConnection(Objects.requireNonNull(getContext()))) {

                    final String phone = editPhone.getText().toString();
                    String completePhone = "+212" + phone;
                    if (UtilsFunctions.isEmulator()) {
                        completePhone = "+16505551111"; //whitelist number on Firebase, 123456 is the code to number
                    }

                    if (isPhoneValid(phone)) {

                        sendVerifCode(completePhone);

                        btnContinue.setVisibility(View.GONE);
                        linlayPhoneCointaner.setVisibility(View.GONE);
                        btnSignUp.setVisibility(View.VISIBLE);
                        editVerifCode.setVisibility(View.VISIBLE);

                        StringBuilder sb = new StringBuilder();
                        sb.append(getString(R.string.sms_sent_to));
                        sb.append(completePhone);
                        sb.append(getString(R.string.with_code));

                        txtDescription.setText(sb);

                    }

                } else {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkMonitor.checkNetworkConnection(Objects.requireNonNull(getContext()))) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Objects.requireNonNull(getActivity()), new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();
                            SharedPrefManager.getInstance(getContext()).storeToken(newToken);
                            Log.d(TAG, "Token: " + newToken);
                        }
                    });
                    verifySignUpCode();
                } else {
                    Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean isPhoneValid(String phone) {

        if (phone.isEmpty()) {
            editPhone.setError(getString(R.string.phone_required));
            editPhone.requestFocus();
            return false;
        }

        if (phone.length() < 9) {
            editPhone.setError(getString(R.string.not_valid_phone));
            editPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void sendVerifCode(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        Log.d(TAG, "request code verification for : " + phone);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();

                                    Log.d(TAG, "signIn success - phone number : " + user.getPhoneNumber());
                                    Log.d(TAG, "signIn success - display name : " + user.getDisplayName());

                                    String fcmToken = SharedPrefManager.getInstance(getContext()).getToken();

                                    String phone = "+212" + editPhone.getText().toString();
                                    User userToRegister = new User(phone);

                                    userToRegister.setFcmToken(fcmToken);
                                    userToRegister.setSignUpType(DataBaseHelper.LISTA);

                                    //TODO - must adjust
                                    userToRegister.setEmail(phone + "@gmail.com");

                                    registerUserToAppServer(getContext(), userToRegister);

                                } else {
                                    Log.d(TAG, "signIn : failure");
                                    editVerifCode.setError(getString(R.string.not_valid_code));
                                    editVerifCode.requestFocus();
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    }
                                }
                            }
                        }
                );
    }


    public void registerUserToAppServer(final Context context, final User userToRegister) {

        if (NetworkMonitor.checkNetworkConnection(context)) {

            dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), getContext(), R.string.signing_up).create();
            dialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_REGISTER_USER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");
                                String message = jsonObject.getString("message");
                                dialog.dismiss();
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
                                        usersDataManager.updateLastLoggedIn(serverUserId);
                                        addDefaultItems(jsonObject);
                                        Toast.makeText(getContext(), R.string.welcome_msg, Toast.LENGTH_SHORT).show();
                                        LauncherActivity launcherActivity = (LauncherActivity) getActivity();
                                        Objects.requireNonNull(launcherActivity).displayFragment(new WelcomeFragment(), WelcomeFragment.TAG);
                                    }
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
                            dialog.dismiss();
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
                    postData.put("fcm_token", "" + userToRegister.getFcmToken());
                    postData.put("user_phone", "" + userToRegister.getUserPhone());
                    return postData;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);

        } else {
            // Network Problem
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    private void addDefaultItems(JSONObject jsonObject) {

        UsersDataManager usersDataManager = new UsersDataManager(getContext());
        User currentUser = usersDataManager.getCurrentUser();

        try {
            JSONArray categoriesJSONArray = jsonObject.getJSONArray("default_categories");

            for (int i = 0; i < categoriesJSONArray.length(); i++) {
                //insert category
                JSONObject JSONCategory = categoriesJSONArray.getJSONObject(i);

                String categoryName = JSONCategory.getString("category_name");
                int crudStatus = JSONCategory.getInt("crud_status");
                int dCategoryId = JSONCategory.getInt("d_category_id");
                int sync = DataBaseHelper.SYNC_STATUS_FAILED;
                String email = currentUser.getEmail();
                int userId = currentUser.getUserId();
                int serverCategoryId = 0;
                int color = Color.getRandomColor(getContext());

                String dCategoryImage = JSONCategory.getString("d_category_image");
                SharedPrefManager.getInstance(getContext()).storeImageToFile(dCategoryImage, "png", DefaultCategory.PREFIX_D_CATEGORY, dCategoryId);

                Category category = new Category(categoryName, color, 0, sync, userId, email, serverCategoryId);
                category.setCrudStatus(crudStatus);
                category.setDCategoryID(dCategoryId);

                DataManager dataManager = new DataManager(getContext());
                dataManager.addCategory(getContext(), category);

            }

            JSONArray goodsJSONArray = jsonObject.getJSONArray("default_goods");

            for (int i = 0; i < goodsJSONArray.length(); i++) {
                //insert good
                JSONObject jsonGood = goodsJSONArray.getJSONObject(i);

                String goodName = jsonGood.getString("good_name");
                int quantityLevelId = jsonGood.getInt("quantity_level");
                int isToBuyInt = jsonGood.getInt("is_to_buy");
                int crudStatus = jsonGood.getInt("crud_status");
                boolean isToBuy = (isToBuyInt == 1);
                String goodDesc = jsonGood.getString("good_desc");
                int sync = DataBaseHelper.SYNC_STATUS_FAILED;
                String email = currentUser.getEmail();
                int serverGoodId = 0;
                int serverCategoryId = 0;
                int isOrdered = 0;

                CategoriesDataProvider categoriesDataProvider = new CategoriesDataProvider(getContext());
                Category category = categoriesDataProvider.getCategoryByCrud(crudStatus);

                int categoryId = category.getCategoryId();

                Good good = new Good(goodName, categoryId, quantityLevelId, isToBuy, sync, email, serverGoodId, serverCategoryId);
                good.setCrudStatus(0);
                good.setGoodDesc(goodDesc);
                good.setIsOrdered(isOrdered);

                DataManager dataManager = new DataManager(getContext());
                dataManager.addGood(good);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void verifySignUpCode() {
        String codeEntered = editVerifCode.getText().toString();
        if (UtilsFunctions.isEmulator()) {
            codeEntered = "123456"; // 123456 is the code to number
        }
        if (isCodeEnteredValid(codeEntered)) {
            if (codeSent != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, codeEntered);
                signInWithPhoneAuthCredential(credential);
            } else {
                editVerifCode.setError(getString(R.string.not_valid_code));
                editVerifCode.requestFocus();
            }
        }
    }

    private boolean isCodeEnteredValid(String codeEntered) {
        if (codeEntered.isEmpty()) {
            editVerifCode.setError(getString(R.string.code_required));
            editVerifCode.requestFocus();
            return false;
        }
        if (codeEntered.length() < 6) {
            editVerifCode.setError(getString(R.string.not_valid_code));
            editVerifCode.requestFocus();
            return false;
        }
        return true;
    }


}
