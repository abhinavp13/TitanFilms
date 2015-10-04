package com.nanodegree.abhinav.titanfilms.highestrated.daohandler;

import android.app.Activity;
import android.content.Context;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.highestrated.adapter.HighestRatedMoviesGridAdapter;
import com.nanodegree.abhinav.titanfilms.highestrated.daohandler.HighestRatedMoviesPaginatedData;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

import java.util.ArrayList;

/**
 * This class handles fetching of data for Highest rated Movies
 *
 * Created by Abhinav Puri
 */
public class HighestRatedMoviesDataFetchHandler {

    /**
     * Static url prefix for fetching data for highest rated movies.
     */
    public static String PREURLFORHIGHESTRATED = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=1000&api_key=";

    /**
     * Context for activity attached
     */
    private Context context;

    /**
     * Dao data fetch class
     */
    private HighestRatedMoviesPaginatedData highestRatedMoviesPaginatedData;

    /**
     * Application saved data
     */
    private ApplicationSavedData applicationSavedData;

    /**
     * Adapter for Highest rated movies grid.
     */
    private HighestRatedMoviesGridAdapter highestRatedMoviesGridAdapter;

    /**
     * Boolean value noting whether first time is triggered or not.
     */
    private boolean isForTheFirstTime;


    public HighestRatedMoviesDataFetchHandler(HighestRatedMoviesGridAdapter highestRatedMoviesGridAdapter,Context context, HighestRatedMoviesPaginatedData highestRatedMoviesPaginatedData){
        this.context = context;
        this.highestRatedMoviesPaginatedData = highestRatedMoviesPaginatedData;
        this.highestRatedMoviesGridAdapter = highestRatedMoviesGridAdapter;
        PREURLFORHIGHESTRATED = PREURLFORHIGHESTRATED + context.getResources().getString(R.string.key)+"&page=";
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) ((Activity) context).getApplication());
        isForTheFirstTime = true;
    }

    public void startHandling(){

        if(applicationSavedData.getHighestRatedMoviesSavedData().size() == 0) {
            highestRatedMoviesPaginatedData.fetchData(PREURLFORHIGHESTRATED + applicationSavedData.getNextPageToDownloadFromForHighestRated());
        } else {
            ArrayList<TheMovieDBObject> safeList = new ArrayList<>();
            safeList.addAll(applicationSavedData.getHighestRatedMoviesSavedData());

            highestRatedMoviesGridAdapter.getTheHighestRatedMoviesData().clear();
            highestRatedMoviesGridAdapter.notifyDataSetChanged();
            isForTheFirstTime = false;

            highestRatedMoviesGridAdapter.addMoreItems(safeList);

        }

    }

    public void dataReady(ArrayList<TheMovieDBObject> theMovieDBObjectArrayList){
        // Clear data if it is for the first time :
        if(isForTheFirstTime){
            highestRatedMoviesGridAdapter.getTheHighestRatedMoviesData().clear();
            highestRatedMoviesGridAdapter.notifyDataSetChanged();

        }
        ArrayList<TheMovieDBObject> safeList  = new ArrayList<>();
        safeList.addAll(highestRatedMoviesGridAdapter.getTheHighestRatedMoviesData());
        safeList.addAll(theMovieDBObjectArrayList);
        applicationSavedData.setHighestRatedMoviesSavedData(safeList);

        isForTheFirstTime = false;
        highestRatedMoviesGridAdapter.addMoreItems(theMovieDBObjectArrayList);

    }

    public void loadMore(){
        highestRatedMoviesPaginatedData.fetchData(PREURLFORHIGHESTRATED + applicationSavedData.getNextPageToDownloadFromForHighestRated());
    }

}
