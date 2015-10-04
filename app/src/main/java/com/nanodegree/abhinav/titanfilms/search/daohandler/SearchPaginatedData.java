package com.nanodegree.abhinav.titanfilms.search.daohandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.highestrated.activities.HighestRatedMoviesGridActivity;

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
 * This class is used for fetching paginated data for a searched query.
 *
 * Created by Abhinav Puri
 */
public class SearchPaginatedData {

    /**
     * Search Data fetch handler class
     * Needed for callbacks when data is ready.
     */
    SearchDataFetchHandler searchDataFetchHandler;

    /**
     * Poster location url with size specified.
     */
    private final String POSTERPATHCONSTANT = "http://image.tmdb.org/t/p/w342";

    /**
     * Back Drop image location url.
     */
    private final String BACKDROPPATHCONSTANT = "http://image.tmdb.org/t/p/w780";

    /**
     * Reviews fetching path.
     */
    private String REVIEWSPATH = "http://api.themoviedb.org/3/movie/%d/reviews?api_key=";

    /**
     * Trailers fetching path.
     */
    private String TRAILERPATH = "http://api.themoviedb.org/3/movie/%d/videos?api_key=";

    /**
     * List of TheMovieDBObject items.
     */
    private ArrayList<TheMovieDBObject> theMovieDBObjectArrayList;

    /**
     * HTTP query
     */
    private String query;

    /**
     * Activity Context.
     */
    private Context context;

    /**
     * Dialog for no internet connection :
     */
    final AlertDialog alertDialog;

    /**
     * List of all registered asyncTasks.
     */
    ArrayList<AsyncTask> asyncTaskArrayList;

    /**
     * Checks whether class is active or inactive.
     */
    boolean inActive = false;

    /**
     * Overriding Default constructor
     *
     * @param searchDataFetchHandler
     */
    public SearchPaginatedData(SearchDataFetchHandler searchDataFetchHandler, Context context){
        this.searchDataFetchHandler = searchDataFetchHandler;
        theMovieDBObjectArrayList = new ArrayList<TheMovieDBObject>();
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
        REVIEWSPATH = REVIEWSPATH + context.getResources().getString(R.string.key);
        TRAILERPATH = TRAILERPATH + context.getResources().getString(R.string.key);
        asyncTaskArrayList = new ArrayList<>();
    }

    /**
     * This function makes asynchronous calls for fetching data for the given query.
     *
     * @param query
     */
    public void startFetching(String query){
        this.query = query;

        // Need to clear previously open async tasks :
        cancelAllRegisteredAsyncs();

        // Async Call :
        new JSONDataFetch().execute(query);
    }


