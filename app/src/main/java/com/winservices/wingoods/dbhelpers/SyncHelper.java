package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.os.AsyncTask;

public class SyncHelper {


    public static void sync(Context context){
        new SyncAsyncTask(context).execute();
    }

    private static class SyncAsyncTask extends AsyncTask<Void, Void, Void> {
        private Synchronizer synchronizer;

        private SyncAsyncTask(Context context) {
            this.synchronizer = new Synchronizer(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            synchronizer.sync();
            return null;
        }
    }

}
