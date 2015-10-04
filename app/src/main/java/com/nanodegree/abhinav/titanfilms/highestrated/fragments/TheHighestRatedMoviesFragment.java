package com.nanodegree.abhinav.titanfilms.highestrated.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.highestrated.adapter.HighestRatedMoviesGridAdapter;
import com.nanodegree.abhinav.titanfilms.highestrated.daohandler.HighestRatedMoviesDataFetchHandler;
import com.nanodegree.abhinav.titanfilms.highestrated.daohandler.HighestRatedMoviesPaginatedData;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

import java.util.ArrayList;

/**
 * Created by Abhinav Puri
 */
public class TheHighestRatedMoviesFragment  extends Fragment {

    /**
     * Root View for this fragment.
     */
    private View rootView;

    /**
     * Adapter class for grid view
     */
    private HighestRatedMoviesGridAdapter highestRatedMoviesGridAdapter;

    /**
     * Data Saved for application
     */
    private static ApplicationSavedData applicationSavedData;

    /**
     * Grid View for highest rated movies.
     */
    private GridView gridView;

    /**
     * The data fetching class.
     */
    private static HighestRatedMoviesPaginatedData highestRatedMoviesPaginatedData;

    /**
     * Data fetch handling class.
     */
    private static HighestRatedMoviesDataFetchHandler highestRatedMoviesDataFetchHandler;

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

        // Initializing Highest rated movies adapter :
        highestRatedMoviesGridAdapter = new HighestRatedMoviesGridAdapter(getActivity(), LayoutInflater.from(getActivity()), null);

        // Adding adapter :
        gridView.setAdapter(highestRatedMoviesGridAdapter);

        // Loading Dummy for now :
        loadingDummyView();

        // Start Data Fetching :
        highestRatedMoviesPaginatedData = new HighestRatedMoviesPaginatedData(getActivity());

        // Calling handler :
        highestRatedMoviesDataFetchHandler = new HighestRatedMoviesDataFetchHandler(highestRatedMoviesGridAdapter,getActivity(),highestRatedMoviesPaginatedData);

        // Adding handler :
        highestRatedMoviesPaginatedData.setHandler(highestRatedMoviesDataFetchHandler);

        // Adding handler to adapter for loading more items :
        highestRatedMoviesGridAdapter.setHandler(highestRatedMoviesDataFetchHandler);

        // Start Handling :
        highestRatedMoviesDataFetchHandler.startHandling();

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
        highestRatedMoviesGridAdapter.addMoreItemsClearPrevious(theMovieDBObjects, true);
    }

    /**
     * Resuming activity
     */
    public static void resume(){
        highestRatedMoviesPaginatedData.fetchData(highestRatedMoviesDataFetchHandler.PREURLFORHIGHESTRATED + applicationSavedData.getNextPageToDownloadFromForHighestRated());
    }
}
