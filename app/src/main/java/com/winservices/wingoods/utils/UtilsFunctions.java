package com.winservices.wingoods.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class UtilsFunctions {

    private final static int REQUEST_LOCATION = 199;

    public static final String TAG = UtilsFunctions.class.getSimpleName();

    //Spinner loader
    public static SimpleCursorAdapter getSpinnerAdapter(Context context){
        DataBaseHelper db = DataBaseHelper.getInstance(context);
        Cursor categories = db.getAllCategories();

        String[] from = new String[]{DataBaseHelper.COL_CATEGORY_NAME};
        // create an array of the display item we want to bind our data to
        int[] to = new int[]{android.R.id.text1};
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_spinner_item,
                categories, from, to, 0);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return mAdapter;
    }

    public static void hideKeyboard(Context context, View view) {

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static AlertDialog.Builder getDialogBuilder(LayoutInflater layoutInflater, Context context, int msgId){

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        View view = layoutInflater.inflate(R.layout.progress, null);
        TextView msg = view.findViewById(R.id.txt_msg_progress);
        msg.setText(context.getResources().getString(msgId));
        builder.setView(view);
        builder.setCancelable(false);
        return builder;
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static String dateToString(Date date, String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.FRANCE);
        return dateFormat.format(date);
    }

    public static Date stringToDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        Date date = new Date();
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Bitmap stringToBitmap(Context context, String encodedImage) {

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap!=null){
            return bitmap;
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_shop_image);
        }


    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap getPNG(String PNGPath){

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(PNGPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public static Bitmap getOrientedBitmap(String photoPath) {
        Log.d(TAG, "getOrientedBitmap: " + photoPath);
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap rotatedBitmap = null;
        if (ei != null) {
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        }
        return rotatedBitmap;
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static String formatPhone(String phone){
        phone = phone.replace("-", "");
        phone = phone.replace(" ", "");


        switch (phone.length()){
            case 10 :
                if (phone.substring(0,1).equals("0")){
                    phone = "+212"+phone.substring(1);
                }
                break;
            case 14 :
                if (phone.substring(0,5).equals("00212")){
                    phone = "+212"+phone.substring(5);
                }
                break;
        }

        return phone;
    }

    public static String imageToString(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    public static String to2digits(int number) {
        if (number >= 0 && number < 10) {
            return "0" + String.valueOf(number);
        }
        return String.valueOf(number);
    }

    public static String getDayOfWeek(Context context,int value) {
        String day = "";
        switch (value) {
            case 1:
                day = context.getResources().getString(R.string.sunday);
                break;
            case 2:
                day = context.getResources().getString(R.string.monday);
                break;
            case 3:
                day = context.getResources().getString(R.string.tuesday);
                break;
            case 4:
                day = context.getResources().getString(R.string.wednesday);
                break;
            case 5:
                day = context.getResources().getString(R.string.thursday);
                break;
            case 6:
                day = context.getResources().getString(R.string.friday);
                break;
            case 7:
                day = context.getResources().getString(R.string.saturday);
                break;
        }
        return day;
    }

    public static Bitmap loadImageFromUrl(String url) {

        Bitmap bm;
        try {

            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();

            conn.connect();
            InputStream is = null;
            try
            {
                is= conn.getInputStream();
            }catch(IOException e)
            {
                return null;
            }

            BufferedInputStream bis = new BufferedInputStream(is);

            bm = BitmapFactory.decodeStream(bis);

            bis.close();
            is.close();

        } catch (IOException e) {
            return null;
        }

        return  Bitmap.createScaledBitmap(bm,100,100,true);

    }

    public static void storeImageToFile(final Context context, final Bitmap shopImg, final int serverShopId) {
        final String file_path = context.getFilesDir().getPath() + "/jpg";
        Thread thread = new Thread() {
            public void run() {
                File dir = new File(file_path);
                if (!dir.exists()) {
                    if (dir.mkdirs()) {
                        Log.d(TAG, "Files created");
                    }
                }
                File file = new File(dir, "lista_pro_shop_" + serverShopId + ".jpg");
                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(file);
                    shopImg.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SharedPrefManager.getInstance(context).storeShopImagePath(serverShopId, file.getAbsolutePath());
            }
        };
        thread.run();

    }

    public static boolean isGPSEnabled(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static void enableGPS(final Activity activity, final Intent intent) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.e("location", "Connect");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e("location", "fail");
                        //googleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("location", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                activity.startActivity(intent);
                activity.finish();
                Log.d("location_enable", "enable");
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    //UtilsFunctions.displayPromptForEnablingGPS(activity);
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_LOCATION);
                        Log.d(TAG, "error enabling GPS : " + e.toString() );
                   } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    public static void displayPromptForEnablingGPS(final Context context)
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Merci d'activer la localisation";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                context.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
