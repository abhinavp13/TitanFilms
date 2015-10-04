package com.nanodegree.abhinav.titanfilms.mostpopular.activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.mostpopular.fragments.TheMostPopularDetailsFragment;
import com.nanodegree.abhinav.titanfilms.mostpopular.fragments.TheMostPopularMoviesFragment;
import com.nanodegree.abhinav.titanfilms.tfutils.ActivitySelectChanges;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;


public class MostPopularMoviesGridActivity extends Activity {


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
        setContentView(R.layout.activity_most_popular_movies_grid);

        // Title Text :
        ((TextView)findViewById(R.id.title)).setText("Most Popular");

        // Setting Status Bar :
        TFUtils.setStatusBarColor(getWindow(), getResources().getColor(R.color.dark_blue));

        // Setting font for Title View :
        TFUtils.setRobotoLight((TextView) findViewById(R.id.title), getAssets());

        // Some Option Font Changes :
        TFUtils.setRobotoLight((TextView) findViewById(R.id.most_popular_option_text), getAssets());
        TFUtils.setRobotoLight((TextView) findViewById(R.id.highest_rating_option_text), getAssets());
        TFUtils.setRobotoLight((TextView) findViewById(R.id.favourite_movies_option_text), getAssets());
        TFUtils.setRobotoLight((TextView) findViewById(R.id.selection_ride_text_view),getAssets());

        // Color Spread effect :
        //actionBarColorChange((RelativeLayout) findViewById(R.id.action_bar), getResources().getColor(android.R.color.white), getResources().getColor(R.color.blue));

        // It wont do anything if nothing is selected or rootview is null, or fragment not created yet.
        TheMostPopularDetailsFragment fragment = (TheMostPopularDetailsFragment)getFragmentManager().findFragmentById(R.id.most_popular_fragment_2);
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
            ActivitySelectChanges activitySelect = new ActivitySelectChanges(MostPopularMoviesGridActivity.this);

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

    /**
     * This function performs action Color Change in blink of an eye.
     * Animation is not seem with naked eye.
     * Can be removed, but is used currently.
     *
     * @param actionBar
     * @param fromColor
     * @param toColor
     */
    private void actionBarColorChange(final RelativeLayout actionBar,int fromColor, int toColor){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                actionBar.setBackgroundColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }


    @Override
    public void onResume(){
        super.onResume();
        if(loadedOnce) {
            TheMostPopularMoviesFragment.resume();
            loadedOnce = false;
        }

        // It wont do anything if nothing is selected or rootview is null, or fragment not created yet.
        TheMostPopularDetailsFragment fragment = (TheMostPopularDetailsFragment)getFragmentManager().findFragmentById(R.id.most_popular_fragment_2);
        if(fragment!=null) {
            fragment.refresh(true);
        }
    }

    /**
     * This function is called when favourite button is pressed by user in landscape mode.
     * @param v
     */
    public void favClicked(View v){
        TheMostPopularDetailsFragment fragment = (TheMostPopularDetailsFragment)getFragmentManager().findFragmentById(R.id.most_popular_fragment_2);
        fragment.favClicked(v);
    }

    /**
     * This function is called when share button is pressed by user in landscape mode.
     * @param v
     */
    public void shareClicked(View v){
        TheMostPopularDetailsFragment fragment = (TheMostPopularDetailsFragment)getFragmentManager().findFragmentById(R.id.most_popular_fragment_2);
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
