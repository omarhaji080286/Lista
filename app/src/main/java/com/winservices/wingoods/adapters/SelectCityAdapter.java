package com.winservices.wingoods.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.winservices.wingoods.R;
import com.winservices.wingoods.activities.LauncherActivity;
import com.winservices.wingoods.activities.MainActivity;
import com.winservices.wingoods.activities.OrderActivity;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.fragments.WelcomeFragment;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SelectCityAdapter extends RecyclerView.Adapter<SelectCityAdapter.CityVH> {

    public final static String TAG = SelectCityAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<City> cities;

    public SelectCityAdapter(Context context, ArrayList<City> cities) {
        this.context = context;
        this.cities = cities;
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
        holder.llCity.setOnClickListener(view -> {
            UsersDataManager usersDataManager = new UsersDataManager(context);
            User user = usersDataManager.getCurrentUser();
            updateUserCity(user, city.getServerCityId());
        });

    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    private void updateUserCity(User user, int serverCityId) {
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
                            Toast.makeText(context, R.string.welcome_msg, Toast.LENGTH_LONG).show();
                            user.setServerCityId(serverCityId);
                            UsersDataManager usersDataManager = new UsersDataManager(context);
                            usersDataManager.updateUser(user);

                            LauncherActivity launcherActivity = (LauncherActivity) context;
                            launcherActivity.displayFragment(new WelcomeFragment(), WelcomeFragment.TAG);

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
