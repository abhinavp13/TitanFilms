package com.nanodegree.abhinav.titanfilms.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is Database helper class.
 * A wrapper around Database.
 *
 * Created by Abhinav Puri
 */
public class FavouriteDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database name
     */
    private static final String DATABASE_NAME = FavouriteTable.TABLE_FAVOURITE + "table.db";

    /**
     * Database version
     */
    private static final int DATABASE_VERSION = 1;

    public FavouriteDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        FavouriteTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        FavouriteTable.onUpgrade(db,oldVersion,newVersion);
    }
}
