package com.nanodegree.abhinav.titanfilms.favourite.fragments;

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
import com.nanodegree.abhinav.titanfilms.favourite.adapter.FavouriteMoviesGridAdapter;
import com.nanodegree.abhinav.titanfilms.favourite.daohandler.FavouriteMoviesDataFetchHandler;
import com.nanodegree.abhinav.titanfilms.favourite.daohandler.FavouriteMoviesPaginatedData;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

import java.util.ArrayList;

/**
 * Created by Abhinav Puri
 */
public class TheFavouriteMoviesFragment  extends Fragment {

    /**
     * Root View for this fragment.
     */
    private View rootView;

    /**
     * Adapter class for grid view
     */
    private FavouriteMoviesGridAdapter favouriteMoviesGridAdapter;

    /**
     * Data Saved for application
     */
    private static ApplicationSavedData applicationSavedData;

    /**
     * Grid View for favourite movies.
     */
    private GridView gridView;

    /**
     * The data fetching class.
     */
    private static FavouriteMoviesPaginatedData favouriteMoviesPaginatedData;

    /**
     * Data fetch handling class.
     */
    private static FavouriteMoviesDataFetchHandler favouriteMoviesDataFetchHandler;

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

        // Initializing Favourite movies adapter :
        favouriteMoviesGridAdapter = new FavouriteMoviesGridAdapter(getActivity(), LayoutInflater.from(getActivity()), null);

        // Adding adapter :
        gridView.setAdapter(favouriteMoviesGridAdapter);

        // Loading Dummy for now :
        loadingDummyView();

        // Start Data Fetching :
        favouriteMoviesPaginatedData = new FavouriteMoviesPaginatedData(getActivity());

        // Calling handler :
        favouriteMoviesDataFetchHandler = new FavouriteMoviesDataFetchHandler(favouriteMoviesGridAdapter,getActivity(),favouriteMoviesPaginatedData);

        // Adding handler :
        favouriteMoviesPaginatedData.setHandler(favouriteMoviesDataFetchHandler);

        // Adding handler to adapter for loading more items :
        favouriteMoviesGridAdapter.setHandler(favouriteMoviesDataFetchHandler);

        // Start Handling :
        favouriteMoviesDataFetchHandler.startHandling();


        // It wont do anything if nothing is selected or rootview is null, or fragment not created yet.
        TheFavouriteDetailsFragment fragment = (TheFavouriteDetailsFragment)getFragmentManager().findFragmentById(R.id.favourite_fragment_2);
        if(fragment!=null) {
            fragment.refresh(true);
        }

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
        favouriteMoviesGridAdapter.addMoreItemsClearPrevious(theMovieDBObjects, true);
    }

    /**
     * Resuming activity
     */
    public static void resume(){
        favouriteMoviesPaginatedData.fetchData(favouriteMoviesDataFetchHandler.PREURLFORFAVOURITE + applicationSavedData.getNextPageToDownloadFromForFavourite());
    }
}
