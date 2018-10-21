package com.winservices.wingoods.utils;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

import com.winservices.wingoods.dbhelpers.Synchronizer;
import com.winservices.wingoods.sync.ListaSyncAdapter;


public class MJobScheduler extends JobService {

    private static final String TAG = "MJobScheduler";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(final JobParameters params) {

        Log.d(TAG, "Job started");

        doBackGroundWork(params);

        return true;
    }

    private void doBackGroundWork (final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (jobCancelled){ return; }

                //ListaSyncAdapter.syncImmediately(getApplicationContext());

                /* (int i = 0; i < 10; i++) {
                  Log.d(TAG, "run : " + i);
                  if (jobCancelled){
                      return;
                  }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
                Log.d(TAG, "Job finished");
                jobFinished(params, false);

            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cacelled before completion");
        jobCancelled = true;
        return false;
    }
}
