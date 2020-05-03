package com.winservices.wingoods.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.SelectCityAdapter;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;
import com.winservices.wingoods.utils.NetworkMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class SelectCityFragment extends Fragment {

    public final static String TAG = SelectCityFragment.class.getSimpleName();
    private ArrayList<City> cities = new ArrayList<>();
    private RecyclerView rvCities;
    private ProgressBar progressBar;

    public SelectCityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_city, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCities = view.findViewById(R.id.rvCities);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        getCitiesFromServer();

    }


    private void getCitiesFromServer(){
        if (NetworkMonitor.checkNetworkConnection(Objects.requireNonNull(getContext()))) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_GET_CITIES,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String message = jsonObject.getString("message");
                            if (error) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray JSONCities = jsonObject.getJSONArray("cities");

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

                                SelectCityAdapter adapter = new SelectCityAdapter(getContext(), false);
                                adapter.setCities(cities);

                                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                                rvCities.setLayoutManager(llm);
                                rvCities.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
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

            RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }



}
