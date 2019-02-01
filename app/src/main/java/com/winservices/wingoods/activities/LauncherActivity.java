package com.winservices.wingoods.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.fragments.SignUpFragment;
import com.winservices.wingoods.fragments.WelcomeFragment;
import com.winservices.wingoods.models.User;

import java.util.Objects;

public class LauncherActivity extends AppCompatActivity {

    private String currentFragTag = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
