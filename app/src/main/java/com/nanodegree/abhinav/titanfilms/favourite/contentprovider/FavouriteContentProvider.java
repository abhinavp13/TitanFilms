package com.nanodegree.abhinav.titanfilms.favourite.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.nanodegree.abhinav.titanfilms.dao.FavouriteDatabaseHelper;
import com.nanodegree.abhinav.titanfilms.dao.FavouriteTable;

import java.net.ContentHandler;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Content Provider class for data handling saved in database.
 *
 * Created by Abhinav Puri.
 */
public class FavouriteContentProvider extends ContentProvider {

    /**
     * Database wrapper
     */
    private FavouriteDatabaseHelper databaseHelper;

    /**
     * Some static ints used for URI Matcher
     */
    private static final int FAVOURITE = 10;
    private static final int FAVOURTIE_ID = 20;

    /**
     * Content Provider Authority
     */
    private static final String AUTHORITY = "com.nanodegree.abhinav.titanfilms.favourite.contentprovider";

    /**
     * Base Path for URI
     */
    private static final String BASE_PATH = "favourite";

    /**
     * Content Provider URI
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    /**
     * Content Provider Type
     */
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/favourite";

    /**
     * Content Provider Item Type
     */
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/favourite";

    /**
     * URI Matcher
     */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Some static initializations
     */
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, FAVOURITE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", FAVOURTIE_ID);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new FavouriteDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        /*
         * Using queryBuilder instead of query.
         */
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(FavouriteTable.TABLE_FAVOURITE);

        int uriType = sURIMatcher.match(uri);
        switch (uriType){
            case FAVOURITE:
                break;
            case FAVOURTIE_ID:
                // Adding the primary key to original query :
                queryBuilder.appendWhere(FavouriteTable.COLUMN_ID + "="+ uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Need a writable database :
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        long id = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FAVOURITE:
                id = sqlDB.insert(FavouriteTable.TABLE_FAVOURITE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        int rowsDeleted = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FAVOURITE:
                rowsDeleted = sqlDB.delete(FavouriteTable.TABLE_FAVOURITE, selection,
                        selectionArgs);
                break;
            case FAVOURTIE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(FavouriteTable.TABLE_FAVOURITE, FavouriteTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(FavouriteTable.TABLE_FAVOURITE, FavouriteTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        int rowsUpdated = 0;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FAVOURITE:
                rowsUpdated = sqlDB.update(FavouriteTable.TABLE_FAVOURITE,values,selection,selectionArgs);
                break;
            case FAVOURTIE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(FavouriteTable.TABLE_FAVOURITE,values, FavouriteTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(FavouriteTable.TABLE_FAVOURITE, values, FavouriteTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    /**
     * Check for availability of column asked by user.
     *
     * @param projection
     */
    private void checkColumns(String[] projection) {
        String[] available = { FavouriteTable.COLUMN_TITLE,
                FavouriteTable.COLUMN_RELEASE_DATE,
                FavouriteTable.COLUMN_MOVIE_ID,
                FavouriteTable.COLUMN_POSTER_BLOB,
                FavouriteTable.COLUMN_VOTE_AVERAGE,
                FavouriteTable.COLUMN_VOTE_COUNT,
                FavouriteTable.COLUMN_PLOT,
                FavouriteTable.COLUMN_BACKDROP_BLOB,
                FavouriteTable.COLUMN_REVIEWS,
                FavouriteTable.COLUMN_TRAILER,
                FavouriteTable.COLUMN_GENREIDS,
                FavouriteTable.COLUMN_ID };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));

            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
