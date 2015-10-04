package com.nanodegree.abhinav.titanfilms.highestrated.fragments;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.favourite.daohandler.DeleteFavourite;
import com.nanodegree.abhinav.titanfilms.favourite.daohandler.SaveFavourite;
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

import pl.tajchert.sample.DotsTextView;

/**
 * This Fragment is only used in case of large screen devices
 * and that too only in portrait mode.
 *
 * Created by Abhinav Puri.
 */
public class TheHighestRatedDetailsFragment extends Fragment {

    /**
     * RootView for this Fragment.
     */
    private View rootView;

    /**
     * This is application data saved data.
     */
    private ApplicationSavedData applicationSavedData;

    /**
     * This is the currently selected movie data object.
     */
    private TheMovieDBObject currentTheMovieDBObjectSelected;

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
     * Toast for this fragment.
     */
    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.highest_rated_movies_detail_fragment,container,false);
        refresh(false);
        return rootView;
    }

    public void refresh(boolean hardRefresh){
        UIInitialize(hardRefresh);
    }

    /**
     * Used for startup initializations.
     */
    private void UIInitialize(boolean hardRefresh){

        // Check whether fragment is created yet or not :
        if(rootView == null)
            return;

        final ScrollView scrollView = (ScrollView)rootView.findViewById(R.id.scrollView);
        final DotsTextView dotsTextView = (DotsTextView)rootView.findViewById(R.id.dots_for_fragment);
        if(!hardRefresh){
            dotsTextView.showAndPlay();
            return;
        }

        // Check whether activity is attached or not :
        if(getActivity() == null)
            return;

        // Saved Data :
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) getActivity().getApplication());

        if(applicationSavedData == null)
            return;

        // Current Object :
        currentTheMovieDBObjectSelected = applicationSavedData.getCurrentSelectedForHighestRated();

        // Will surely break, if data is null :
        if(currentTheMovieDBObjectSelected == null){
            return;
        }

        // Empty data come in :
        if(currentTheMovieDBObjectSelected.getTitle() == null)
            return;

        // Changing the visibility for showing actual movie view :
        scrollView.setVisibility(View.VISIBLE);
        dotsTextView.setVisibility(View.GONE);

        // Capturing Main Backdrop image, it was saved as bitmap in main application :
        TitanFilmsApplication titanFilmsApplication = (TitanFilmsApplication) getActivity().getApplication();
        Drawable bitmap = titanFilmsApplication.bitmap;
        ImageView im  = (ImageView)rootView.findViewById(R.id.movie_image_view);
        im.setImageDrawable(bitmap);

        TFUtils.setRobotoLight((TextView) rootView.findViewById(R.id.popularity_tag), getActivity().getAssets());
        TFUtils.setRobotoLight((TextView)rootView.findViewById(R.id.plot_tag),getActivity().getAssets());
        TFUtils.setRobotoLight((TextView)rootView.findViewById(R.id.genre_tag),getActivity().getAssets());

        isShownFav = true;
        loadedOnce = false;

        // Set Status Bar color :
        TFUtils.setStatusBarColor(getActivity().getWindow(), getResources().getColor(R.color.dark_blue));

        // Set the text View font :
        TFUtils.setRobotoLight((TextView) rootView.findViewById(R.id.title_of_movie), getActivity().getAssets());

        // Set Backdrop image :
        loadBackdropImage();

        // Set movie_trailer Image (in case not set during transition) :
        setMovieImage();

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
            PopulateGenre populateGenre = new PopulateGenre(getActivity());
            populateGenre.populateGenreGivenIds(currentTheMovieDBObjectSelected.getGenreIds());
        }

        // Setting up favourite as not selected by default :
        {
            if(!applicationSavedData.checkForAvailabilityInFavouriteMovies(currentTheMovieDBObjectSelected)) {
                TextView tv = (TextView) rootView.findViewById(R.id.text_view_favourite);
                tv.setVisibility(View.VISIBLE);

                View saveView = (View) rootView.findViewById(R.id.save_view);

                // Scaling save View :
                float factor = 0;
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(saveView, "scaleX", factor);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(saveView, "scaleY", factor);
                scaleDownX.setDuration(10);
                scaleDownY.setDuration(10);
                scaleDownX.start();
                scaleDownY.start();

                RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar1);
                ratingBar.setRating(0);
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(ratingBar, "scaleX", 2f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(ratingBar, "scaleY", 2f);
                scaleUpX.setDuration(10);
                scaleUpY.setDuration(10);
                scaleUpX.start();
                scaleUpY.start();

                isShownFav = false;
            } else {
                TextView tv = (TextView)rootView.findViewById(R.id.text_view_favourite);
                tv.setVisibility(View.INVISIBLE);

                View saveView = (View)rootView.findViewById(R.id.save_view);

                // Scaling save View :
                float factor = 1;
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(saveView, "scaleX", factor);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(saveView, "scaleY",factor);
                scaleUpX.setDuration(10);
                scaleUpY.setDuration(10);
                scaleUpX.start();
                scaleUpY.start();

                RatingBar ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar1);
                ratingBar.setRating(1);
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ratingBar,"scaleX",1f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ratingBar,"scaleY",1f);
                scaleDownX.setDuration(10);
                scaleDownY.setDuration(10);
                scaleDownX.start();
                scaleDownY.start();

                isShownFav = true;
            }
        }

        // Trailer listener :
        {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentTheMovieDBObjectSelected.getTrailerLink().equals("") || currentTheMovieDBObjectSelected.getTrailerLink()==null){
                        new TrailerFetchAgain().execute(currentTheMovieDBObjectSelected.getTrailerLinkFetchingPath(),String.valueOf(currentTheMovieDBObjectSelected.getId()));
                        // Showing Save Toast :
                        if(getActivity() != null) {
                            if (toast == null) {
                                toast = Toast.makeText(getActivity(), "Downloading Trailer, Try Again Later", Toast.LENGTH_SHORT);
                            } else {
                                toast.cancel();
                                toast = Toast.makeText(getActivity(), "Downloading Trailer, Try Again Later", Toast.LENGTH_SHORT);
                            }
                            toast.show();
                        }
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

            ((View)rootView.findViewById(R.id.trailer_play_view)).setOnClickListener(onClickListener);
        }
    }


    /**
     * This function sets backdrop image.
     */
    private void loadBackdropImage(){
        ImageView imageView = (ImageView)rootView.findViewById(R.id.backdrop_image_view);
        Picasso.with(getActivity()).load(currentTheMovieDBObjectSelected.getBackdropPath()).into(imageView);
    }

    /**
     * Sets the movie image view.
     */
    private void setMovieImage(){
        Picasso.with(getActivity()).load(currentTheMovieDBObjectSelected.getPosterPath()).into((ImageView) rootView.findViewById(R.id.movie_image_view));
    }

    /**
     * This function sets the title for movie_trailer.
     */
    private void setTitle(){
        TextView tv = (TextView) rootView.findViewById(R.id.title_of_movie);
        tv.setText(currentTheMovieDBObjectSelected.getTitle());
    }

    /**
     * This function sets the reviews for the currently selected movie.
     */
    private void setReviews(){
        if(rootView == null || getActivity() == null){
            return;
        }

        HashMap<String,String> reviewsMap = currentTheMovieDBObjectSelected.getReviews();

        TextView tv = (TextView)rootView.findViewById(R.id.reviews_text_view);
        View v = rootView.findViewById(R.id.reviews_line);
        v.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);

        if(reviewsMap==null){
            // Try to fetch again... missed out previously.
            // Review Fetching :
            new reviewFetchAgain().execute(currentTheMovieDBObjectSelected.getReviewPath(),String.valueOf(currentTheMovieDBObjectSelected.getId()));
            isFetchedReviews = true;
            return;
        }
        if(!reviewsMap.entrySet().iterator().hasNext()){

            if(!isFetchedReviews) {
                // Try to fetch again... missed out previously.
                // Review Fetching :
                new reviewFetchAgain().execute(currentTheMovieDBObjectSelected.getReviewPath(), String.valueOf(currentTheMovieDBObjectSelected.getId()));
                isFetchedReviews = true;
            }
            return;
        }

        tv.setVisibility(View.VISIBLE);
        v.setVisibility(View.VISIBLE);
        TFUtils.setRobotoLight(tv,getActivity().getAssets());

        int i = 1;
        Iterator it = reviewsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            int authorR = getResources().getIdentifier("author"+i,"id",getActivity().getPackageName());
            TextView authorTextView = (TextView)rootView.findViewById(authorR);
            authorTextView.setText((String)pair.getKey());

            int contentR = getResources().getIdentifier("content"+i,"id",getActivity().getPackageName());
            TextView contentTextView = (TextView)rootView.findViewById(contentR);
            contentTextView.setText((String)pair.getValue());

            int viewR = getResources().getIdentifier("gap"+i,"id",getActivity().getPackageName());
            View gap = rootView.findViewById(viewR);

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
        ((TextView)rootView.findViewById(R.id.release_date)).setText(currentTheMovieDBObjectSelected.getReleaseDate());
        TFUtils.setRobotoLightItalic((TextView) rootView.findViewById(R.id.release_date), getActivity().getAssets());
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
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = in.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                JSONObject tokener = (JSONObject) new JSONTokener(builder.toString()).nextValue();
                Log.d(" Great !!!", "");
                return tokener.getJSONArray("results");


            }catch (UnknownHostException e){
                Log.d(getActivity().getString(R.string.no_host_found),getActivity().getString(R.string.may_be_no_internet_or_server_is_down));
                e.printStackTrace();
            } catch (MalformedURLException e){
                Log.d(getActivity().getString(R.string.no_url_found), getActivity().getString(R.string.wrong_url));
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(getActivity().getString(R.string.cant_read_input), getActivity().getString(R.string.unable_to_read_input));
                e.printStackTrace();
            } catch (JSONException e){
                Log.d(getActivity().getString(R.string.cant_parse_input), getActivity().getString(R.string.unable_to_parse_input));
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
                Log.d(getActivity().getString(R.string.no_host_found),getActivity().getString(R.string.may_be_no_internet_or_server_is_down));
                e.printStackTrace();
            } catch (MalformedURLException e){
                Log.d(getActivity().getString(R.string.no_url_found), getActivity().getString(R.string.wrong_url));
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(getActivity().getString(R.string.cant_read_input), getActivity().getString(R.string.unable_to_read_input));
                e.printStackTrace();
            } catch (JSONException e){
                Log.d(getActivity().getString(R.string.cant_parse_input), getActivity().getString(R.string.unable_to_parse_input));
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
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getActivity().getString(R.string.unable_to_reach_hollywood));
        alertDialog.setMessage(getActivity().getString(R.string.check_internet_connection));
        alertDialog.setCancelable(false);
        alertDialog.setButton2("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                getActivity().startActivity(intent);
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
     * This function sets the voting average for this movie.
     */
    public void setVoteAverage(){
        TextView voteAverage = (TextView)rootView.findViewById(R.id.vote_average);
        voteAverage.setText(currentTheMovieDBObjectSelected.getVoteAverage());

        RatingBar ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        if(currentTheMovieDBObjectSelected.getVoteAverage()!=null) {
            ratingBar.setRating(Float.parseFloat(currentTheMovieDBObjectSelected.getVoteAverage()) / 2.0f);
        }
        ratingBar.setStepSize(0.1f);
    }

    /**
     * This function sets the vote count for this movie.
     */
    public void setVoteCount(){
        TextView voteCount = (TextView)rootView.findViewById(R.id.vote_count);
        voteCount.setText(currentTheMovieDBObjectSelected.getVoteCount());
    }

    /**
     * This function sets the plot for the movie.
     */
    public void setPlot(){
        TextView plot = (TextView)rootView.findViewById(R.id.plot);
        plot.setText(currentTheMovieDBObjectSelected.getPlotSynopsis());
    }

    /**
     * Function called when favourite button is clicked.
     * @param v
     */
    public void favClicked(View v){

        // Fetch poster Image :
        ImageView im  = (ImageView) getActivity().findViewById(R.id.movie_image_view);
        Bitmap bitmapPosterImage = ((BitmapDrawable)im.getDrawable()).getBitmap();

        //Fetch Backdrop image :
        ImageView imbackdrop  = (ImageView)getActivity().findViewById(R.id.backdrop_image_view);
        Bitmap bitmapBackdropImage = ((BitmapDrawable)imbackdrop.getDrawable()).getBitmap();


        if(isShownFav) {
            hideForFav();
            isShownFav = false;
            currentTheMovieDBObjectSelected.setIsSavedAsFavourite(false);

            // Deleting from android device :
            DeleteFavourite deleteFavourite = new DeleteFavourite(getActivity());
            deleteFavourite.deleteData(currentTheMovieDBObjectSelected);

            // Important for saving Info :
            applicationSavedData.deleteFavouriteMovies(currentTheMovieDBObjectSelected);

        } else {
            showForFav();

            // Showing Save Toast :
            if(getActivity() != null) {
                if (toast == null) {
                    toast = Toast.makeText(getActivity(), "Favourite Saved", Toast.LENGTH_SHORT);
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getActivity(), "Favourite Saved", Toast.LENGTH_SHORT);
                }
                toast.show();
            }

            // Persisting it in local android device
            SaveFavourite saveFavourite = new SaveFavourite(getActivity());
            saveFavourite.saveDataFromDBObject(currentTheMovieDBObjectSelected, bitmapPosterImage, bitmapBackdropImage);

            // Important for saving Info :
            applicationSavedData.appendFavouriteMovies(currentTheMovieDBObjectSelected);

            currentTheMovieDBObjectSelected.setIsSavedAsFavourite(true);
            isShownFav = true;
        }
    }

    /**
     * Used by favClicked for hiding main favourite icon.
     */
    public void hideForFav(){
        TextView tv = (TextView)rootView.findViewById(R.id.text_view_favourite);
        tv.setVisibility(View.VISIBLE);

        View saveView = (View)rootView.findViewById(R.id.save_view);

        // Scaling save View :
        float factor = 0;
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(saveView, "scaleX", factor);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(saveView, "scaleY",factor);
        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);
        scaleDownX.start();
        scaleDownY.start();

        RatingBar ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar1);
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
        TextView tv = (TextView)rootView.findViewById(R.id.text_view_favourite);
        tv.setVisibility(View.INVISIBLE);

        View saveView = (View)rootView.findViewById(R.id.save_view);

        // Scaling save View :
        float factor = 1;
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(saveView, "scaleX", factor);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(saveView, "scaleY",factor);
        scaleUpX.setDuration(500);
        scaleUpY.setDuration(500);
        scaleUpX.start();
        scaleUpY.start();

        RatingBar ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar1);
        ratingBar.setRating(1);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ratingBar,"scaleX",1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ratingBar,"scaleY",1f);
        scaleDownX.setDuration(500);
        scaleDownY.setDuration(500);
        scaleDownX.start();
        scaleDownY.start();
    }
}
