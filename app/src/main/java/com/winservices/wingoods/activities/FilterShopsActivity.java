package com.winservices.wingoods.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.adapters.CitiesAdapter;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.models.City;
import com.winservices.wingoods.models.Country;
import com.winservices.wingoods.models.ShopsFilter;
import com.winservices.wingoods.utils.UtilsFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterShopsActivity extends AppCompatActivity {

    private static final String TAG = "FilterShopsActivity";
    private RelativeLayout rlCity;
    private ImageView arrowCity;
    private RecyclerView rvCities;
    private CitiesAdapter citiesAdapter;
    private Dialog dialog;
    private boolean sectionCitiesIsExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_shops);
        setTitle(getString(R.string.filters));
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rlCity = findViewById(R.id.rl_city);
        arrowCity = findViewById(R.id.arrow_city);
        rvCities = findViewById(R.id.rv_cities);
        Button btShowResult = findViewById(R.id.bt_showResult);
        Button btResetFilters = findViewById(R.id.bt_reSetFilters);

        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), this, R.string.loading).create();
        dialog.show();

        loadCitiesFromServer(this);

        btShowResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopsFilter shopsFilter = new ShopsFilter();
                ArrayList<City> selectedCities = citiesAdapter.getSelectedCities();

                if (selectedCities.size()>0){
                    shopsFilter.setSelectedCities(selectedCities);
                    shopsFilter.setFilterState(ShopsFilter.ENABLE);
                    Intent shopsReturnIntent = new Intent();
                    shopsReturnIntent.putExtra("filter", shopsFilter);
                    setResult(Activity.RESULT_OK,shopsReturnIntent);
                    finish();
                } else {
                    Toast.makeText(FilterShopsActivity.this, R.string.select_city, Toast.LENGTH_SHORT).show();
                }

            }
        });

        btResetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopsFilter shopsFilter = new ShopsFilter();
                shopsFilter.setFilterState(ShopsFilter.DISABLE);
                Intent shopsReturnIntent = new Intent();
                shopsReturnIntent.putExtra("filter", shopsFilter);
                setResult(Activity.RESULT_OK,shopsReturnIntent);
                finish();
                Toast.makeText(FilterShopsActivity.this, R.string.filters_removed, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void prepareCitiesSection(ArrayList<City> cities){

        //Section "CITY"
        //Parent
        rlCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sectionCitiesIsExpanded){
                    UtilsFunctions.collapse(rvCities);
                    arrowCity.setImageResource(R.drawable.ic_arrow_down_black);
                    sectionCitiesIsExpanded = false;
                } else {
                    UtilsFunctions.expand(rvCities);
                    arrowCity.setImageResource(R.drawable.ic_arrow_up_black);
                    sectionCitiesIsExpanded = true;
                }

            }
        });

        //Child
        citiesAdapter = new CitiesAdapter(cities,this);
        ShopsFilter shopsFilterFromIntent = (ShopsFilter) getIntent().getSerializableExtra("shopsFilter");
        if (shopsFilterFromIntent.isEnable()){
            citiesAdapter.setSelectedCities(shopsFilterFromIntent.getSelectedCities());
        }
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false );
        rvCities.setLayoutManager(llm);
        rvCities.setAdapter(citiesAdapter);

    }


    private void loadCitiesFromServer(final Context context) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DataBaseHelper.HOST_URL_GET_CITIES,
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
                                JSONArray JSONCities = jsonObject.getJSONArray("cities");
                                ArrayList<City> cities = new ArrayList<>();

                                for (int i = 0; i < JSONCities.length(); i++) {
                                    JSONObject JSONCity = JSONCities.getJSONObject(i);

                                    int serverCountryId = JSONCity.getInt("server_country_id");
                                    String countryName = JSONCity.getString("country_name");
                                    Country country = new Country(serverCountryId, countryName);

                                    int serverCityId = JSONCity.getInt("server_city_id");
                                    String cityName = JSONCity.getString("city_name");
                                    City city = new City(serverCityId,cityName, country );

                                    cities.add(city);

                                }

                                dialog.dismiss();

                                prepareCitiesSection(cities);

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
                postData.put("jsonData", "" );
                return postData;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
