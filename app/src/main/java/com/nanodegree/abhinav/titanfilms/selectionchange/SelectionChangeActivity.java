package com.nanodegree.abhinav.titanfilms.selectionchange;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.search.activities.SearchGridActivity;
import com.nanodegree.abhinav.titanfilms.tfutils.ActivitySelectChanges;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;


public class SelectionChangeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting Content View :
        setContentView(R.layout.activity_selection_change);

        // Setting Status Bar :
        TFUtils.setStatusBarColor(getWindow(), getResources().getColor(R.color.dark_blue));

        // Setting font for Title View :
        TFUtils.setRobotoLight((TextView) findViewById(R.id.textView2), getAssets());


        // Activity Change listener class :
        ActivitySelectChanges activitySelectChanges = new ActivitySelectChanges(SelectionChangeActivity.this);

        // Listeners  :
        ((ImageView)findViewById(R.id.imageView1)).setOnClickListener(activitySelectChanges);
        ((ImageView)findViewById(R.id.imageView2)).setOnClickListener(activitySelectChanges);
        ((ImageView)findViewById(R.id.imageView3)).setOnClickListener(activitySelectChanges);

        // Listener for Relative layout which supports search option :
        ((RelativeLayout)findViewById(R.id.relativeLayout2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionChangeActivity.this,SearchGridActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SelectionChangeActivity.this, v, "search_bar");
                startActivity(intent,options.toBundle());
            }
        });

        // Check for whether there is animation requirement or not :
        if(savedInstanceState == null){
            showAnimation();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("ANIMATION",false);
    }

    /**
     * Does Animation when activity starts.
     * Must be called from onCreate(Bundle instance) stage only.
     */
    private void showAnimation(){

        // Search Bar animation :
        ObjectAnimator searchText = ObjectAnimator.ofFloat(findViewById(R.id.relativeLayout2),"translationX", -2 * getWindowManager().getDefaultDisplay().getWidth(), 0f);
        searchText.setDuration(1500);
        searchText.start();

        // Mostpopular ImageView :
        scaleAndStart(findViewById(R.id.imageView1));

        // Favourite ImageView :
        scaleAndStart(findViewById(R.id.imageView3));

        // Highest Popular ImageView :
        scaleAndStart(findViewById(R.id.imageView2));

    }

    /**
     * Helper function for animation.
     * @param v
     */
    private void scaleAndStart(View v){

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(v,"ScaleX",0f);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(v,"ScaleY",0f);
        objectAnimatorX.setDuration(0);
        objectAnimatorY.setDuration(0);
        objectAnimatorX.start();
        objectAnimatorY.start();

        ObjectAnimator objectAnimatorXX = ObjectAnimator.ofFloat(v,"ScaleX",1f);
        ObjectAnimator objectAnimatorYY = ObjectAnimator.ofFloat(v,"ScaleY",1f);
        objectAnimatorXX.setDuration(1500);
        objectAnimatorYY.setDuration(1500);
        objectAnimatorXX.start();
        objectAnimatorYY.start();
    }

    @Override
    public void onBackPressed(){
        // Fix for transition cover showing even after destroyed.
        View relativeView = findViewById(R.id.above_blue);
        if(relativeView!=null) {
            relativeView.setVisibility(View.INVISIBLE);
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy(){
        // Fix for transition cover showing even after destroyed.
        View relativeView = findViewById(R.id.above_blue);
        if(relativeView!=null) {
            relativeView.setVisibility(View.INVISIBLE);
        }
        super.onDestroy();
    }
}
