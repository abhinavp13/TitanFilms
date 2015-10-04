package com.nanodegree.abhinav.titanfilms.highestrated.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.abhinav.titanfilms.R;
import com.nanodegree.abhinav.titanfilms.application.ApplicationSavedData;
import com.nanodegree.abhinav.titanfilms.application.TitanFilmsApplication;
import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;
import com.nanodegree.abhinav.titanfilms.highestrated.activities.HighestRatedMoviesDetailActivity;
import com.nanodegree.abhinav.titanfilms.highestrated.daohandler.HighestRatedMoviesDataFetchHandler;
import com.nanodegree.abhinav.titanfilms.highestrated.fragments.TheHighestRatedDetailsFragment;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This class is HighestRatedGridView Adapter.
 *
 * Created by Abhinav Puri
 */
public class HighestRatedMoviesGridAdapter extends BaseAdapter {

    /**
     * Context for HighestRatedMovies Activity.
     */
    private Context context;

    /**
     * Layout Inflater from HighestRatedMovies Activity.
     */
    private LayoutInflater layoutInflater;

    /**
     * List of TheMovieDBObject type.
     */
    private ArrayList<TheMovieDBObject> theMovieDBObjectArrayList;

    /**
     * Boolean value saving whether data is dummy data or not.
     * If It is dummy data, then dummy tiles are loaded.
     * Else, Movies Tiles are loaded.
     */
    private boolean isDummyData;

    /**
     * Handler class.
     * Need to call back this class function for loading more items.
     */
    private HighestRatedMoviesDataFetchHandler highestRatedMoviesDataFetchHandler;

    /**
     * This is used for saving/fetching currently selected movie tile.
     */
    private ApplicationSavedData applicationSavedData;

    /**
     * Check for list populating for the first time.
     */
    private boolean isFirstTime;

    /**
     * Toast object to display when trailer link not found.
     */
    private Toast toast;

