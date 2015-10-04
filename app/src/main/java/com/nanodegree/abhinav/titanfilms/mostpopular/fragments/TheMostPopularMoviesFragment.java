package com.nanodegree.abhinav.titanfilms.mostpopular.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.mostpopular.activities.MostPopularMoviesGridActivity;
import com.nanodegree.abhinav.titanfilms.selectionchange.SelectionChangeActivity;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.mostpopular.adapter.MostPopularMoviesGridAdapter;
import com.nanodegree.abhinav.titanfilms.mostpopular.daohandler.MostPopularMoviesDataFetchHandler;
import com.nanodegree.abhinav.titanfilms.mostpopular.daohandler.MostPopularMoviesPaginatedData;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;

import java.util.ArrayList;

/**
 * Created by Abhinav Puri
 */
public class TheMostPopularMoviesFragment extends Fragment{

    /**
     * Root View for this fragment.
     */
    private View rootView;

    /**
     * Adapter class for grid view
     */
    private MostPopularMoviesGridAdapter mostPopularMoviesGridAdapter;

    /**
     * Data Saved for application
     */
    private static ApplicationSavedData applicationSavedData;

    /**
     * Grid View for most popular movies.
     */
    private GridView gridView;

    /**
     * The data fetching class.
     */
    private static MostPopularMoviesPaginatedData mostPopularMoviesPaginatedData;

    /**
     * Data fetch handling class.
     */
    private static MostPopularMoviesDataFetchHandler mostPopularMoviesDataFetchHandler;

    /**
     * Used by scroll event to detect top reached :
     */
    private boolean isRequiredToNotify = false;

    /**
     * Action Bar view :
     */
    private RelativeLayout actionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.grid_view,container,false);

        // Grid view initiation :
        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        // Populate applicationSavedData :
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) getActivity().getApplication());

        // Initializing Most popular movies adapter :
        mostPopularMoviesGridAdapter = new MostPopularMoviesGridAdapter(getActivity(), LayoutInflater.from(getActivity()), null);

        // Adding adapter :
        gridView.setAdapter(mostPopularMoviesGridAdapter);

        // Loading Dummy for now :
        loadingDummyView();

        // Start Data Fetching :
        mostPopularMoviesPaginatedData = new MostPopularMoviesPaginatedData(getActivity());

        // Calling handler :
        mostPopularMoviesDataFetchHandler = new MostPopularMoviesDataFetchHandler(mostPopularMoviesGridAdapter,getActivity(),mostPopularMoviesPaginatedData);

        // Adding handler :
        mostPopularMoviesPaginatedData.setHandler(mostPopularMoviesDataFetchHandler);

        // Adding handler to adapter for loading more items :
        mostPopularMoviesGridAdapter.setHandler(mostPopularMoviesDataFetchHandler);

        // Start Handling :
        mostPopularMoviesDataFetchHandler.startHandling();

        return rootView;
    }

    /**
     * Loads dummy tiles
     * Just some tiles expressing loading symbol.
     * This function clears data from adapter also.
     */
    public void loadingDummyView(){
        ArrayList<TheMovieDBObject> theMovieDBObjects = new ArrayList<>();
        for (int i = 0;i<12;i++){
            theMovieDBObjects.add(null);
        }
        mostPopularMoviesGridAdapter.addMoreItemsClearPrevious(theMovieDBObjects, true);
    }

    /**
     * Resuming activity
     */
    public static void resume(){
        mostPopularMoviesPaginatedData.fetchData(mostPopularMoviesDataFetchHandler.PREURLFORMOSTPOPULAR + applicationSavedData.getNextPageToDownloadFromForMostPopular());
    }

}
