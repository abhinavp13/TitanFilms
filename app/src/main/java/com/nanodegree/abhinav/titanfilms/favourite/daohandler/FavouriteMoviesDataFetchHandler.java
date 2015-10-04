package com.nanodegree.abhinav.titanfilms.favourite.daohandler;

import android.app.Activity;
import android.content.Context;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.favourite.adapter.FavouriteMoviesGridAdapter;
import com.nanodegree.abhinav.titanfilms.favourite.daohandler.FavouriteMoviesPaginatedData;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

import java.util.ArrayList;

/**
 * This class handles fetching of data for Favourite Movies
 *
 * Created by Abhinav Puri
 */
public class FavouriteMoviesDataFetchHandler {

    /**
     * Static url prefix for fetching data for favourite movies.
     */
    public static String PREURLFORFAVOURITE = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=1000&api_key=";

    /**
     * Context for activity attached
     */
    private Context context;

    /**
     * Dao data fetch class
     */
    private FavouriteMoviesPaginatedData favouriteMoviesPaginatedData;

    /**
     * Application saved data
     */
    private ApplicationSavedData applicationSavedData;

    /**
     * Adapter for Favourite movies grid.
     */
    private FavouriteMoviesGridAdapter favouriteMoviesGridAdapter;

    /**
     * Boolean value noting whether first time is triggered or not.
     */
    private boolean isForTheFirstTime;


    public FavouriteMoviesDataFetchHandler(FavouriteMoviesGridAdapter favouriteMoviesGridAdapter,Context context, FavouriteMoviesPaginatedData favouriteMoviesPaginatedData){
        this.context = context;
        this.favouriteMoviesPaginatedData = favouriteMoviesPaginatedData;
        this.favouriteMoviesGridAdapter = favouriteMoviesGridAdapter;
        PREURLFORFAVOURITE = PREURLFORFAVOURITE + context.getResources().getString(R.string.key)+"&page=";
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) ((Activity) context).getApplication());
        isForTheFirstTime = true;
    }

    /**
     * This function is called from Adapter to start fetching data.
     */
    public void startHandling(){
        favouriteMoviesPaginatedData.fetchData(PREURLFORFAVOURITE + applicationSavedData.getNextPageToDownloadFromForFavourite());
    }

    /**
     * Callback function used by paginated data fetching class when data is ready.
     *
     * @param theMovieDBObjectArrayList
     */
    public void dataReady(ArrayList<TheMovieDBObject> theMovieDBObjectArrayList){

        if(theMovieDBObjectArrayList == null)
            return;

        ArrayList<TheMovieDBObject> safeList  = new ArrayList<>();
        safeList.addAll(theMovieDBObjectArrayList);

        favouriteMoviesGridAdapter.addMoreItemsClearPrevious(theMovieDBObjectArrayList,false);

    }

    public void loadMore(){
        favouriteMoviesPaginatedData.fetchData(PREURLFORFAVOURITE + applicationSavedData.getNextPageToDownloadFromForFavourite());
    }

}
