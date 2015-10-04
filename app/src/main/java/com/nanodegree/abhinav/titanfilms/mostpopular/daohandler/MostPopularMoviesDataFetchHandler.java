package com.nanodegree.abhinav.titanfilms.mostpopular.daohandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.mostpopular.adapter.MostPopularMoviesGridAdapter;

import java.util.ArrayList;

/**
 * This class handles fetching of data for Most popular Movies
 *
 * Created by Abhinav Puri
 */
public class MostPopularMoviesDataFetchHandler {

    /**
     * Static url prefix for fetching data for most popular movies.
     */
    public static String PREURLFORMOSTPOPULAR = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";

    /**
     * Context for activity attached
     */
    private Context context;

    /**
     * Dao data fetch class
     */
    private MostPopularMoviesPaginatedData mostPopularMoviesPaginatedData;

    /**
     * Application saved data
     */
    private ApplicationSavedData applicationSavedData;

    /**
     * Adapter for Most popular movies grid.
     */
    private MostPopularMoviesGridAdapter mostPopularMoviesGridAdapter;

    /**
     * Boolean value noting whether first time is triggered or not.
     */
    private boolean isForTheFirstTime;


    public MostPopularMoviesDataFetchHandler(MostPopularMoviesGridAdapter mostPopularMoviesGridAdapter,Context context, MostPopularMoviesPaginatedData mostPopularMoviesPaginatedData){
        this.context = context;
        this.mostPopularMoviesPaginatedData = mostPopularMoviesPaginatedData;
        this.mostPopularMoviesGridAdapter = mostPopularMoviesGridAdapter;
        PREURLFORMOSTPOPULAR = PREURLFORMOSTPOPULAR + context.getResources().getString(R.string.key)+"&page=";
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) ((Activity) context).getApplication());
        isForTheFirstTime = true;
    }

    public void startHandling(){

        if(applicationSavedData.getMostPopularMoviesSavedData().size() == 0) {
            mostPopularMoviesPaginatedData.fetchData(PREURLFORMOSTPOPULAR + applicationSavedData.getNextPageToDownloadFromForMostPopular());
        } else {
            ArrayList<TheMovieDBObject> safeList = new ArrayList<>();
            safeList.addAll(applicationSavedData.getMostPopularMoviesSavedData());

            mostPopularMoviesGridAdapter.getTheMostPopularMoviesData().clear();
            mostPopularMoviesGridAdapter.notifyDataSetChanged();
            isForTheFirstTime = false;

            mostPopularMoviesGridAdapter.addMoreItems(safeList);

        }

    }

    public void dataReady(ArrayList<TheMovieDBObject> theMovieDBObjectArrayList){
        // Clear data if it is for the first time :
        if(isForTheFirstTime){
            mostPopularMoviesGridAdapter.getTheMostPopularMoviesData().clear();
            mostPopularMoviesGridAdapter.notifyDataSetChanged();

        }
        ArrayList<TheMovieDBObject> safeList  = new ArrayList<>();
        safeList.addAll(mostPopularMoviesGridAdapter.getTheMostPopularMoviesData());
        safeList.addAll(theMovieDBObjectArrayList);
        applicationSavedData.setMostPopularMoviesSavedData(safeList);

        isForTheFirstTime = false;
        mostPopularMoviesGridAdapter.addMoreItems(theMovieDBObjectArrayList);

    }

    public void loadMore(){
        mostPopularMoviesPaginatedData.fetchData(PREURLFORMOSTPOPULAR + applicationSavedData.getNextPageToDownloadFromForMostPopular());
    }

}
