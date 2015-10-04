package com.nanodegree.abhinav.titanfilms.favourite.daohandler;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.dao.FavouriteTable;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.favourite.activities.FavouriteMoviesGridActivity;
import com.nanodegree.abhinav.titanfilms.favourite.contentprovider.FavouriteContentProvider;
import com.nanodegree.abhinav.titanfilms.favourite.daohandler.FavouriteMoviesDataFetchHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class fetches data on per page basis from theMovieDb.com
 *
 * Created by Abhinav Puri.
 */
public class FavouriteMoviesPaginatedData {

    /**
     * List of TheMovieDBObject items.
     */
    private ArrayList<TheMovieDBObject> theMovieDBObjectArrayList;

    /**
     * Context of activity
     */
    private Context context;

    /**
     * Data fetching handler class.
     */
    private FavouriteMoviesDataFetchHandler favouriteMoviesDataFetchHandler;

    public FavouriteMoviesPaginatedData(Context context){
        theMovieDBObjectArrayList = new ArrayList<>();
        this.context = context;
    }

    public void setHandler(FavouriteMoviesDataFetchHandler favouriteMoviesDataFetchHandler){
        this.favouriteMoviesDataFetchHandler = favouriteMoviesDataFetchHandler;
    }

    public void fetchData(String request){

        //Clear data from this class.
        theMovieDBObjectArrayList.clear();
        // Need to parse data.
        parseData(request);
    }

    private void parseData(String request){

        // Need to fetch data from content provider :
        // Instead of JSONDataFetch .
        FetchFavourite fetchFavourite = new FetchFavourite(context);
        favouriteMoviesDataFetchHandler.dataReady(fetchFavourite.fetchData());

    }
}
