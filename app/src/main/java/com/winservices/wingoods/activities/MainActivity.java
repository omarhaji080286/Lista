package com.winservices.wingoods.activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.winservices.wingoods.R;
import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.dbhelpers.DataManager;
import com.winservices.wingoods.dbhelpers.GoodsDataProvider;
import com.winservices.wingoods.dbhelpers.SyncHelper;
import com.winservices.wingoods.dbhelpers.Synchronizer;
import com.winservices.wingoods.dbhelpers.UsersDataManager;
import com.winservices.wingoods.fragments.MyGoods;
import com.winservices.wingoods.models.Category;
import com.winservices.wingoods.models.User;
import com.winservices.wingoods.sync.ListaSyncAdapter;
import com.winservices.wingoods.utils.Color;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.Controlers;
import com.winservices.wingoods.utils.MJobScheduler;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "MainActivity";

    public static int FRAGMENT_REQUEST_CODE = 1;
    FloatingActionButton fabPlus, fabAddCategory, fabWtsp;
    Animation fabOpen, fabClose, fabRotateClockWise, fabAntiClockWise;
    TextView addCategoriesLabel, txtSend, txtUserEmail;
    boolean isOpen = false;
    NavigationView navigationView;
    FrameLayout mInterceptorFrame;
    int fragmentId = R.id.nav_my_goods;
    MyGoods myGoodsFragment;
    private SyncReceiver syncReceiver;
    private boolean syncTriggeredByUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        syncReceiver = new SyncReceiver();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mInterceptorFrame = findViewById(R.id.fl_interceptor);
        mInterceptorFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isOpen) {
                    collapseFab();
                    return true;
                }
                UtilsFunctions.hideKeyboard(getApplicationContext(), v);
                return false;
            }


        });

        fabPlus = findViewById(R.id.fab_plus);
        fabAddCategory = findViewById(R.id.fab_add_category);
        fabWtsp = findViewById(R.id.fab_wtsp);
        addCategoriesLabel = findViewById(R.id.txt_add_categories_label);
        txtSend = findViewById(R.id.txt_wtsp);
        txtUserEmail = navigationView.getHeaderView(0).findViewById(R.id.txt_user_email);

        UsersDataManager usersDataManager = new UsersDataManager(this);
        User user = usersDataManager.getCurrentUser();

        txtUserEmail.setText(user.getEmail());

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabAntiClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    collapseFab();
                } else {
                    expandFab();
                }
            }
        });

        final Context context = this;

        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = LayoutInflater.from(context)
                        .inflate(R.layout.fragment_add_category, mInterceptorFrame, false);

                Button btnAddCategory = mView.findViewById(R.id.btn_add_category);
                Button btnCancel = mView.findViewById(R.id.btn_cancel);
                final EditText editCategoryName = mView.findViewById(R.id.edit_category);

                //prepare widget

                //Show the dialog
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                editCategoryName.requestFocus();

                //Action Button "Cancel"
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        collapseFab();
                    }
                });

                //Action Button "Add"
                btnAddCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String categoryName = editCategoryName.getText().toString();
                        if (Controlers.inputOk(categoryName)) {
                            UsersDataManager usersDataManager = new UsersDataManager(context);
                            User currentUser = usersDataManager.getCurrentUser();

                            Category category = new Category(categoryName.trim(), Color.getRandomColor(context), R.drawable.others,
                                    DataBaseHelper.SYNC_STATUS_FAILED, currentUser.getUserId(), currentUser.getEmail());

                            DataManager dataManager = new DataManager(context);
                            int result = dataManager.addCategory(context, category);

                            switch (result) {
                                case Constants.SUCCESS:
                                    Toast.makeText(context, context.getResources().getString(R.string.category_add_success), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    collapseFab();
                                    displaySelectedScreen(fragmentId);
                                    break;
                                case Constants.DATAEXISTS:
                                    Toast.makeText(context, context.getResources().getString(R.string.category_exists), Toast.LENGTH_SHORT).show();
                                    break;
                                case Constants.ERROR:
                                    Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.input_txt_not_valid), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        fabWtsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoodsDataProvider goodsDataProvider = new GoodsDataProvider(getApplicationContext());
                String message = goodsDataProvider.getMessageToSend();
                openWhatsApp(message);
                collapseFab();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Begin preparation
        ComponentName componentName = new ComponentName(this, MJobScheduler.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.JOB_ID, componentName);

        builder.setPeriodic(Synchronizer.PERIOD);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);

        JobInfo jobInfo = builder.build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        //End Job preparation

        if (jobScheduler != null) {
            int resultCode = jobScheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS){
                Log.d(TAG, "Job scheduled");
            } else {
                Log.d(TAG, "Job scheduling failed");
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FRAGMENT_REQUEST_CODE) {
            fragmentId = data.getIntExtra(Constants.SELECTED_FRAGMENT, R.id.nav_my_goods);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Sync Adapter first call
        ListaSyncAdapter.initializeSyncAdapter(this);
        registerReceiver(syncReceiver, new IntentFilter(Constants.ACTION_REFRESH_AFTER_SYNC));
        displaySelectedScreen(fragmentId);
    }

    private void openWhatsApp(String message) {
        String wtspUri = "com.whatsapp";
        if (Controlers.whatsappInstalled(wtspUri, this)) {
            //String smsNumber = "212672401726"; //without '+'
            try {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                // sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net"); //phone number without "+" prefix
                sendIntent.setPackage(wtspUri);
                startActivity(sendIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.wtsp_not_installed), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //go to activity
        switch (id){
            case R.id.action_invite:
                startActivity(new Intent(this, InviteActivity.class));
                break;
            case R.id.action_receive_invitation:
                if (NetworkMonitor.checkNetworkConnection(this)){
                    startActivity(new Intent(this, ReceiveInvitationActivity.class));
                } else {
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync:
               if (NetworkMonitor.checkNetworkConnection(this)){
                   syncTriggeredByUser = true;
                   SyncHelper.sync(this);
               } else {
                   Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
               }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_my_goods:
                myGoodsFragment = new MyGoods();
                fragment = myGoodsFragment;
                fragmentId = R.id.nav_my_goods;
                navigationView.getMenu().getItem(0).setChecked(true);
                break;
            case R.id.nav_shops:
                navigationView.getMenu().getItem(1).setChecked(true);
                goToShops();
                break;
            case R.id.nav_my_orders:
                navigationView.getMenu().getItem(2).setChecked(true);
                goToOrders();
                break;
            case R.id.nav_log_out:
                logOut();
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    private void logOut(){


        //Log out from FaceBook
        LoginManager.getInstance().logOut();

        //Log out from FaceBook
        googleLogOut();

        UsersDataManager usersDataManager = new UsersDataManager(this);
        User user = usersDataManager.getCurrentUser();
        user.setLastLoggedIn(DataBaseHelper.IS_NOT_LOGGED_IN);
        usersDataManager.updateUser( user);

        finish();
        startActivity(new Intent(this, AuthActivity.class));
    }

    private void googleLogOut(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
    }

    // @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        int fragmentId = item.getItemId();
        displaySelectedScreen(fragmentId);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void expandFab() {
        fabAddCategory.startAnimation(fabOpen);
        addCategoriesLabel.startAnimation(fabOpen);
        fabWtsp.startAnimation(fabOpen);
        txtSend.startAnimation(fabOpen);
        fabPlus.startAnimation(fabRotateClockWise);
        fabAddCategory.setClickable(true);
        fabWtsp.setClickable(true);
        isOpen = true;
    }

    private void collapseFab() {
        fabAddCategory.startAnimation(fabClose);
        addCategoriesLabel.startAnimation(fabClose);
        fabWtsp.startAnimation(fabClose);
        txtSend.startAnimation(fabClose);
        fabPlus.startAnimation(fabAntiClockWise);
        fabAddCategory.setClickable(false);
        fabWtsp.setClickable(false);
        isOpen = false;
    }


    private void goToShops(){
        if (NetworkMonitor.checkNetworkConnection(this)){
            Intent intent = new Intent(MainActivity.this, ShopsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void goToOrders(){
        if (NetworkMonitor.checkNetworkConnection(this)){
            Intent intent = new Intent(MainActivity.this, MyOrdersActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    protected void onDestroy() {
        //clear the job
        super.onDestroy();

        //JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        //jobScheduler.cancel(Constants.JOB_ID);
        //Toast.makeText(this, "Job canceled...", Toast.LENGTH_SHORT).show();

        DataBaseHelper.closeDB();
    }



    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncReceiver);
    }

    public class SyncReceiver extends BroadcastReceiver {

        private Handler handler; // Handler used to execute code on the UI thread

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Post the UI updating code to our Handler

            this.handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    myGoodsFragment.reloadMainList();
                    myGoodsFragment.mAdapter.refreshList();
                    myGoodsFragment.categoriesToChooseAdapter.refreshList();

                    if (syncTriggeredByUser){
                        Toast.makeText(context, R.string.sync_finished, Toast.LENGTH_SHORT).show();
                        syncTriggeredByUser = false;
                    }

                    Log.d(TAG, "Sync BroadCast received");
                }
            });
        }
    }

}
