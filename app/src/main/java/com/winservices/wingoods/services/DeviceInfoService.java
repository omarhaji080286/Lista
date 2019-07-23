package com.winservices.wingoods.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.RequestHandler;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.models.Device;
import com.winservices.wingoods.utils.NetworkMonitor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeviceInfoService implements Runnable {

    private final static String TAG = DeviceInfoService.class.getSimpleName();
    private Device device;
    private Context context;

    public DeviceInfoService(Context context) {
        this.device = new Device();
        this.context = context;
    }

    private void sendDeviceServiceToServer() {
        if (NetworkMonitor.checkNetworkConnection(context)) {

            UsersDataManager usersDataManager = new UsersDataManager(context);
            device.setDeviceUser(usersDataManager.getCurrentUser());

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    DataBaseHelper.HOST_URL_STORE_DEVICE_INFOS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");
                                String message = jsonObject.getString("message");
                                if (error) {
                                    //error in server
                                    Log.d(TAG, message);
                                } else {
                                    Log.d(TAG, message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, error.getMessage());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postData = new HashMap<>();
                    postData.put("jsonData", device.toJSONObject().toString());
                    return postData;
                }
            };

            RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        } else {
            Log.d(TAG, context.getResources().getString(R.string.network_error));
        }
    }

    @Override
    public void run() {
        sendDeviceServiceToServer();
    }
}
