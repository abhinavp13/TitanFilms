package com.nanodegree.abhinav.titanfilms.favourite.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.tfutils.PopulateGenre;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;
import com.squareup.picasso.Picasso;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FavouriteMoviesDetailActivity extends Activity {

    /**
     * This is application data saved data.
     */
    private ApplicationSavedData applicationSavedData;

    /**
     * This is the currently selected movie data object.
     */
    private TheMovieDBObject currentTheMovieDBObjectSelected;

    /**
     * Context for this activity.
     */
    private Context context;

    /**
     * Checking for fetching of reviews once.
     */
    private boolean isFetchedReviews;

    /**
     * Boolean value for loading done once.
     */
    private boolean loadedOnce;

    /**
     * Boolean value for favourite icon shown or not
     */
    private boolean isShownFav;

    /**
     * Toast for this activity
     */
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies_detail);

        // No need for this activity if orientation is landscape :
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            this.finish();
        }
        context = FavouriteMoviesDetailActivity.this;

        TFUtils.setRobotoLight((TextView) findViewById(R.id.popularity_tag), getAssets());
        TFUtils.setRobotoLight((TextView)findViewById(R.id.plot_tag),getAssets());
        TFUtils.setRobotoLight((TextView)findViewById(R.id.genre_tag),getAssets());

        isShownFav = true;
        loadedOnce = false;

        // Set Status Bar color :
        TFUtils.setStatusBarColor(getWindow(), getResources().getColor(R.color.dark_blue));

        // Set the text View font :
        TFUtils.setRobotoLight((TextView) findViewById(R.id.title_of_movie), getAssets());

        // Saved Data :
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) getApplication());

        // Current Object :
        currentTheMovieDBObjectSelected = applicationSavedData.getCurrentSelectedForFavourite();

        // Poster Image :
        ImageView im  = (ImageView)findViewById(R.id.movie_image_view);
        im.setImageBitmap(currentTheMovieDBObjectSelected.getBitmapPosterImage());


        // Set Backdrop image :
        ImageView imageView = (ImageView)findViewById(R.id.backdrop_image_view);
        imageView.setImageBitmap(currentTheMovieDBObjectSelected.getBitmapBackdropImage());

        // Set Title :
        setTitle();

        // Check for Fetching reviews once.
        isFetchedReviews = false;

        // Set Reviews ;
        setReviews();

        // Set Trailer Link :
        setTrailer();

        // Add Release Data.
        setReleaseDate();

        // Defining vote average for view :
        setVoteAverage();

        // Defining vote count for view :
        setVoteCount();

        // Defining plot for view :
        setPlot();

        // Check for genreId :
        if(currentTheMovieDBObjectSelected.getGenreIds() != null && currentTheMovieDBObjectSelected.getGenreIds().length > 0){
            PopulateGenre populateGenre = new PopulateGenre(context);
            populateGenre.populateGenreGivenIds(currentTheMovieDBObjectSelected.getGenreIds());
        }


        // Trailer listener :
        {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentTheMovieDBObjectSelected.getTrailerLink() == null || currentTheMovieDBObjectSelected.getTrailerLink().equals("")){

                        // Trailer loading Toast :
                        if (toast == null) {
                            toast = Toast.makeText(FavouriteMoviesDetailActivity.this, "Fetching Trailer, Try Again Later", Toast.LENGTH_SHORT);
                        } else {
                            toast.cancel();
                            toast = Toast.makeText(FavouriteMoviesDetailActivity.this, "Fetching Trailer, Try Again Later", Toast.LENGTH_SHORT);
                        }
                        toast.show();
                        return;
                    }
                    String id = currentTheMovieDBObjectSelected.getTrailerLink();
                    id = id.substring(id.lastIndexOf("=") + 1);


                    // Intent to play Youtube videos, fallback to browser if youtube app not present
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + id));
                        startActivity(intent);
                    }
                }
            };

            ((View)findViewById(R.id.trailer_play_view)).setOnClickListener(onClickListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(loadedOnce) {
            // Set Backdrop image :
            loadBackdropImage();

            // Set movie_trailer Image (in case not set during transition) :
            setMovieImage();

            // Set Title :
            setTitle();

            setTrailer(); setReviews();
            loadedOnce = false;
        }
    }

    /**
     * This function sets backdrop image.
     */
    private void loadBackdropImage(){
        ImageView imageView = (ImageView)findViewById(R.id.backdrop_image_view);
        Picasso.with(FavouriteMoviesDetailActivity.this).load(currentTheMovieDBObjectSelected.getBackdropPath()).into(imageView);
    }

    /**
     * Sets the movie image view.
     */
    private void setMovieImage(){
        Picasso.with(context).load(currentTheMovieDBObjectSelected.getPosterPath()).into((ImageView) findViewById(R.id.movie_image_view));
    }

    /**
     * This function sets the title for movie_trailer.
     */
    private void setTitle(){
        TextView tv = (TextView) findViewById(R.id.title_of_movie);
        tv.setText(currentTheMovieDBObjectSelected.getTitle());
    }

    /**
     * This function sets the reviews for the currently selected movie.
     */
    private void setReviews(){
        HashMap<String,String> reviewsMap = currentTheMovieDBObjectSelected.getReviews();

        TextView tv = (TextView)findViewById(R.id.reviews_text_view);
        View v = findViewById(R.id.reviews_line);
        v.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);

        if(!reviewsMap.entrySet().iterator().hasNext()){
            return;
        }

        tv.setVisibility(View.VISIBLE);
        v.setVisibility(View.VISIBLE);
        TFUtils.setRobotoLight(tv,getAssets());

        int i = 1;
        Iterator it = reviewsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            int authorR = getResources().getIdentifier("author"+i,"id",getPackageName());
            TextView authorTextView = (TextView)findViewById(authorR);
            authorTextView.setText((String)pair.getKey());

            int contentR = getResources().getIdentifier("content"+i,"id",getPackageName());
            TextView contentTextView = (TextView)findViewById(contentR);
            contentTextView.setText((String)pair.getValue());

            int viewR = getResources().getIdentifier("gap"+i,"id",getPackageName());
            View gap = findViewById(viewR);

            contentTextView.setVisibility(View.VISIBLE);
            authorTextView.setVisibility(View.VISIBLE);
            gap.setVisibility(View.VISIBLE);

            i++;

            if(i>10) {
                break;
            }

            it.remove();
        }

    }

    /**
     * This function set the trailer link for the movie selected.
     */
    private void setTrailer() { }

    /**
     * This function sets the release date along with its font.
     */
    public void setReleaseDate(){
        ((TextView)findViewById(R.id.release_date)).setText(currentTheMovieDBObjectSelected.getReleaseDate());
        TFUtils.setRobotoLightItalic((TextView) findViewById(R.id.release_date), getAssets());
    }


    /**
     * Class for fetching reviews.
     * All reviews are fetched and saved.
     * (Can break for very large and too many reviews.)
     *
     * @author Abhinav Puri
     */
    private class reviewFetchAgain extends AsyncTask<String,String,JSONArray> {

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

                TheMovieDBObject requiredObject = currentTheMovieDBObjectSelected;

                HashMap<String,String> requiredHashMap = new HashMap<>();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);
                    requiredHashMap.put(jsonObject.getString("author"),jsonObject.getString("content"));
                }

                if(requiredObject != null){
                    requiredObject.setReviews(requiredHashMap);
                }

                Log.d("Fetched from reviews ","aslkfal;kj");

                setReviews();

            }catch(JSONException e){
                e.printStackTrace();
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
    private class TrailerFetchAgain extends AsyncTask<String,String,JSONArray> {

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

                Log.d("Fetched from trialer","aslkfal;kj");
                currentTheMovieDBObjectSelected.setTrailerLink(trailerChosen);

                setTrailer();

            }catch(JSONException e){
                e.printStackTrace();
            }

        }
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
                loadedOnce = true;
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

        alertDialog.show();
    }

    // Captures Retry pressed by user
    public void retryRequired(){
        setTrailer(); setReviews();
    }

    /**
     * Share button clicked.
     * @param v
     */
    public void shareClicked(View v){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, currentTheMovieDBObjectSelected.getTrailerLink());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * Back button clicked.
     * @param v
     */
    public void backClicked(View v){
        FavouriteMoviesDetailActivity.this.onBackPressed();
    }

    /**
     * This function sets the voting average for this movie.
     */
    public void setVoteAverage(){
        TextView voteAverage = (TextView)findViewById(R.id.vote_average);
        voteAverage.setText(currentTheMovieDBObjectSelected.getVoteAverage());

        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        ratingBar.setRating(Float.parseFloat(currentTheMovieDBObjectSelected.getVoteAverage()) / 2.0f);
        ratingBar.setStepSize(0.1f);
    }

    /**
     * This function sets the vote count for this movie.
     */
    public void setVoteCount(){
        TextView voteCount = (TextView)findViewById(R.id.vote_count);
        voteCount.setText(currentTheMovieDBObjectSelected.getVoteCount());
    }

    /**
     * This function sets the plot for the movie.
     */
    public void setPlot(){
        TextView plot = (TextView)findViewById(R.id.plot);
        plot.setText(currentTheMovieDBObjectSelected.getPlotSynopsis());
    }

    /**
     * Function called when favourite button is clicked.
     * @param v
     */
    public void favClicked(View v){



        //TODO : Persisting it in local android device....


        if(isShownFav) {
            hideForFav();
            isShownFav = false;
            currentTheMovieDBObjectSelected.setIsSavedAsFavourite(false);
        } else {
            showForFav();

            // Showing Save Toast :
            if (toast == null) {
                toast = Toast.makeText(FavouriteMoviesDetailActivity.this, "Favourite Saved", Toast.LENGTH_SHORT);
            } else {
                toast.cancel();
                toast = Toast.makeText(FavouriteMoviesDetailActivity.this, "Favourite Saved", Toast.LENGTH_SHORT);
            }
            toast.show();

            currentTheMovieDBObjectSelected.setIsSavedAsFavourite(true);
            isShownFav = true;
        }
    }

    /**
     * Used by favClicked for hiding main favourite icon.
     */
    public void hideForFav(){
        TextView tv = (TextView)findViewById(R.id.text_view_favourite);
        tv.setVisibility(View.VISIBLE);

        View saveView = (View)findViewById(R.id.save_view);

        // Scaling save View :
        float factor = 0;
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(saveView, "scaleX", factor);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(saveView, "scaleY",factor);
        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);
        scaleDownX.start();
        scaleDownY.start();

        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar1);
        ratingBar.setRating(0);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(ratingBar,"scaleX",2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(ratingBar,"scaleY",2f);
        scaleUpX.setDuration(500);
        scaleUpY.setDuration(500);
        scaleUpX.start();
        scaleUpY.start();
    }

    /**
     * Used by favClicked for showing main favourite icon.
     */
    public void showForFav(){
        TextView tv = (TextView)findViewById(R.id.text_view_favourite);
        tv.setVisibility(View.INVISIBLE);

        View saveView = (View)findViewById(R.id.save_view);

        // Scaling save View :
        float factor = 1;
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(saveView, "scaleX", factor);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(saveView, "scaleY",factor);
        scaleUpX.setDuration(500);
        scaleUpY.setDuration(500);
        scaleUpX.start();
        scaleUpY.start();

        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar1);
        ratingBar.setRating(1);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ratingBar,"scaleX",1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ratingBar,"scaleY",1f);
        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);
        scaleDownX.start();
        scaleDownY.start();
    }
}

