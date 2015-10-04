package com.nanodegree.abhinav.titanfilms.dao;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

/**
 * This Class contains the database schema and creation logic.
 *
 * Created by Abhinav Puri
 */
public class FavouriteTable {

    /**
     * Table Name
     */
    public static final String TABLE_FAVOURITE = "favourite";

    /**
     * Primary Key for table
     */
    public static final String COLUMN_ID = "_id";

    /**
     * Columns for table
     */
    public static final String COLUMN_MOVIE_ID = "movieid";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_DATE = "releasedate";
    public static final String COLUMN_POSTER_BLOB = "posterblob";
    public static final String COLUMN_VOTE_AVERAGE = "voteaverage";
    public static final String COLUMN_VOTE_COUNT = "votecount";
    public static final String COLUMN_PLOT = "plot";
    public static final String COLUMN_BACKDROP_BLOB = "backdropblob";
    public static final String COLUMN_REVIEWS = "reviews";
    public static final String COLUMN_TRAILER = "trailer";
    public static final String COLUMN_GENREIDS = "genreids";

    /**
     * Database creation query
     */
    private static final String DATABASE_CREATION = "create table "
            + TABLE_FAVOURITE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MOVIE_ID + " text not null, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_RELEASE_DATE + " text not null, "
            + COLUMN_POSTER_BLOB + " blob, "
            + COLUMN_VOTE_AVERAGE + " text not null, "
            + COLUMN_VOTE_COUNT + " text not null, "
            + COLUMN_PLOT + " text,"
            + COLUMN_BACKDROP_BLOB + " blob, "
            + COLUMN_REVIEWS + " text, "
            + COLUMN_TRAILER + " text, "
            + COLUMN_GENREIDS + " text "
            + ");";


    /**
     * Function called by the framework, if the database is accessed but not yet created.
     *
     * @param sqLiteDatabase
     */
    public static void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(DATABASE_CREATION);
    }

    /**
     * Called, if the database version is increased in your application code
     *
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    public static void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
        onCreate(sqLiteDatabase);
    }


}
