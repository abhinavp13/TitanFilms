package com.nanodegree.abhinav.titanfilms.favourite.daohandler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

import com.nanodegree.abhinav.titanfilms.dao.FavouriteTable;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.favourite.contentprovider.FavouriteContentProvider;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

/**
 * This class actually saves data using sqlite db.
 *
 * Created by Abhinav Puri
 */
public class SaveFavourite {

    /**
     * Context of an Activity.
     */
    private Context context;

    /**
     * Constructor saving context
     *
     * @param context
     */
    public SaveFavourite(Context context){
        this.context = context;
    }

    /**
     * Important function calling actual insert query.
     *
     * @param movieId
     * @param title
     * @param releaseDate
     * @param posterBlob
     * @param voteAverage
     * @param voteCount
     * @param plot
     * @param backdropBlob
     * @param reviews
     * @param trailer
     * @param genreIds
     */
    private void saveData(String movieId, String title, String releaseDate,
                         byte[] posterBlob, String voteAverage, String voteCount,
                         String plot, byte[] backdropBlob, String reviews,
                         String trailer, String genreIds) {

        // Preparing content map :
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteTable.COLUMN_MOVIE_ID, movieId);
        contentValues.put(FavouriteTable.COLUMN_TITLE, title);
        contentValues.put(FavouriteTable.COLUMN_RELEASE_DATE, releaseDate);
        contentValues.put(FavouriteTable.COLUMN_POSTER_BLOB, posterBlob);
        contentValues.put(FavouriteTable.COLUMN_VOTE_AVERAGE, voteAverage);
        contentValues.put(FavouriteTable.COLUMN_VOTE_COUNT, voteCount);
        contentValues.put(FavouriteTable.COLUMN_PLOT, plot);
        contentValues.put(FavouriteTable.COLUMN_BACKDROP_BLOB, backdropBlob);
        contentValues.put(FavouriteTable.COLUMN_REVIEWS, reviews);
        contentValues.put(FavouriteTable.COLUMN_TRAILER, trailer);
        contentValues.put(FavouriteTable.COLUMN_GENREIDS, genreIds);

        // Finally, Insert :
        ((Activity)context).getContentResolver().insert(FavouriteContentProvider.CONTENT_URI, contentValues);

    }

    /**
     * This function creates values from db object and persist from them.
     *
     * @param theMovieDBObject
     * @param bitmapPosterImage for poster Image
     * @param bitmapBackdropImage for backdrop Image
     */
    public void saveDataFromDBObject(TheMovieDBObject theMovieDBObject, Bitmap bitmapPosterImage, Bitmap bitmapBackdropImage){

        // Fetching and passing required data :
        saveData(String.valueOf(theMovieDBObject.getId()),
                theMovieDBObject.getTitle(),
                theMovieDBObject.getReleaseDate(),
                TFUtils.getBytes(bitmapPosterImage),
                theMovieDBObject.getVoteAverage(),
                theMovieDBObject.getVoteCount(),
                theMovieDBObject.getPlotSynopsis(),
                TFUtils.getBytes(bitmapBackdropImage),
                TFUtils.hashMapToGsonString(theMovieDBObject.getReviews()),
                theMovieDBObject.getTrailerLink(),
                TFUtils.intArrayToGsonString(theMovieDBObject.getGenreIds())
        );
    }

}
