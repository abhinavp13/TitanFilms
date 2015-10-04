package com.nanodegree.abhinav.titanfilms.application;


import com.nanodegree.abhinav.titanfilms.dao.TheMovieDBObject;

import java.util.ArrayList;

/**
 * This is an application data source.
 * It saves some data for most popular, highest rating. and favourite movies.
 * This class saves few data for faster loading of data from each of
 * Most popular, Highest Rated and Favourite Movies.
 *
 * Remember for Favourite Movies are also saved offline.
 * This class helps within app during activity change, one donot need to
 * fetch from database again, it gets saved here.
 * But once app is closed, then when again opened, will do a database fetch.
 *
 * Created by Abhinav Puri
 */
public class ApplicationSavedData {


    /**
     * Current Element selected for each type
     */
    private TheMovieDBObject currentSelectedForMostPopular;
    private TheMovieDBObject currentSelectedForHighestRated;
    private TheMovieDBObject currentSelectedForFavourite;
    private TheMovieDBObject currentSelectedForSearch;

    /**
     * List of objects saved for each type while application is in running stage.
     */
    private ArrayList<TheMovieDBObject> mostPopularMoviesSavedData;
    private ArrayList<TheMovieDBObject> highestRatedMoviesSavedData;
    private ArrayList<TheMovieDBObject> favouriteMoviesSavedData;
    private ArrayList<TheMovieDBObject> searchMoviesSavedData;

    /**
     * These are used for fetching data in a paginated manner.
     */
    private int nextPageToDownloadFromForMostPopular;
    private int nextPageToDownloadFromForHighestRated;
    private int nextPageToDownloadFromForFavourite;
    private int nextPageToDownloadFromForSearch;

    /**
     * Constructor initializing Elements.
     */
    public ApplicationSavedData(){
        currentSelectedForMostPopular = new TheMovieDBObject();
        currentSelectedForHighestRated = new TheMovieDBObject();
        currentSelectedForFavourite = new TheMovieDBObject();
        currentSelectedForSearch = new TheMovieDBObject();

        mostPopularMoviesSavedData = new ArrayList<>();
        highestRatedMoviesSavedData = new ArrayList<>();
        favouriteMoviesSavedData = new ArrayList<>();
        searchMoviesSavedData = new ArrayList<>();

        nextPageToDownloadFromForMostPopular = 1;
        nextPageToDownloadFromForHighestRated = 1;
        nextPageToDownloadFromForFavourite = 1;
        nextPageToDownloadFromForSearch = 1;
    }

    // ######################################################### Getters and Setters #################################################################### //

    public ArrayList<TheMovieDBObject> getSearchMoviesSavedData() {
        return searchMoviesSavedData;
    }

    public void setSearchMoviesSavedData(ArrayList<TheMovieDBObject> searchMoviesSavedData) {
        nextPageToDownloadFromForSearch ++ ;
        this.searchMoviesSavedData = searchMoviesSavedData;
    }


    public ArrayList<TheMovieDBObject> getFavouriteMoviesSavedData() {
        nextPageToDownloadFromForFavourite++;
        return favouriteMoviesSavedData;
    }

    public void setFavouriteMoviesSavedData(ArrayList<TheMovieDBObject> favouriteMoviesSavedData) {
        this.favouriteMoviesSavedData = favouriteMoviesSavedData;
    }

    public ArrayList<TheMovieDBObject> getHighestRatedMoviesSavedData() {
        return highestRatedMoviesSavedData;
    }

    public void setHighestRatedMoviesSavedData(ArrayList<TheMovieDBObject> highestRatedMoviesSavedData) {
        nextPageToDownloadFromForHighestRated++;
        this.highestRatedMoviesSavedData = highestRatedMoviesSavedData;
    }

    public ArrayList<TheMovieDBObject> getMostPopularMoviesSavedData() {
        return mostPopularMoviesSavedData;
    }

    public void setMostPopularMoviesSavedData(ArrayList<TheMovieDBObject> mostPopularMoviesSavedData) {
        nextPageToDownloadFromForMostPopular++;
        this.mostPopularMoviesSavedData = mostPopularMoviesSavedData;
    }

    public TheMovieDBObject getCurrentSelectedForMostPopular() {
        return currentSelectedForMostPopular;
    }

    public void setCurrentSelectedForMostPopular(TheMovieDBObject currentSelectedForMostPopular) {
        this.currentSelectedForMostPopular = currentSelectedForMostPopular;
    }

    public TheMovieDBObject getCurrentSelectedForHighestRated() {
        return currentSelectedForHighestRated;
    }

    public void setCurrentSelectedForHighestRated(TheMovieDBObject currentSelectedForHighestRated) {
        this.currentSelectedForHighestRated = currentSelectedForHighestRated;
    }

    public TheMovieDBObject getCurrentSelectedForFavourite() {
        return currentSelectedForFavourite;
    }

    public void setCurrentSelectedForFavourite(TheMovieDBObject currentSelectedForFavouriteMovies) {
        this.currentSelectedForFavourite = currentSelectedForFavouriteMovies;
    }

    public int getNextPageToDownloadFromForMostPopular() {
        return nextPageToDownloadFromForMostPopular;
    }

    public int getNextPageToDownloadFromForHighestRated() {
        return nextPageToDownloadFromForHighestRated;
    }

    public int getNextPageToDownloadFromForFavourite() {
        return nextPageToDownloadFromForFavourite;
    }

    public int getNextPageToDownloadFromForSearch() {
        return nextPageToDownloadFromForSearch;
    }

    public TheMovieDBObject getCurrentSelectedForSearch() {
        return currentSelectedForSearch;
    }

    public void setCurrentSelectedForSearch(TheMovieDBObject currentSelectedForSearch) {
        this.currentSelectedForSearch = currentSelectedForSearch;
    }

    // ################################################################################################################################################ //


    /**
     * Appends to the data present in favourite movies arraylist.
     *
     * @param theMovieDBObject
     */
    public void appendFavouriteMovies(TheMovieDBObject theMovieDBObject){
        if(favouriteMoviesSavedData == null){
            favouriteMoviesSavedData = new ArrayList<>();
        }
        favouriteMoviesSavedData.add(theMovieDBObject);
    }

    /**
     * Deletes data saved in favourite movies arraylist.
     *
     * It does not delete data on android device.
     *
     * @param theMovieDBObject
     */
    public void deleteFavouriteMovies(TheMovieDBObject theMovieDBObject){
        if(favouriteMoviesSavedData.size() == 1){
            favouriteMoviesSavedData = null;
        } else {
            for(int i = 0; i < favouriteMoviesSavedData.size(); i++){
                if(favouriteMoviesSavedData.get(i).equals(theMovieDBObject)){
                    favouriteMoviesSavedData.remove(i);
                    return;
                }
            }
        }
    }


    /**
     *
     * Important to check for favourite movie data availability across saved instance.
     *
     * @param theMovieDBObject
     * @return
     */
    public boolean checkForAvailabilityInFavouriteMovies(TheMovieDBObject theMovieDBObject){

        if(favouriteMoviesSavedData == null){
            return false;
        }

        for(TheMovieDBObject theMovieDBObject1: favouriteMoviesSavedData){
            if(theMovieDBObject.equals(theMovieDBObject1)){
                return true;
            }
        }
        return false;
    }

}