    /**
     * Constructor for this class.
     *
     * @param context
     * @param layoutInflater
     * @param theMovieDBObjectArrayList
     */
    public HighestRatedMoviesGridAdapter(Context context, LayoutInflater layoutInflater, ArrayList<TheMovieDBObject> theMovieDBObjectArrayList){
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.theMovieDBObjectArrayList = theMovieDBObjectArrayList;
        this.isDummyData = false;
        if(this.theMovieDBObjectArrayList == null){
            this.theMovieDBObjectArrayList = new ArrayList<>();
        }
        isFirstTime = true;
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) ((Activity) context).getApplication());

    }

    /**
     * This function sets handler class for fetching more items.
     *
     * @param highestRatedMoviesDataFetchHandler
     */
    public void setHandler(HighestRatedMoviesDataFetchHandler highestRatedMoviesDataFetchHandler){
        this.highestRatedMoviesDataFetchHandler = highestRatedMoviesDataFetchHandler;
    }

    /**
     * Getter for list of TheMovieDBObject.
     * @return list of highest rated movies data.
     */
    public ArrayList<TheMovieDBObject> getTheHighestRatedMoviesData(){
        return theMovieDBObjectArrayList;
    }

    /**
     * View Holder class for movie data
     */
    public class ViewHolder{
        ImageView moviePoster;
        TextView title;
        View selectionBar;
        RelativeLayout trailer_play_view;
    }

    /**
     * View Holder class for loading dummy tiles
     */
    public class DummyViewHolder{
        TextView loadingTextView;
        TextView nameTextView;
        View selectionBar;
    }

    @Override
    public int getCount() {
        return theMovieDBObjectArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return theMovieDBObjectArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(theMovieDBObjectArrayList.get(position)==null)
            isDummyData = true;
        else
            isDummyData = false;

        if(!isDummyData) {
            final ViewHolder holder;
            if(convertView!=null){
                if(!(convertView.getTag() instanceof ViewHolder)){
                    convertView = null;
                }
            }
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.custom_tile, null);
                holder.moviePoster = (ImageView) convertView.findViewById(R.id.movie_image_view);
                holder.title = (TextView) convertView.findViewById(R.id.movie_text_view);
                holder.selectionBar = (View)convertView.findViewById(R.id.selected_tile_bar);
                holder.trailer_play_view = (RelativeLayout)convertView.findViewById(R.id.trailer_play_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(theMovieDBObjectArrayList.get(position).getTitle());
            Picasso.with(convertView.getContext()).load(theMovieDBObjectArrayList.get(position).getPosterPath()).into(holder.moviePoster);

            holder.trailer_play_view.setOnClickListener(new TrailerPlayListener(position));

            if(isFirstTime && applicationSavedData.getCurrentSelectedForHighestRated()==null){
                applicationSavedData.setCurrentSelectedForHighestRated(theMovieDBObjectArrayList.get(position));
            } else if(isFirstTime && applicationSavedData.getCurrentSelectedForHighestRated()!=null) {
                if (applicationSavedData.getCurrentSelectedForHighestRated().getTitle() == null) {
                    applicationSavedData.setCurrentSelectedForHighestRated(theMovieDBObjectArrayList.get(position));
                }
            }


            if(isFirstTime) {
                if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                        ((Activity) context).getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2) != null) {
                    TheHighestRatedDetailsFragment fragment = (TheHighestRatedDetailsFragment) ((Activity) context).getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2);
                    fragment.refresh(true);
                }
                isFirstTime = false;
            }

            if(applicationSavedData.getCurrentSelectedForHighestRated().equals(theMovieDBObjectArrayList.get(position))){
                holder.selectionBar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
            } else {
                holder.selectionBar.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(!applicationSavedData.getCurrentSelectedForHighestRated().equals(theMovieDBObjectArrayList.get(position))){
                        holder.selectionBar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
                        applicationSavedData.setCurrentSelectedForHighestRated(theMovieDBObjectArrayList.get(position));
                        notifyDataSetChanged();
                    }

                    TitanFilmsApplication titanFilmsApplication = (TitanFilmsApplication) ((Activity)context).getApplication();
                    titanFilmsApplication.bitmap = holder.moviePoster.getDrawable();

                    // Update the fragment, if exists :
                    if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                            ((Activity) context).getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2)!=null){
                        TheHighestRatedDetailsFragment fragment = (TheHighestRatedDetailsFragment)((Activity)context).getFragmentManager().findFragmentById(R.id.highest_rated_fragment_2);
                        fragment.refresh(true);
                    } else {
                        Intent intent = new Intent(context, HighestRatedMoviesDetailActivity.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, Pair.create((View) holder.trailer_play_view, "trailer_play"), Pair.create((View)holder.moviePoster, "moviePoster"));
                        context.startActivity(intent, options.toBundle());
                    }

                }
            });

            if(position == theMovieDBObjectArrayList.size()-10) {
                // time to load more ;
                highestRatedMoviesDataFetchHandler.loadMore();
            }

        } else {
            final DummyViewHolder dummyViewHolder;
            if(convertView!=null){
                if(!(convertView.getTag() instanceof DummyViewHolder)){
                    convertView = null;
                }
            }
            if(convertView == null){
                dummyViewHolder = new DummyViewHolder();
                convertView = layoutInflater.inflate(R.layout.dummy_tile,null);
                dummyViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
                dummyViewHolder.selectionBar = convertView.findViewById(R.id.selected_tile_bar_loading);
                convertView.setTag(dummyViewHolder);
            } else {
                dummyViewHolder = (DummyViewHolder) convertView.getTag();
            }
            dummyViewHolder.selectionBar.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }

        return convertView;

    }

    /**
     * This function clears previous data and and adds dummy data.
     * Called once in the beginning for dummy views addition.
     *
     * @param theMovieDBObjects
     * @param isDummy
     */
    public void addMoreItemsClearPrevious(ArrayList<TheMovieDBObject> theMovieDBObjects, boolean isDummy){
        this.isDummyData = isDummy;
        theMovieDBObjectArrayList.clear();
        theMovieDBObjectArrayList.addAll(0, theMovieDBObjects);
        notifyDataSetChanged();
    }

    /**
     * This function adds in the beginning new data.
     * Data is assured to be movie data and not dummy data.
     * Makes sure data is not present already.
     *
     * @param theMovieDBObjectArrayList
     */
    public void addMoreItems(ArrayList<TheMovieDBObject> theMovieDBObjectArrayList){

        // Clean new data list :
        ArrayList<TheMovieDBObject> cleanData = new ArrayList<>();

        // Check for duplicity :
        for(TheMovieDBObject theMovieDBObject : theMovieDBObjectArrayList){
            int flag = 0;
            for(TheMovieDBObject theMovieDBObject1 : this.theMovieDBObjectArrayList){

                // Equals definition is overriden in TheMovieDBObject class.
                if(theMovieDBObject.equals(theMovieDBObject1)){
                    flag = 1;
                }
            }
            if(flag == 0) {
                cleanData.add(theMovieDBObject);
            }
        }

        this.theMovieDBObjectArrayList.addAll(this.theMovieDBObjectArrayList.size(),theMovieDBObjectArrayList);
        this.isDummyData = false;

        notifyDataSetChanged();
    }


    /**
     * Class used to play trailer when user presses trailer play button,
     * present on the top of each movie tile.
     *
     * @author Abhinav Puri.
     */
    class TrailerPlayListener implements View.OnClickListener{

        /**
         * position
         */
        private int position;

        public TrailerPlayListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(theMovieDBObjectArrayList.get(position).getTrailerLink() == null || theMovieDBObjectArrayList.get(position).getTrailerLink().equals("")){

                // Context is required.
                if(context == null){
                    return;
                }

                // Trailer loading Toast :
                if (toast == null) {
                    toast = Toast.makeText(context, "Fetching Trailer, Try Again Later", Toast.LENGTH_SHORT);
                } else {
                    toast.cancel();
                    toast = Toast.makeText(context, "Fetching Trailer, Try Again Later", Toast.LENGTH_SHORT);
                }
                toast.show();
                return;
            }

            String id = theMovieDBObjectArrayList.get(position).getTrailerLink();
            id = id.substring(id.lastIndexOf("=") + 1);


            // Intent to play Youtube videos, fallback to browser if youtube app not present
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                ((Activity)context).startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                return;
            }
        }
    }
}
