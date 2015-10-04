package com.nanodegree.abhinav.titanfilms.favourite.daohandler;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.dao.FavouriteTable;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.favourite.contentprovider.FavouriteContentProvider;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

import java.util.ArrayList;

/**
 * Mainly used for select queries.
 *
 * Created by Abhinav Puri
 */
public class FetchFavourite {

    /**
     * This is an activity context
     */
    private Context context;

    /**
     * This is Application Instance
     */
    private TitanFilmsApplication titanFilmsApplication;

    /**
     * Constructor used to save context.
     *
     * @param context
     */
    public FetchFavourite(Context context){
        this.context = context;
    }

    public FetchFavourite(TitanFilmsApplication titanFilmsApplication){
        this.titanFilmsApplication = titanFilmsApplication;
    }

    /**
     * Get Cursor pointing at first select query result
     *
     * @return DB Cursor
     */
    private Cursor getCursor(){
        String[] projection = {
                FavouriteTable.COLUMN_TITLE,
                FavouriteTable.COLUMN_MOVIE_ID,
                FavouriteTable.COLUMN_RELEASE_DATE,
                FavouriteTable.COLUMN_POSTER_BLOB,
                FavouriteTable.COLUMN_VOTE_AVERAGE,
                FavouriteTable.COLUMN_VOTE_COUNT,
                FavouriteTable.COLUMN_PLOT,
                FavouriteTable.COLUMN_BACKDROP_BLOB,
                FavouriteTable.COLUMN_REVIEWS,
                FavouriteTable.COLUMN_TRAILER,
                FavouriteTable.COLUMN_GENREIDS
        };

        // Select Query Cursor :
        if(context != null) {
            return context.getContentResolver().query(FavouriteContentProvider.CONTENT_URI, projection, null, null, null);
        } else {
            return titanFilmsApplication.getContentResolver().query(FavouriteContentProvider.CONTENT_URI, projection, null, null, null);
        }
    }

    /**
     * This function gets the cursor and loads the data from db.
     *
     * @return  arraylist of TheMovieDBObject
     */
    public ArrayList<TheMovieDBObject> fetchData(){

        ArrayList<TheMovieDBObject> theMovieDBObjectArrayList = null;
        Cursor cursor = getCursor();
        if(cursor != null){
            while(cursor.moveToNext()){

                TheMovieDBObject theMovieDBObject = new TheMovieDBObject();

                theMovieDBObject.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_TITLE)));
                theMovieDBObject.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_MOVIE_ID))));
                theMovieDBObject.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_RELEASE_DATE)));
                theMovieDBObject.setBitmapPosterImage(TFUtils.getImage(cursor.getBlob(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_POSTER_BLOB))));
                theMovieDBObject.setVoteAverage(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_VOTE_AVERAGE)));
                theMovieDBObject.setVoteCount(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_VOTE_COUNT)));
                theMovieDBObject.setPlotSynopsis(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_PLOT)));
                theMovieDBObject.setBitmapBackdropImage(TFUtils.getImage(cursor.getBlob(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_BACKDROP_BLOB))));
                theMovieDBObject.setReviews(TFUtils.GsonStringToHashMap(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_REVIEWS))));
                theMovieDBObject.setTrailerLink(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_TRAILER)));
                theMovieDBObject.setGenreIds(TFUtils.GsonStringToIntArray(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteTable.COLUMN_GENREIDS))));

                if(theMovieDBObjectArrayList == null)
                    theMovieDBObjectArrayList = new ArrayList<>();

                theMovieDBObjectArrayList.add(theMovieDBObject);
            }
        }

        return theMovieDBObjectArrayList;
    }

}

