package com.winservices.wingoods.activities;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.winservices.wingoods.utils.Color;
import com.winservices.wingoods.utils.Constants;
import com.winservices.wingoods.utils.Controlers;
import com.winservices.wingoods.utils.MJobScheduler;
import com.winservices.wingoods.utils.NetworkMonitor;
import com.winservices.wingoods.utils.UtilsFunctions;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    public static int FRAGMENT_REQUEST_CODE = 1;
    FloatingActionButton fabPlus, fabAddCategory, fabAddOrder;
    Animation fabOpen, fabClose, fabRotateClockWise, fabAntiClockWise, fabTxtOpen, fabTxtClose;
    TextView addCategoriesLabel, txtAddOrder;
    boolean isOpen = false;
    FrameLayout mInterceptorFrame;
    int fragmentId = 101;
    MyGoods myGoodsFragment;
    private String currentFragTag = "none";
    private SyncReceiver syncReceiver;
    private boolean syncTriggeredByUser = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        myGoodsFragment = new MyGoods();

        syncReceiver = new SyncReceiver();

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

        fabAddOrder = findViewById(R.id.fab_add_order);
        txtAddOrder = findViewById(R.id.add_order_label);
        fabPlus = findViewById(R.id.fab_plus);
        fabAddCategory = findViewById(R.id.fab_add_category);
        addCategoriesLabel = findViewById(R.id.txt_add_categories_label);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabTxtOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_txt_open);
        fabTxtClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_txt_close);
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

        fabAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isThereGoodToBuy()) {
                    SyncHelper.sync(context);
                    Intent intent = new Intent(MainActivity.this, ShopsActivity.class);
                    intent.putExtra(Constants.ORDER_INITIATED, true);
                    startActivity(intent);
                    collapseFab();
                } else {
                    Toast.makeText(context, R.string.no_items_to_buy, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                            category.setDCategoryID(Constants.USER_D_CATEGORY_ID);

                            DataManager dataManager = new DataManager(context);
                            int result = dataManager.addCategory(context, category);

                            switch (result) {
                                case Constants.SUCCESS:
                                    Toast.makeText(context, context.getResources().getString(R.string.category_add_success), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    collapseFab();
                                    SyncHelper.sync(getApplicationContext());
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

    }

    private boolean isThereGoodToBuy() {
        GoodsDataProvider goodsDataProvider = new GoodsDataProvider(this);
        int goodsToBuyNb = goodsDataProvider.getGoodsToBuyNb();
        return (goodsToBuyNb > 0);
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
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
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
            fragmentId = data.getIntExtra(Constants.SELECTED_FRAGMENT, 101);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(syncReceiver, new IntentFilter(Constants.ACTION_REFRESH_AFTER_SYNC));
        displayFragment(myGoodsFragment, MyGoods.TAG);
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
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_invite:
                startActivity(new Intent(this, InviteActivity.class));
                break;
            case R.id.action_receive_invitation:
                if (NetworkMonitor.checkNetworkConnection(this)) {
                    startActivity(new Intent(this, ReceiveInvitationActivity.class));
                } else {
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync:
                if (NetworkMonitor.checkNetworkConnection(this)) {
                    syncTriggeredByUser = true;
                    SyncHelper.sync(this);
                } else {
                    Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayFragment(Fragment fragment, String tag) {
        if (currentFragTag.equals(tag)) return;
        currentFragTag = tag;
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_main, fragment, tag)
                .commit();
    }

    private void expandFab() {
        mInterceptorFrame.setVisibility(View.VISIBLE);
        fabAddOrder.startAnimation(fabOpen);
        txtAddOrder.startAnimation(fabTxtOpen);
        fabAddCategory.startAnimation(fabOpen);
        addCategoriesLabel.startAnimation(fabTxtOpen);
        fabPlus.startAnimation(fabRotateClockWise);
        fabAddCategory.setClickable(true);
        isOpen = true;
    }

    private void collapseFab() {
        mInterceptorFrame.setVisibility(View.INVISIBLE);
        fabAddOrder.startAnimation(fabClose);
        txtAddOrder.startAnimation(fabTxtClose);
        fabAddCategory.startAnimation(fabClose);
        addCategoriesLabel.startAnimation(fabTxtClose);
        fabPlus.startAnimation(fabAntiClockWise);
        fabAddCategory.setClickable(false);
        isOpen = false;
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

                    if (syncTriggeredByUser) {
                        Toast.makeText(context, R.string.sync_finished, Toast.LENGTH_SHORT).show();
                        syncTriggeredByUser = false;
                    }

                    Log.d(TAG, "Sync BroadCast received");
                }
            });
        }
    }

}
