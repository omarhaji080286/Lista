package com.winservices.wingoods.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.PermissionUtil;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.winservices.wingoods.utils.PermissionUtil.TXT_CAMERA;

public class ProfileActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = ProfileActivity.class.getSimpleName();

    private TextView txtPhone;
    private EditText editUserName;
    private ImageView imgProfile;
    private String currentImagePath;
    private int serverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(getString(R.string.my_profile));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtPhone = findViewById(R.id.txtPhone);
        editUserName = findViewById(R.id.editUserName);
        imgProfile = findViewById(R.id.imgProfile);

        UsersDataManager usersDataManager = new UsersDataManager(this);
        final User currentUser = usersDataManager.getCurrentUser();
        serverUserId = currentUser.getServerUserId();

        txtPhone.setText(currentUser.getUserPhone());
        if (currentUser.getUserName().equals("null")) currentUser.setUserName("");
        editUserName.setText(currentUser.getUserName());

        loadUserImage(currentUser.getServerUserId());

        initBtnChangePic();

        editUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    currentUser.setUserName(charSequence.toString());
                    UsersDataManager.UpdateUser updateUser = new UsersDataManager.UpdateUser(getApplicationContext(), currentUser);
                    Thread t = new Thread(updateUser);
                    t.start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void loadUserImage(int serverUserId) {

        String userImgPath = SharedPrefManager.getInstance(this).getUserImagePath(serverUserId);
        if (userImgPath != null) {
            Bitmap imageBitmap = UtilsFunctions.getOrientedBitmap(userImgPath);
            if (imageBitmap != null) {
                imgProfile.setImageBitmap(imageBitmap);
            } else {
                imgProfile.setImageResource(R.drawable.users);
            }
        } else {
            imgProfile.setImageResource(R.drawable.users);
        }

    }

    private void initBtnChangePic() {

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtil permissionUtil = new PermissionUtil(getApplicationContext());
                if (permissionUtil.checkPermission(TXT_CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                    return;
                }
                permissionUtil.requestPermission(TXT_CAMERA, ProfileActivity.this);
            }
        });
    }


    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(Objects.requireNonNull(this).getPackageManager()) != null) {
            File imageFile = null;

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.winservices.wingoods", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File getImageFile() throws IOException {
        String imageName = User.PREFIX_USER + serverUserId;
        String file_path = getFilesDir().getPath() + "/jpg";
        File storageDir = new File(file_path);
        if (!storageDir.exists()) storageDir.mkdirs();
        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bitmap imageBitmap = BitmapFactory.decodeFile(currentImagePath);
            Bitmap imageBitmap = UtilsFunctions.getOrientedBitmap(currentImagePath);
            imgProfile.setImageBitmap(imageBitmap);
            storeAndUploadImage();
        }
    }

    private void storeAndUploadImage() {
        Thread thread = new Thread() {
            public void run() {
                SharedPrefManager.getInstance(getApplicationContext()).storeUserImagePath(serverUserId, currentImagePath);
                uploadUserImage(serverUserId, getApplicationContext());
            }
        };
        thread.run();
    }

    private void uploadUserImage(final int serverUserId, final Context context) {
        if (NetworkMonitor.checkNetworkConnection(context)) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_UPLOAD_USER_IMAGE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");
                                String message = jsonObject.getString("message");
                                if (error) {
                                    //error in server
                                    Log.d(TAG, "Error on upload user image");
                                } else {
                                    Log.d(TAG, "upload user image ok : ");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Error on upload user image : " + error);
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {

                    String imagePath = SharedPrefManager.getInstance(context).getUserImagePath(serverUserId);
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                    String imageString = UtilsFunctions.imageToString(imageBitmap, 30);

                    Map<String, String> postData = new HashMap<>();
                    postData.put("server_user_id", String.valueOf(serverUserId));
                    postData.put("image_string", imageString);
                    postData.put("language", Locale.getDefault().getLanguage());
                    return postData;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
