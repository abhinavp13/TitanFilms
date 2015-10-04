package com.nanodegree.abhinav.titanfilms.highestrated.activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.highestrated.fragments.TheHighestRatedDetailsFragment;
import com.nanodegree.abhinav.titanfilms.highestrated.fragments.TheHighestRatedMoviesFragment;
import com.nanodegree.abhinav.titanfilms.tfutils.ActivitySelectChanges;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

public class HighestRatedMoviesGridActivity extends Activity {

    /**
     * boolean for keeping loaded once.
     */
    public static boolean loadedOnce = false;

    /**
     * boolean for option menu
     */
    boolean isItReturnTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting Content View :
        setContentView(R.layout.activity_highest_rated_movies_grid);

        // Title Text :
        ((TextView)findViewById(R.id.title)).setText("Highest Rated");

        // Setting Status Bar :
        TFUtils.setStatusBarColor(getWindow(), getResources().getColor(R.color.dark_blue));

        // Setting font for Title View :
        TFUtils.setRobotoLight((TextView) findViewById(R.id.title), getAssets());

        // Some Option Font Changes :
        TFUtils.setRobotoLight((TextView) findViewById(R.id.most_popular_option_text), getAssets());
        TFUtils.setRobotoLight((TextView) findViewById(R.id.highest_rating_option_text), getAssets());
        TFUtils.setRobotoLight((TextView) findViewById(R.id.favourite_movies_option_text), getAssets());
        TFUtils.setRobotoLight((TextView) findViewById(R.id.selection_ride_text_view),getAssets());

        // It wont do anything if nothing is selected or rootview is null, or fragment not created yet.
        TheHighestRatedDetailsFragment fragment = (TheHighestRatedDetailsFragment)getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2);
        if(fragment!=null) {
            fragment.refresh(true);
        }

        isItReturnTime = false;

        //Capture touch event on the menu option view :
        {
            RelativeLayout wholeView = (RelativeLayout) findViewById(R.id.header_tile_new);
            wholeView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        // Selection changed :
        {
            // Activity listener class :
            ActivitySelectChanges activitySelect = new ActivitySelectChanges(HighestRatedMoviesGridActivity.this);

            // Adding Listeners :
            {
                ((TextView) findViewById(R.id.most_popular_option_text)).setOnClickListener(activitySelect);
                ((TextView) findViewById(R.id.highest_rating_option_text)).setOnClickListener(activitySelect);
                ((TextView) findViewById(R.id.favourite_movies_option_text)).setOnClickListener(activitySelect);
            }

        }
    }

    @Override
    public void onBackPressed(){
        if(isItReturnTime){
            changeSelection(null);
            return;
        }

        super.onBackPressed();
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
            TheHighestRatedMoviesFragment.resume();
            loadedOnce = false;
        }

        // It wont do anything if nothing is selected or rootview is null, or fragment not created yet.
        TheHighestRatedDetailsFragment fragment = (TheHighestRatedDetailsFragment)getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2);
        if(fragment!=null) {
            fragment.refresh(true);
        }
    }

    /**
     * This function is called when favourite button is pressed by user in landscape mode.
     * @param v
     */
    public void favClicked(View v){
        TheHighestRatedDetailsFragment fragment = (TheHighestRatedDetailsFragment)getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2);
        fragment.favClicked(v);
    }

    /**
     * This function is called when share button is pressed by user in landscape mode.
     * @param v
     */
    public void shareClicked(View v){
        TheHighestRatedDetailsFragment fragment = (TheHighestRatedDetailsFragment)getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2);
        fragment.shareClicked(v);
    }

    /**
     * This function is called when menu change
     * @param v
     */
    public void changeSelection(View v){

        int timeInMillisecs = 350;

        if(!isItReturnTime) {

            ((TextView) findViewById(R.id.selection_ride_text_view)).setVisibility(View.INVISIBLE);
            RelativeLayout menuRelativeView = (RelativeLayout) findViewById(R.id.menu_relative_view_new);
            ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(menuRelativeView, "rotation", 0f, -90f);
            imageViewObjectAnimator.setDuration((long)(timeInMillisecs*1.2)); // miliseconds
            imageViewObjectAnimator.start();

            final RelativeLayout wholeView = (RelativeLayout) findViewById(R.id.header_tile_new);
            wholeView.setPivotX(wholeView.getWidth());
            final ObjectAnimator wholeViewAnimator = ObjectAnimator.ofFloat(wholeView, "rotation", 90f, 0f);
            wholeViewAnimator.setDuration((long)(timeInMillisecs));

            wholeViewAnimator.start();
            wholeView.setVisibility(View.VISIBLE);

            wholeViewAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((TextView) findViewById(R.id.selection_ride_text_view)).setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            isItReturnTime = true;
            return;
        }

        if(isItReturnTime){
            RelativeLayout menuRelativeView = (RelativeLayout) findViewById(R.id.menu_relative_view_new);
            ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(menuRelativeView, "rotation", -90f, 0f);
            imageViewObjectAnimator.setDuration((long)(timeInMillisecs/1.2)); // miliseconds
            imageViewObjectAnimator.start();

            ((TextView) findViewById(R.id.selection_ride_text_view)).setVisibility(View.INVISIBLE);


            final RelativeLayout wholeView = (RelativeLayout) findViewById(R.id.header_tile_new);
            wholeView.setPivotX(wholeView.getWidth());
            final ObjectAnimator wholeViewAnimator = ObjectAnimator.ofFloat(wholeView, "rotation", 0f, 90);
            wholeViewAnimator.setDuration((long) (timeInMillisecs));

            wholeViewAnimator.start();
            wholeView.setVisibility(View.VISIBLE);

            isItReturnTime = false;
            return;
        }


    }

}
