package com.nanodegree.abhinav.titanfilms.mainactivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.selectionchange.SelectionChangeActivity;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

import java.util.Timer;
import java.util.TimerTask;

import pl.tajchert.sample.DotsTextView;


/**
 * This is the main activity for this app.
 *
 * @author Abhinav Puri
 */
public class MainActivity extends Activity {

    /**
     * Important animation handlers
     */
    private Handler handler;
    private Handler innerHandler;
    private Handler innerInnerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.splash_screen);

        // Splash Screen Work :
        SplashScreenInitiation();

        // Setting Status Bar :
        TFUtils.setStatusBarColor(getWindow(), getResources().getColor(R.color.dark_blue));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * This function create loading animations and plays them.
     * They act as splash screen view animations.
     */
    private void SplashScreenInitiation(){

        // This gets displayed after loading animation ends.
        final TextView textViewAboveLoading = (TextView)findViewById(R.id.text_view_above_loading);
        // This is the main loading animation.
        final AnimatedCircleLoadingView animatedCircleLoadingView = (AnimatedCircleLoadingView)findViewById(R.id.circle_loading_view);
        // This is the google hangout dots animation.
        final DotsTextView dotsTextView = (DotsTextView)findViewById(R.id.dots);
        // This is used to transit half blue and half white
        final LinearLayout halfBlueHalfWhite = (LinearLayout)findViewById(R.id.half_blue_half_white);
        // This is transition text view in between two activities
        final TextView titleView = (TextView)findViewById(R.id.title);

        // Start animated loading animation :
        animatedCircleLoadingView.startIndeterminate();
        // Start google hangout animation :
        dotsTextView.showAndPlay();

        // Setting Roboto light font to Title text View :
        TFUtils.setRobotoLight((TextView) findViewById(R.id.title), getAssets());

        //Splash Screen for few seconds :
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //Some small animations :
                animatedCircleLoadingView.stopOk();
                dotsTextView.hideAndStop();
                dotsTextView.setVisibility(View.GONE);

                // Only show if orientation is portrait :
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    textViewAboveLoading.setVisibility(View.INVISIBLE);
                } else {
                    textViewAboveLoading.setVisibility(View.VISIBLE);
                }

                // Handler required for Waiting animations to complete :
                innerHandler = new Handler();
                innerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // Animating animated circular loading view :
                        ObjectAnimator objectAnimatorForAnimatedLoadingView = ObjectAnimator.ofFloat(animatedCircleLoadingView, "translationX", 0f, -2 * getWindowManager().getDefaultDisplay().getWidth());
                        objectAnimatorForAnimatedLoadingView.setDuration(500);
                        objectAnimatorForAnimatedLoadingView.start();

                        // Animating "Your Hollywood Ride" textview :
                        ObjectAnimator objectAnimatorForTextViewAboveLoading = ObjectAnimator.ofFloat(textViewAboveLoading, "translationX", 0f, 2 * getWindowManager().getDefaultDisplay().getWidth());
                        objectAnimatorForTextViewAboveLoading.setDuration(500);
                        objectAnimatorForTextViewAboveLoading.start();

                        // starting new Activity :
                        innerInnerHandler = new Handler();
                        innerInnerHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startNewActivity();
                            }
                        },250);
                    }
                },2000);

            }
        }, 3500);


    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){

        // Callbacks removal is needed here,
        // When ever app goes in background,
        // Need to kill callbacks.
        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
        }
        if(innerHandler != null) {
            innerHandler.removeCallbacksAndMessages(null);
        }
        if(innerInnerHandler != null) {
            innerInnerHandler.removeCallbacksAndMessages(null);
        }
        super.onStop();
    }

    @Override
    public void onRestart(){

        // For safety reasons, cut all callbacks from handlers.
        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
        }
        if(innerHandler != null) {
            innerHandler.removeCallbacksAndMessages(null);
        }
        if(innerInnerHandler != null) {
            innerInnerHandler.removeCallbacksAndMessages(null);
        }
        // Important Initailization again needed :
        SplashScreenInitiation();
        super.onRestart();
    }



    /**
     * This function starts new activity when ever
     * saved instance has bypass value as true.
     * Needed for orientation change check.
     */
    private void startNewActivity(){

        // Title View
        final TextView titleView = (TextView)findViewById(R.id.title);

        // Intent for new activity :
        Intent intent = new Intent(MainActivity.this, SelectionChangeActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, (View)findViewById(R.id.below_blue),"search_bar");
        startActivity(intent, options.toBundle());

        // For safety reasons, cut all callbacks from handlers.
        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
        }
        if(innerHandler != null) {
            innerHandler.removeCallbacksAndMessages(null);
        }
        if(innerInnerHandler != null) {
            innerInnerHandler.removeCallbacksAndMessages(null);
        }

        // Handler required for Waiting animations to complete :
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);

    }

}
