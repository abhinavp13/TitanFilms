package com.nanodegree.abhinav.titanfilms.tfutils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nanodegree.abhinav.titanfilms.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class populates genre into corresponding activity views.
 * Need to populate in such a way that it looks centralized always.
 * Does not matter whether number of genre are how many will always
 * populate no more than first eight genres.
 *
 * Created by Abhinav Puri
 */
public class PopulateGenre {

    /**
     * Activity Context
     */
    Context context;

    /**
     * Constructor for saving context.
     *
     * @param context
     */
    public PopulateGenre(Context context){
        this.context = context;
    }

    /**
     * Main function for which this class will
     * get used. This is called from every detail
     * activity and detail fragment.
     *
     * @param genreIds
     */
    public void populateGenreGivenIds(int[] genreIds) {

        String[] genres = null;
        try {
            genres = getGenres(genreIds);
        } catch (Exception e) {
            Log.e("AppConfig Error : ", e.toString());
        }

        /*
         * Its time to populate genres
         */
        if(genres !=null && genres.length>0){
           populateAtMaxEightGenres(genres);
        }
    }

    /**
     * This function finds all genre given genreIds.
     *
     * @param genreIds
     * @return array of genres in Strings
     */
    private String[] getGenres(int[] genreIds) throws IOException {

        /*
         * Singleton AppConfig
         * If available use to fetch genreMap.
         */
        if (AppConfig.appConfig == null) {
            AppConfig.Instance();
        }

        ArrayList<String> genreNames = new ArrayList<>();
        HashMap<String, String> idToGenreNameMap = AppConfig.findMap("*.genreMap");

        for (int genreId : genreIds) {
            String value = idToGenreNameMap.get(String.valueOf(genreId));
            if (value != null && value.length() > 0) {
                genreNames.add(value);
            }
        }

        return genreNames.toArray(new String[genreNames.size()]);
    }

    /**
     * This function uses activity context.
     * Searches for genre populating views present.
     * If present in context, populate corresponding
     * genre views else hide genre view.
     *
     * @param genres
     */
    private void populateAtMaxEightGenres(String[] genres){

        Activity activity = (Activity)context;

        View genre1 = activity.findViewById(R.id.genre1);
        View genre2 = activity.findViewById(R.id.genre2);

        /*
         * They should be present for populate genres.
         */
        if(genre1 == null || genre2 == null){
            return;
        }

        switch (genres.length){
            case 1 :
                populateInGenre1(genres);
                break;
            case 2 :
                populateInGenre1(genres);
                break;
            case 3 :
                populateInGenre1(genres);
                break;
            case 4 :
                populateInGenre1(genres);
                break;
            case 5 :
                populateInGenre1(new String[]{genres[0],genres[1],genres[2]});
                populateInGenre2_2(new String[]{genres[3], genres[4]});
                break;
            case 6 :
                populateInGenre1(new String[]{genres[0],genres[1],genres[2]});
                populateInGenre2(new String[]{genres[3], genres[4], genres[5]});
                break;
            case 7 :
                populateInGenre1(new String[]{genres[0],genres[1],genres[2],genres[3]});
                populateInGenre2(new String[]{genres[4], genres[5], genres[6]});
                break;
            case 8 :
                populateInGenre1(new String[]{genres[0], genres[1], genres[2], genres[3]});
                populateInGenre2(new String[]{genres[4], genres[5], genres[6], genres[7]});
                break;
            default:
                break;
        }
    }


