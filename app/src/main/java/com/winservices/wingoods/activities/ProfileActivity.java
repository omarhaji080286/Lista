package com.winservices.wingoods.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.SelectCityAdapter;
import com.winservices.wingoods.dbhelpers.CitiesDataManager;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.PermissionUtil;
import com.winservices.wingoods.utils.SharedPrefManager;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.winservices.wingoods.utils.PermissionUtil.TXT_CAMERA;

public class ProfileActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = ProfileActivity.class.getSimpleName();

    private ImageView imgProfile;
    private String currentImagePath;
    private int serverUserId;
    private ArrayList<City> cities = new ArrayList<>();
    private RecyclerView rvCities;
    private ProgressBar progressBar;
    public AlertDialog dialog;
    public TextView txtCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle(getString(R.string.my_profile));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView txtPhone = findViewById(R.id.txtPhone);
        EditText editUserName = findViewById(R.id.editUserName);
        txtCityName = findViewById(R.id.txtCityName);
        ImageView imgEditCity =  findViewById(R.id.imgEditCity);
        imgProfile = findViewById(R.id.imgProfile);

        imgEditCity.setOnClickListener(view -> {
            @SuppressLint("InflateParams") View mView = LayoutInflater.from(ProfileActivity.this)
                    .inflate(R.layout.fragment_select_city, null, false);

            rvCities = mView.findViewById(R.id.rvCities);
            progressBar = mView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            getCitiesFromServer();

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
            mBuilder.setView(mView);
            dialog = mBuilder.create();
            dialog.show();

        });

        UsersDataManager usersDataManager = new UsersDataManager(this);
        final User currentUser = usersDataManager.getCurrentUser();

        City city = currentUser.getCity(this);

        serverUserId = currentUser.getServerUserId();

        txtCityName.setText(city.getCityName());

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
                imgProfile.setImageResource(R.drawable.profile);
            }
        } else {
            imgProfile.setImageResource(R.drawable.profile);
        }

    }

    private void initBtnChangePic() {

        imgProfile.setOnClickListener(view -> {
            PermissionUtil permissionUtil = new PermissionUtil(getApplicationContext());
            if (permissionUtil.checkPermission(TXT_CAMERA) == PackageManager.PERMISSION_GRANTED) {
                captureImage();
                return;
            }
            permissionUtil.requestPermission(TXT_CAMERA, ProfileActivity.this);
        });
    }


    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
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
        thread.start();
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
                                //String message = jsonObject.getString("message");
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
        if (id == android.R.id.home) {
            Intent intent = new Intent(ProfileActivity.this, LauncherActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCitiesFromServer(){
        if (NetworkMonitor.checkNetworkConnection(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_GET_CITIES,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray JSONCities = jsonObject.getJSONArray("cities");

                                cities.clear();
                                for (int i = 0; i < JSONCities.length(); i++) {
                                    JSONObject JSONCity = JSONCities.getJSONObject(i);

                                    int serverCountryId = JSONCity.getInt("server_country_id");
                                    String countryName = JSONCity.getString("country_name");
                                    Country country = new Country(serverCountryId, countryName);

                                    int serverCityId = JSONCity.getInt("server_city_id");
                                    String cityName = JSONCity.getString("city_name");
                                    City city = new City(serverCityId, cityName, country);

                                    cities.add(city);
                                }

                                SelectCityAdapter adapter = new SelectCityAdapter(ProfileActivity.this, true);
                                adapter.setCities(cities);

                                LinearLayoutManager llm = new LinearLayoutManager(ProfileActivity.this);
                                rvCities.setLayoutManager(llm);
                                rvCities.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    },
                    error -> progressBar.setVisibility(View.GONE)
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", "" + "nothing");
                    postData.put("language", "" + Locale.getDefault().getLanguage());
                    return postData;
                }
            };

            RequestHandler.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(ProfileActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }



}
