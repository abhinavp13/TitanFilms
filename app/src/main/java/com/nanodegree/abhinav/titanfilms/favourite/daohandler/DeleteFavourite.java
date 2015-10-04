package com.nanodegree.abhinav.titanfilms.favourite.daohandler;

import android.content.Context;


import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.favourite.contentprovider.FavouriteContentProvider;

/**
 * Class used to delete saved data on android device.
 * It uses content provider for deletion.
 *
 * Created by Abhinav Puri
 */
public class DeleteFavourite {

    /**
     * Activity context
     */
    private Context context;

    /**
     * Constructor saving context of an activity.
     *
     * @param context
     */
    public DeleteFavourite(Context context){
        this.context = context;
    }

    /**
     * Function calling content provider delete query
     *
     * @param theMovieDBObject
     */
    public void deleteData(TheMovieDBObject theMovieDBObject){
        context.getContentResolver().delete(FavouriteContentProvider.CONTENT_URI,"votecount=?", new String[]{theMovieDBObject.getVoteCount()});
    }
}