    /**
     * Populates in first row of Genres.
     * Make sure length of genres is less than or equal to 4.
     *
     * @param genres
     */
    private void populateInGenre1(String[] genres){

        Activity activity = (Activity)context;

        HashMap<String,String> genreResourceIcon = AppConfig.findMap("*.genreResourceIcon");

        // Set the visibility of main linear layout as visible :
        activity.findViewById(R.id.genre1).setVisibility(View.VISIBLE);

        for(int i = 0; i<genres.length; i++){
            int linearLayoutId = activity.getResources().getIdentifier("genre1_"+ String.valueOf(i+1) ,"id",activity.getPackageName());
            ((LinearLayout)activity.findViewById(linearLayoutId)).setVisibility(View.VISIBLE);

            int imageViewId = activity.getResources().getIdentifier("genre1_" + String.valueOf(i+1) + "_image_view", "id", activity.getPackageName());
            int drawableGenreIcon = activity.getResources().getIdentifier(genreResourceIcon.get(genres[i]),"drawable",activity.getPackageName());
            ((ImageView)activity.findViewById(imageViewId)).setImageResource(drawableGenreIcon);

            int textViewId = activity.getResources().getIdentifier("genre1_"+String.valueOf(i+1)+"_text_view","id", activity.getPackageName());
            ((TextView)activity.findViewById(textViewId)).setText(genres[i]);
        }

    }

    /**
     * Populates in second row of Genres.
     * Make sure length of genres is less than or equal to 4.
     *
     * @param genres
     */
    private void populateInGenre2(String[] genres){
        Activity activity = (Activity)context;

        HashMap<String,String> genreResourceIcon = AppConfig.findMap("*.genreResourceIcon");

        // Set the visibility of main linear layout as visible :
        activity.findViewById(R.id.genre2).setVisibility(View.VISIBLE);

        int i = 0;
        for(i = 0; i<genres.length; i++){
            int linearLayoutId = activity.getResources().getIdentifier("genre2_"+ String.valueOf(i+1) ,"id",activity.getPackageName());
            ((LinearLayout)activity.findViewById(linearLayoutId)).setVisibility(View.VISIBLE);

            int imageViewId = activity.getResources().getIdentifier("genre2_" + String.valueOf(i+1) + "_image_view", "id", activity.getPackageName());
            int drawableGenreIcon = activity.getResources().getIdentifier(genreResourceIcon.get(genres[i]),"drawable",activity.getPackageName());
            ((ImageView)activity.findViewById(imageViewId)).setImageResource(drawableGenreIcon);

            int textViewId = activity.getResources().getIdentifier("genre2_"+String.valueOf(i+1)+"_text_view","id", activity.getPackageName());
            ((TextView)activity.findViewById(textViewId)).setText(genres[i]);
        }
    }

    /**
     * Populates in second row of Genres.
     * Make sure length of genres is less than or equal to 4.
     * This is used when we only need to populate 2 items in
     * second row.
     *
     * @param genres
     */
    private void populateInGenre2_2(String[] genres){
        Activity activity = (Activity)context;

        HashMap<String,String> genreResourceIcon = AppConfig.findMap("*.genreResourceIcon");

        // Set the visibility of main linear layout as visible :
        activity.findViewById(R.id.genre2).setVisibility(View.VISIBLE);

        for(int i = 0; i< 4; i++){

            int linearLayoutId = activity.getResources().getIdentifier("genre2_"+ String.valueOf(i+1) ,"id",activity.getPackageName());
            int imageViewId = activity.getResources().getIdentifier("genre2_" + String.valueOf(i+1) + "_image_view", "id", activity.getPackageName());
            int textViewId = activity.getResources().getIdentifier("genre2_"+String.valueOf(i+1)+"_text_view","id", activity.getPackageName());

            if(i == 0 || i== 3 ){
                ((LinearLayout)activity.findViewById(linearLayoutId)).setVisibility(View.VISIBLE);
                ((ImageView)activity.findViewById(imageViewId)).setVisibility(View.INVISIBLE);
                ((TextView)activity.findViewById(textViewId)).setVisibility(View.INVISIBLE);
                continue;
            }

            int drawableGenreIcon = activity.getResources().getIdentifier(genreResourceIcon.get(genres[i-1]),"drawable",activity.getPackageName());
            ((LinearLayout)activity.findViewById(linearLayoutId)).setVisibility(View.VISIBLE);
            ((ImageView)activity.findViewById(imageViewId)).setImageResource(drawableGenreIcon);
            ((TextView)activity.findViewById(textViewId)).setText(genres[i-1]);
        }
    }


}
