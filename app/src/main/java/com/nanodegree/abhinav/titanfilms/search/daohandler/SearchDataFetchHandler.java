package com.nanodegree.abhinav.titanfilms.search.daohandler;

import android.app.Activity;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.GridView;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.search.adapter.SearchGridAdapter;

import java.util.ArrayList;

import pl.tajchert.sample.DotsTextView;

/**
 * This class handles data fetching for searched query using pagination.
 *
 * Created by Abhinav Puri
 */
public class SearchDataFetchHandler {

    /**
     * Context for Search Grid Acitivity
     */
    Context context;

    private static String QUERY_STANDARD = "http://api.themoviedb.org/3/search/movie?language=en&include_adult=false&api_key=";
    /**
     * Paginated Data fetching class
     */
    SearchPaginatedData searchPaginatedData;

    /**
     * GridView for activity
     */
    GridView gridView;

    /**
     * Query Link
     */
    String dataFetchingRequestLink;

    /**
     * Override the default constructor
     *
     * @param context
     */
    public SearchDataFetchHandler(Context context, GridView gridView){

        this.context = context;
        searchPaginatedData = new SearchPaginatedData(this, context);
        this.gridView = gridView;

        // Update query with key :
        QUERY_STANDARD = QUERY_STANDARD + context.getResources().getString(R.string.key) + "&query=";
    }


    /**
     * This funtion requires query keyword which will be searched for online.
     *
     * @param searchQuery
     */
    public void startFetchingData(String searchQuery) {

        if (searchQuery == null || searchQuery.length() == 0) {

            // Hide the current grid view :
            gridView.setVisibility(View.INVISIBLE);

            // Time to play loading symbol :
            ((DotsTextView)((Activity)context).findViewById(R.id.dots_view)).showAndPlay();
            ((DotsTextView)((Activity)context).findViewById(R.id.dots_view)).setVisibility(View.VISIBLE);

            // Empty Adapter :
            ((SearchGridAdapter)gridView.getAdapter()).hardReset();

            return;
        }

        dataFetchingRequestLink = QUERY_STANDARD + searchQuery;

        searchPaginatedData.cancelAllRegisteredAsyncs();
        searchPaginatedData = new SearchPaginatedData(this, context);
        searchPaginatedData.startFetching(dataFetchingRequestLink);
    }

    /**
     * This function handles once data is ready to render
     * A callback function called from SearchPaginatedData class.
     *
     * @param dataFetched
     */
    public void dataReadyToRender(ArrayList<TheMovieDBObject> dataFetched, String query){

        if(!dataFetchingRequestLink.equals(query)){
            // Hide the current grid view :
            gridView.setVisibility(View.INVISIBLE);

            // Time to play loading symbol :
            ((DotsTextView)((Activity)context).findViewById(R.id.dots_view)).showAndPlay();
            ((DotsTextView)((Activity)context).findViewById(R.id.dots_view)).setVisibility(View.VISIBLE);

            // Empty Adapter :
            ((SearchGridAdapter)gridView.getAdapter()).hardReset();
            return;
        }

        if(dataFetched == null || dataFetched.size() == 0){
            ((Activity)context).findViewById(R.id.nothing_found_text_view).setVisibility(View.VISIBLE);
            return;
        }

        ((Activity)context).findViewById(R.id.nothing_found_text_view).setVisibility(View.INVISIBLE);

        // Time to stop loading symbol :
        ((DotsTextView)((Activity)context).findViewById(R.id.dots_view)).hideAndStop();
        ((DotsTextView)((Activity)context).findViewById(R.id.dots_view)).setVisibility(View.GONE);

        // Update grid adapter :
        ((SearchGridAdapter) gridView.getAdapter()).updateData(dataFetched);

        // Show the current grid view :
        gridView.setVisibility(View.VISIBLE);

    }
}
