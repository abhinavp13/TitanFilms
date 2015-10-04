package com.nanodegree.abhinav.titanfilms.dao;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * POJO class for movie data
 *
 * Created by Abhinav Puri.
 */
public class TheMovieDBObject {

    private String title;
    private String releaseDate;
    private String posterPath;
    private String voteAverage;
    private String voteCount;
    private String plotSynopsis;
    private String backdropPath;
    private HashMap<String, String> reviews;
    private String trailerLink;
    private String trailerLinkFetchingPath;
    private String reviewPath;
    private int[] genreIds;
    private int id;
    private boolean isSavedAsFavourite;

    private Bitmap bitmapPosterImage;
    private Bitmap bitmapBackdropImage;

    public Bitmap getBitmapPosterImage() {
        return bitmapPosterImage;
    }

    public void setBitmapPosterImage(Bitmap bitmapPosterImage) {
        this.bitmapPosterImage = bitmapPosterImage;
    }

    public Bitmap getBitmapBackdropImage() {
        return bitmapBackdropImage;
    }

    public void setBitmapBackdropImage(Bitmap bitmapBackdropImage) {
        this.bitmapBackdropImage = bitmapBackdropImage;
    }

    public boolean isSavedAsFavourite() {
        return isSavedAsFavourite;
    }

    public void setIsSavedAsFavourite(boolean isSavedAsFavourite) {
        this.isSavedAsFavourite = isSavedAsFavourite;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }

    public String getTrailerLinkFetchingPath() {
        return trailerLinkFetchingPath;
    }

    public void setTrailerLinkFetchingPath(String trailerLinkFetchingPath) {
        this.trailerLinkFetchingPath = trailerLinkFetchingPath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<String, String> getReviews() {
        return reviews;
    }

    public void setReviews(HashMap<String, String> reviews) {
        this.reviews = reviews;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }


    public String getReviewPath() {
        return reviewPath;
    }

    public void setReviewPath(String reviewPath) {
        this.reviewPath = reviewPath;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TheMovieDBObject) {
            TheMovieDBObject theMovieDBObject = (TheMovieDBObject)o;
            if(getTitle() == null) return  false;
            return (getTitle().equals(theMovieDBObject.getTitle()) &&
                    getReleaseDate().equals(theMovieDBObject.getReleaseDate()) &&
                    getVoteAverage().equals(theMovieDBObject.getVoteAverage()) &&
                    getPlotSynopsis().equals(theMovieDBObject.getPlotSynopsis()) &&
                    getVoteCount().equals(theMovieDBObject.getVoteCount()));
        }
        return false;
    }
}
