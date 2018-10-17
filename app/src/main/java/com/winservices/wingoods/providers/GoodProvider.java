package com.winservices.wingoods.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.winservices.wingoods.dbhelpers.DataBaseHelper;
import com.winservices.wingoods.models.Good;

/**
 * Created by omarh on 17/10/2018.
 */

public class GoodProvider extends ContentProvider {

    public static final String LOG_TAG = GoodProvider.class.getSimpleName();
    private static final int GOODS = 100;
    private static final int GOOD_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Good.CONTENT_AUTHORITY, Good.PATH_GOODS,GOODS);
        sUriMatcher.addURI(Good.CONTENT_AUTHORITY, Good.PATH_GOODS+"/#",GOOD_ID);
    }

    private DataBaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = DataBaseHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case GOODS:
                return insertGoods(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertGoods(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = db.insert(Good.GoodEntry.TABLE_NAME, null, contentValues);

        if (id==-1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