    /**
     * Important Class for fetching data asynchronously.
     */
    private class JSONDataFetch extends AsyncTask<String,String,JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncTaskArrayList.add(this);
        }

        @Override
        protected JSONArray doInBackground(String... params){
            Log.d(" URL : ", params[0]);
            try {
                params[0] = params[0].trim();
                params[0] = params[0].replaceAll("\\s","%20");
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = in.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                JSONObject tokener = (JSONObject) new JSONTokener(builder.toString()).nextValue();
                Log.d(" Great !!!","");
                return tokener.getJSONArray("results");


            }catch (Exception e){
               // will Ask for retry, null needs to be returned.
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                if(result==null) {
                    searchDataFetchHandler.dataReadyToRender(new ArrayList<TheMovieDBObject>(),query);
                    resultIsNull();
                    return;
                }
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);
                    TheMovieDBObject theMovieDBObject = new TheMovieDBObject();

                    // lets skip, if data is too less :
                    if(jsonObject.getString("poster_path").equals("null") ||
                            jsonObject.getString("title").equals("null") ||
                            jsonObject.getString("vote_count").equals("null") ||
                            jsonObject.getString("vote_average").equals("null") ||
                            jsonObject.getString("backdrop_path").equals("null")){
                        continue;
                    }

                    theMovieDBObject.setTitle(jsonObject.getString("title"));
                    theMovieDBObject.setReleaseDate(jsonObject.getString("release_date"));
                    theMovieDBObject.setPlotSynopsis(jsonObject.getString("overview"));
                    theMovieDBObject.setId(jsonObject.getInt("id"));
                    theMovieDBObject.setPosterPath(POSTERPATHCONSTANT + jsonObject.getString("poster_path"));
                    theMovieDBObject.setBackdropPath(BACKDROPPATHCONSTANT + jsonObject.getString("backdrop_path"));
                    theMovieDBObject.setTrailerLinkFetchingPath(String.format(TRAILERPATH, theMovieDBObject.getId()));
                    theMovieDBObject.setReviewPath(String.format(REVIEWSPATH,theMovieDBObject.getId()));
                    theMovieDBObject.setVoteAverage(jsonObject.getString("vote_average"));
                    theMovieDBObject.setVoteCount(jsonObject.getString("vote_count"));
                    theMovieDBObject.setIsSavedAsFavourite(false);

                    // For Genre :
                    JSONArray jsonArray1 = (JSONArray)jsonObject.get("genre_ids");
                    int[] genreIds = new int[jsonArray1.length()];
                    for(int j=0;j< jsonArray1.length(); j++){
                        genreIds[j] = (Integer) jsonArray1.get(j);
                    }
                    theMovieDBObject.setGenreIds(genreIds);

                    theMovieDBObjectArrayList.add(theMovieDBObject);


                    if(isCancelled()){
                        break;
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }

            // Trailer Fetching :
            for(TheMovieDBObject theMovieDBObject : theMovieDBObjectArrayList) {
                new JSONTrailersFetch().execute(theMovieDBObject.getTrailerLinkFetchingPath(), String.valueOf(theMovieDBObject.getId()));
            }

            // Review Fetching :
            for(TheMovieDBObject theMovieDBObject : theMovieDBObjectArrayList) {
                new JSONReviewsFetch().execute(theMovieDBObject.getReviewPath(), String.valueOf(theMovieDBObject.getId()));
            }

            // Tell that data is ready :
            if(!isCancelled()) {
                searchDataFetchHandler.dataReadyToRender(theMovieDBObjectArrayList,query);
            }

        }
    }


    /**
     * Class for fetching Trailers
     * Usually Trailer is chosen based on the keyword "Official" in the trailer description.
     * But if keyword not present, the assumed trailer is the first trailer.
     *
     * @author Abhinav Puri
     */
    private class JSONTrailersFetch extends AsyncTask<String,String,JSONArray> {

        private int id = 0;

        private boolean noTrailerFound = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncTaskArrayList.add(this);
        }

        @Override
        protected JSONArray doInBackground(String... params){
            id = Integer.parseInt(params[1]);
            Log.d(" URL : ", params[0]);
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = in.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                JSONObject tokener = (JSONObject) new JSONTokener(builder.toString()).nextValue();
                Log.d(" Great !!!","");

                if(tokener.getJSONArray("results") == null){
                    noTrailerFound = true;
                }
                return tokener.getJSONArray("results");


            }catch (Exception e){
                // Will ask for retry.
                // Returned null.
                e.printStackTrace();
                System.out.println();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                if(result==null && !noTrailerFound) {
                    resultIsNull();
                    return;
                }

                if(result == null  && noTrailerFound){
                    return;
                }

                String trailerChosen = "";

                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);

                    if(jsonObject.getString("name").toLowerCase().contains("Official".toLowerCase()) || i == 0){
                        trailerChosen = "https://www.youtube.com/watch?v=" + jsonObject.getString("key");
                    }
                }

                for(TheMovieDBObject theMovieDBObject: theMovieDBObjectArrayList){
                    if(theMovieDBObject.getId() == id){
                        theMovieDBObject.setTrailerLink(trailerChosen);
                        break;
                    }
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

    /**
     * Class for fetching reviews.
     * All reviews are fetched and saved.
     * (Can break for very large and too many reviews.)
     *
     * @author Abhinav Puri
     */
    private class JSONReviewsFetch extends AsyncTask<String,String,JSONArray> {

        private int id = 0;

        private boolean noReviewFound = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncTaskArrayList.add(this);
        }

        @Override
        protected JSONArray doInBackground(String... params){
            id = Integer.parseInt(params[1]);
            Log.d(" URL : ", params[0]);
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = in.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                JSONObject tokener = (JSONObject) new JSONTokener(builder.toString()).nextValue();
                Log.d(" Great !!!","");
                if(tokener.getJSONArray("results") == null){
                    noReviewFound = true;
                }
                return tokener.getJSONArray("results");


            }catch (Exception e){
                // Only can see logs for something went wrong.
                // Will ask user to retry.
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                if(result==null && !noReviewFound) {
                    resultIsNull();
                    return;
                }

                if(result == null && noReviewFound){
                    return;
                }

                TheMovieDBObject requiredObject = null;
                for(TheMovieDBObject theMovieDBObject: theMovieDBObjectArrayList){
                    if(theMovieDBObject.getId() == id){
                        requiredObject = theMovieDBObject;
                    }
                }


                HashMap<String,String> requiredHashMap = new HashMap<>();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);
                    requiredHashMap.put(jsonObject.getString("author"),jsonObject.getString("content"));
                }

                if(requiredObject != null){
                    requiredObject.setReviews(requiredHashMap);
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

        }
    }


    // Captures Retry pressed by user
    public void retryRequired(){
        startFetching(query);
    }

    /**
     * This function is called when result returned is null.
     * Some connection problem.
     */
    public void resultIsNull(){
        if(alertDialog.isShowing()){
            return;
        }
        if(inActive){
            return;
        }
    }


    /**
     * This function removes all the registered Async calls
     * It should be called only when new request appears.
     */
    public void cancelAllRegisteredAsyncs(){

        if(asyncTaskArrayList.size() == 0){
            return;
        }

        for(AsyncTask asyncTask : asyncTaskArrayList){
            if(asyncTask.isCancelled()){
                asyncTask.cancel(true);
            }
        }

        theMovieDBObjectArrayList.clear();
        inActive = true;
    }
}
