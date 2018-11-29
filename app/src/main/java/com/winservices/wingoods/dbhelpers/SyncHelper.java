package com.winservices.wingoods.dbhelpers;

import android.content.Context;
import android.os.AsyncTask;

import com.winservices.wingoods.models.User;

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
            UsersDataManager usersDataManager = new UsersDataManager(synchronizer.context);
            User user = usersDataManager.getCurrentUser();
            if (user != null) synchronizer.sync();
            return null;
        }
    }

}
