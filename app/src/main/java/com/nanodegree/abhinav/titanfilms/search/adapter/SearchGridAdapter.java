package com.nanodegree.abhinav.titanfilms.search.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import com.nanodegree.abhinav.titanfilms.search.activities.SearchMovieDetail;
import com.nanodegree.abhinav.titanfilms.tfutils.TFUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This class is an adapter for grid view present in Search Activity.
 *
 * Created by Abhinav Puri
 */
public class SearchGridAdapter extends BaseAdapter {

    /**
     * The data holder arraylist.
     */
    private ArrayList<TheMovieDBObject> theMovieDBObjectArrayList;

    /**
     * Activity context
     */
    private Context context;

    /**
     * Saved data instance.
     */
    private ApplicationSavedData applicationSavedData;

    /**
     * Toast object to display when trailer link not found.
     */
    private Toast toast;

    /**
     * Override default constructor
     * Context is required for starting new detail activity.
     *
     * @param context
     */
    public SearchGridAdapter(Context context){
        this.context = context;
        applicationSavedData = TFUtils.populateApplicationSavedData((TitanFilmsApplication) ((Activity) context).getApplication());
        theMovieDBObjectArrayList = new ArrayList<TheMovieDBObject>();
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

        // Logic for view holder :
        final ViewHolder holder;
        if (convertView != null) {
            if (!(convertView.getTag() instanceof ViewHolder)) {
                convertView = null;
            }
        }

        // If convert view is null create it and set its tag.
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_tile, null);
            holder.moviePoster = (ImageView) convertView.findViewById(R.id.movie_image_view);
            holder.title = (TextView) convertView.findViewById(R.id.movie_text_view);
            holder.selectionBar = (View) convertView.findViewById(R.id.selected_tile_bar);
            holder.trailer_play_view = (RelativeLayout) convertView.findViewById(R.id.trailer_play_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set up elements :
        holder.title.setText(theMovieDBObjectArrayList.get(position).getTitle());
        Picasso.with(convertView.getContext()).load(theMovieDBObjectArrayList.get(position).getPosterPath()).into(holder.moviePoster);
        holder.trailer_play_view.setOnClickListener(new TrailerPlayListener(position));

        // Set the current selected :
        if (applicationSavedData.getCurrentSelectedForSearch() == null) {
            applicationSavedData.setCurrentSelectedForSearch(theMovieDBObjectArrayList.get(position));
        }
        convertView.setOnClickListener(new TileClickListener(position, holder));

        return convertView;

    }

    /**
     * Clears the whole data in adapter and notifies UI.
     */
    public void hardReset(){
        theMovieDBObjectArrayList.clear();
        this.notifyDataSetChanged();
    }

    /**
     * Adds new Data clearing previous always
     *
     * @param newDataArray
     */
    public void updateData(ArrayList<TheMovieDBObject> newDataArray){

        // Make sure data is not present :
        if(theMovieDBObjectArrayList != null && theMovieDBObjectArrayList.size() > 0){
            theMovieDBObjectArrayList.clear();
        }

        // Make sure to initialize arraylist :
        if(theMovieDBObjectArrayList == null){
            theMovieDBObjectArrayList = new ArrayList<TheMovieDBObject>();
        }

        theMovieDBObjectArrayList.addAll(newDataArray);
        this.notifyDataSetChanged();
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

    /**
     * Class for handling on click events for each tile.
     *
     * @author Abhinav Puri
     */
    class TileClickListener implements View.OnClickListener{

        /**
         * Position in grid view
         */
        private int position;

        /**
         * View holder instance
         */
        private ViewHolder holder;

        /**
         * This is default constructor
         *
         * @param position
         * @param holder
         */
        public TileClickListener(int position, ViewHolder holder){
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {

            if (!applicationSavedData.getCurrentSelectedForSearch().equals(theMovieDBObjectArrayList.get(position))) {
                    applicationSavedData.setCurrentSelectedForSearch(theMovieDBObjectArrayList.get(position));
            }

            TitanFilmsApplication titanFilmsApplication = (TitanFilmsApplication) ((Activity) context).getApplication();
            titanFilmsApplication.bitmap = holder.moviePoster.getDrawable();

            Intent intent = new Intent(context, SearchMovieDetail.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, Pair.create((View) holder.trailer_play_view, "trailer_play"), Pair.create((View) holder.moviePoster, "moviePoster"));
            context.startActivity(intent, options.toBundle());

        }
    }

}
