package com.nanodegree.abhinav.titanfilms.search.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.search.adapter.SearchGridAdapter;
import com.nanodegree.abhinav.titanfilms.search.daohandler.SearchDataFetchHandler;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import pl.tajchert.sample.DotsTextView;

public class SearchGridActivity extends Activity implements TextWatcher {

    /**
     * Grid View for this activity
     */
    private GridView gridView;

    /**
     * Adapter class for grid view
     */
    private SearchGridAdapter gridViewAdapter;

    /**
     * Handler for fetching data for a given query.
     */
    private SearchDataFetchHandler searchDataFetchHandler;

    /**
     * This is the value searched for currently.
     */
    private String valueSearchedFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_grid);

        // Important to change status bar color
        TFUtils.setStatusBarColor(getWindow(), getResources().getColor(R.color.dark_blue));

        // Sometimes keyboard don't pops in, below line forces keyboard to come up (need to add in manifest also)
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(findViewById(R.id.search_edit_text), InputMethodManager.SHOW_IMPLICIT);

        // Find the grid view and hide it.
        gridView = (GridView)findViewById(R.id.grid_view);
        gridView.setVisibility(View.INVISIBLE);

        // Need an adapter for gridview ;
        gridViewAdapter = new SearchGridAdapter(SearchGridActivity.this);
        gridView.setAdapter(gridViewAdapter);

        searchDataFetchHandler = new SearchDataFetchHandler(SearchGridActivity.this, gridView);

        // Text Change listener required for fetching data as soon as user enters a character in search bar.
        ((EditText)findViewById(R.id.search_edit_text)).addTextChangedListener(this);

        // IMEAction handling for keyboard linked with edit text :
        ((EditText)findViewById(R.id.search_edit_text)).setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
    }


    /**
     * Back button pressed by user.
     * This back button is on screen top left corner arrow.
     * Is merged for hardware back button pressed.
     *
     * @param v view of back arrow
     */
    public void backClicked(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed(){

        // Hiding few elements :
        findViewById(R.id.imageView_1).setVisibility(View.INVISIBLE);
        findViewById(R.id.search_image_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.search_edit_text).setVisibility(View.INVISIBLE);

        // Hiding keyboard :
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(findViewById(R.id.search_edit_text).getWindowToken(), 0);

        super.onBackPressed();
    }


    /**
     * Lets delay for a while
     * Only searches online when user has stopped
     * typing for more than DELAY number of milliseconds.
     */
    private Timer timer = new Timer();
    private final long DELAY = 1000;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(timer != null)
            timer.cancel();
    }

    @Override
    public void afterTextChanged(Editable s) {

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                SearchGridActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        findViewById(R.id.nothing_found_text_view).setVisibility(View.INVISIBLE);

                        // Hide the current grid view :
                        gridView.setVisibility(View.INVISIBLE);

                        // Time to play loading symbol :
                        ((DotsTextView) findViewById(R.id.dots_view)).showAndPlay();
                        ((DotsTextView) findViewById(R.id.dots_view)).setVisibility(View.VISIBLE);

                        // Empty Adapter :
                        gridViewAdapter.hardReset();

                        // Time to Search for new key word :
                        searchDataFetchHandler.startFetchingData(((EditText) findViewById(R.id.search_edit_text)).getText().toString());
                    }
                });

            }

        }, DELAY);

    }
}
