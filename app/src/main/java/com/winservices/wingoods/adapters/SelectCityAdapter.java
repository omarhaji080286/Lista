package com.winservices.wingoods.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.LauncherActivity;
import com.winservices.wingoods.activities.ProfileActivity;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.fragments.WelcomeFragment;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.NetworkMonitor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.winservices.wingoods.dbhelpers.DataBaseHelper.APP_IMG_URL;

public class SelectCityAdapter extends RecyclerView.Adapter<SelectCityAdapter.CityVH> {

    public final static String TAG = SelectCityAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<City> cities;
    private boolean isForUpdate;
    private Bitmap icon;

    public SelectCityAdapter(Context context, boolean isForUpdate) {
        this.context = context;
        this.cities = new ArrayList<>();
        this.isForUpdate = isForUpdate;
    }

    @NonNull
    @Override
    public CityVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city_to_select, parent, false);

        return new CityVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityVH holder, int position) {

        City city = this.cities.get(position);

        holder.cityName.setText(city.getCityName());

        String imgUrl = APP_IMG_URL + city.getServerCityId()+".jpg";
        Picasso.get().load(imgUrl).into(holder.imgCity);

        holder.llCity.setOnClickListener(view -> {
            UsersDataManager usersDataManager = new UsersDataManager(context);
            User user = usersDataManager.getCurrentUser();
            updateUserCity(user, city.getServerCityId(), city.getCityName());
        });

    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    private void updateUserCity(User user, int serverCityId, String cityName) {
        if (NetworkMonitor.checkNetworkConnection(context)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_UPDATE_CITY,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");
                        String message = jsonObject.getString("message");
                        if (error) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {

                            user.setServerCityId(serverCityId);
                            UsersDataManager usersDataManager = new UsersDataManager(context);
                            usersDataManager.updateUser(user);

                            if (isForUpdate){
                                ProfileActivity profileActivity = (ProfileActivity) context;
                                profileActivity.dialog.dismiss();
                                profileActivity.txtCityName.setText(cityName);
                            } else {
                                Toast.makeText(context, R.string.welcome_msg, Toast.LENGTH_LONG).show();
                                LauncherActivity launcherActivity = (LauncherActivity) context;
                                launcherActivity.displayFragment(new WelcomeFragment(), WelcomeFragment.TAG);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Log.d(TAG, context.getString(R.string.error))
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", "" + getJSONForUpdateUserCity(user.getServerUserId(), serverCityId));
                    postData.put("language", "" + Locale.getDefault().getLanguage());
                    return postData;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    private String getJSONForUpdateUserCity(int serverUserId, int serverCityId) {
        final JSONObject root = new JSONObject();
        try {

            root.put("serverUserId", serverUserId);
            root.put("serverCityId", serverCityId);

            return root.toString(1);

        } catch (JSONException e) {
            Log.d(TAG, "Can't format JSON");
        }

        return null;
    }

    public void setCities(ArrayList<City> cities){
        this.cities.clear();
        this.cities.addAll(cities);
        notifyDataSetChanged();
    }


    static class CityVH extends RecyclerView.ViewHolder {

        TextView cityName;
        ImageView imgCity;
        LinearLayoutCompat llCity;

        CityVH(View itemView) {
            super(itemView);

            cityName = itemView.findViewById(R.id.cityName);
            imgCity = itemView.findViewById(R.id.imgCity);
            llCity = itemView.findViewById(R.id.llCity);
        }
    }

}
