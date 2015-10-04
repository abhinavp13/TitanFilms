package com.nanodegree.abhinav.titanfilms.tfutils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.favourite.activities.FavouriteMoviesGridActivity;
import com.nanodegree.abhinav.titanfilms.highestrated.activities.HighestRatedMoviesGridActivity;
import com.nanodegree.abhinav.titanfilms.mostpopular.activities.MostPopularMoviesGridActivity;

/**
 * This class is used to change the activity from option menu.
 *
 * Created by Abhinav Puri
 */
public class ActivitySelectChanges implements View.OnClickListener{

    /**
     * Activity Context
     */
    Context context;

    /**
     * Constructor for setting context.
     *
     * @param context
     */
    public ActivitySelectChanges(Context context){
        this.context = context;
    }


    @Override
    public void onClick(final View v) {

        // This click event should only be captured for text view or image view.
        if(!(v instanceof TextView) && !(v instanceof ImageView)){
            return;
        }

        if(v instanceof ImageView){
            changeAcitivityComingFromMainMenu((ImageView)v);
            return;
        }

        // color Change :
        changeColor((TextView)v);

        // Need to change activity after some time :
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mapping((TextView)v) !=null) {
                    Intent intent = new Intent(context, mapping((TextView)v));
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)context, v, "titleView");
                    context.startActivity(intent, options.toBundle());
                }

                // Handler required for Waiting animations to complete :
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity)context).finish();
                    }
                },1000);
            }
        },100);

    }

    /**
     * Function maps corresponding activity class name with text view given to it.
     * Mapping is based on first character in the textView's text string.
     *
     * @param v
     * @return activity class.
     */
    private Class<?> mapping(TextView v){
        switch (v.getText().toString().toUpperCase().charAt(1)){
            case 'O':
                return MostPopularMoviesGridActivity.class;
            case 'I':
                return HighestRatedMoviesGridActivity.class;
            case 'Y':
                return FavouriteMoviesGridActivity.class;
        }
        return null;
    }

    /**
     * Change the color for text selected.
     * @param v
     */
    private void changeColor(TextView v){

        if(v.getId() == R.id.most_popular_option_text){

            v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            v.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            ((TextView)((Activity)context).findViewById(R.id.highest_rating_option_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ((TextView)((Activity)context).findViewById(R.id.highest_rating_option_text)).setTextColor(((Activity)context).getResources().getColor(android.R.color.black));
            ((TextView)((Activity)context).findViewById(R.id.favourite_movies_option_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ((TextView)((Activity)context).findViewById(R.id.favourite_movies_option_text)).setTextColor(((Activity)context).getResources().getColor(android.R.color.black));


        } else if (v.getId() == R.id.highest_rating_option_text){

            v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            v.setTextColor(((Activity)context).getResources().getColor(android.R.color.holo_blue_dark));
            ((TextView)((Activity)context).findViewById(R.id.most_popular_option_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ((TextView)((Activity)context).findViewById(R.id.most_popular_option_text)).setTextColor(((Activity)context).getResources().getColor(android.R.color.black));
            ((TextView)((Activity)context).findViewById(R.id.favourite_movies_option_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ((TextView)((Activity)context).findViewById(R.id.favourite_movies_option_text)).setTextColor(((Activity)context).getResources().getColor(android.R.color.black));

        } else {

            v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            v.setTextColor(((Activity)context).getResources().getColor(android.R.color.holo_blue_dark));
            ((TextView)((Activity)context).findViewById(R.id.most_popular_option_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ((TextView)((Activity)context).findViewById(R.id.most_popular_option_text)).setTextColor(((Activity)context).getResources().getColor(android.R.color.black));
            ((TextView)((Activity)context).findViewById(R.id.highest_rating_option_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ((TextView)((Activity)context).findViewById(R.id.highest_rating_option_text)).setTextColor(((Activity)context).getResources().getColor(android.R.color.black));

        }
    }

    /**
     * This is used for menu options appearing at the beginning
     * of the app. It has image views which can be mapped to
     * corresponding activity classes.
     *
     * @param v
     * @return corresponding activity class
     */
    private Class<?> mappingForImageView(ImageView v){

        if(v.getId() == R.id.imageView1){
            return MostPopularMoviesGridActivity.class;
        } else if (v.getId() == R.id.imageView2){
            return HighestRatedMoviesGridActivity.class;
        } else {
            return FavouriteMoviesGridActivity.class;
        }
    }

    private void changeAcitivityComingFromMainMenu(final ImageView v){
        // Need to change activity after some time :
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mappingForImageView(v) !=null) {
                    Intent intent = new Intent(context, mappingForImageView(v));
                    context.startActivity(intent);
                }

            }
        },100);
    }


}
