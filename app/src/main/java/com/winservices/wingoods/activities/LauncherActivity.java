package com.winservices.wingoods.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.fragments.SignUpFragment;
import com.winservices.wingoods.fragments.WelcomeFragment;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.UtilsFunctions;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getSimpleName();
    private String currentFragTag = "none";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if(extras!=null){
            String data_value_1 = extras.getString(Constants.FCM_DATA_KEY_1);
             if (data_value_1 != null && data_value_1.equals(Constants.FCM_NOTIFICATION_UPDATE)) {
                this.finish();
                showAppOnGooglePlay();
            } else {
                launchApp();
            }
        } else {
            launchApp();
        }

    }

    private void showAppOnGooglePlay() {

        final String appPackageName = getPackageName();
        Intent intent;

        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));;
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            e.printStackTrace();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            launchApp();
        }

    }

    private void launchApp() {
        setContentView(R.layout.activity_launcher);

        UsersDataManager usersDataManager = new UsersDataManager(this);
        User currentUser = usersDataManager.getCurrentUser();

        if (currentUser == null) {
            displayFragment(new SignUpFragment(), SignUpFragment.TAG);
        } else {
            displayFragment(new WelcomeFragment(), WelcomeFragment.TAG);
        }
    }


    public void displayFragment(Fragment fragment, String tag) {
        if (currentFragTag.equals(tag)) return;
        currentFragTag = tag;
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.frameLauncherActivity, fragment, tag)
                .commit();
    }

    @Override
    protected void onResume() {
        SyncHelper.sync(this);
        super.onResume();
    }
}
