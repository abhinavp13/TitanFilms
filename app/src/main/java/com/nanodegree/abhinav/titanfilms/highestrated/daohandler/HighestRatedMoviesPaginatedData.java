package com.nanodegree.abhinav.titanfilms.highestrated.daohandler;

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
import com.nanodegree.abhinav.titanfilms.highestrated.daohandler.HighestRatedMoviesDataFetchHandler;

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
public class HighestRatedMoviesPaginatedData {

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
     * Request query.
     */
    private String request;

    /**
     * Context of activity.
     */
    private Context context;

    /**
     * Data fetching handler class.
     */
    private HighestRatedMoviesDataFetchHandler highestRatedMoviesDataFetchHandler;

    public HighestRatedMoviesPaginatedData(Context context){
        theMovieDBObjectArrayList = new ArrayList<>();
        this.context = context;
        REVIEWSPATH = REVIEWSPATH + context.getResources().getString(R.string.key);
        TRAILERPATH = TRAILERPATH + context.getResources().getString(R.string.key);
    }

    public void setHandler(HighestRatedMoviesDataFetchHandler highestRatedMoviesDataFetchHandler){
        this.highestRatedMoviesDataFetchHandler = highestRatedMoviesDataFetchHandler;
    }

    public void fetchData(String request){
        this.request = request;

        //Clear data from this class.
        theMovieDBObjectArrayList.clear();
        // Need to parse data.
        parseData(request);
    }

    private void parseData(String request){
        new JSONDataFetch().execute(request);
    }

    /**
     * Important Class for fetching data asynchronously.
     */
    private class JSONDataFetch extends AsyncTask<String,String,JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONArray doInBackground(String... params){
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
                return tokener.getJSONArray("results");


            }catch (UnknownHostException e){
                Log.d(context.getString(R.string.no_host_found),context.getString(R.string.may_be_no_internet_or_server_is_down));
                e.printStackTrace();
            } catch (MalformedURLException e){
                Log.d(context.getString(R.string.no_url_found), context.getString(R.string.wrong_url));
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(context.getString(R.string.cant_read_input), context.getString(R.string.unable_to_read_input));
                e.printStackTrace();
            } catch (JSONException e){
                Log.d(context.getString(R.string.cant_parse_input), context.getString(R.string.unable_to_parse_input));
                e.printStackTrace();
            }
            Log.d("Not Great !!!", "");
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                if(result==null) {
                    resultIsNull();
                    return;
                }
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);
                    TheMovieDBObject theMovieDBObject = new TheMovieDBObject();
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
            highestRatedMoviesDataFetchHandler.dataReady(theMovieDBObjectArrayList);

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                return tokener.getJSONArray("results");


            }catch (UnknownHostException e){
                Log.d(context.getString(R.string.no_host_found),context.getString(R.string.may_be_no_internet_or_server_is_down));
                e.printStackTrace();
            } catch (MalformedURLException e){
                Log.d(context.getString(R.string.no_url_found), context.getString(R.string.wrong_url));
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(context.getString(R.string.cant_read_input), context.getString(R.string.unable_to_read_input));
                e.printStackTrace();
            } catch (JSONException e){
                Log.d(context.getString(R.string.cant_parse_input), context.getString(R.string.unable_to_parse_input));
                e.printStackTrace();
            }
            Log.d("Not Great !!!", "");
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                if(result==null) {
                    resultIsNull();
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                return tokener.getJSONArray("results");


            }catch (UnknownHostException e){
                Log.d(context.getString(R.string.no_host_found),context.getString(R.string.may_be_no_internet_or_server_is_down));
                e.printStackTrace();
            } catch (MalformedURLException e){
                Log.d(context.getString(R.string.no_url_found), context.getString(R.string.wrong_url));
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(context.getString(R.string.cant_read_input), context.getString(R.string.unable_to_read_input));
                e.printStackTrace();
            } catch (JSONException e){
                Log.d(context.getString(R.string.cant_parse_input), context.getString(R.string.unable_to_parse_input));
                e.printStackTrace();
            }
            Log.d("Not Great !!!", "");
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                if(result==null) {
                    resultIsNull();
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
        fetchData(request);
    }


    /**
     * This function is called when result returned is null.
     * Some connection problem.
     */
    public void resultIsNull(){
        // Dialog for no internet connection :
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.unable_to_reach_hollywood));
        alertDialog.setMessage(context.getString(R.string.check_internet_connection));
        alertDialog.setCancelable(false);
        alertDialog.setButton2("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                context.startActivity(intent);
                HighestRatedMoviesGridActivity.loadedOnce = true;
                alertDialog.hide();
            }
        });
        alertDialog.setButton("Retry", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                retryRequired();
                alertDialog.hide();
            }
        });



        // TODO : SET ICON FOR DIALOG :

        //alertDialog.setIcon(R.drawable.ic_radio_white_36dp);


        alertDialog.show();
    }
}
