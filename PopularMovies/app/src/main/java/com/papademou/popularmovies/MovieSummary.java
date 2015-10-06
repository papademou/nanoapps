package com.papademou.popularmovies;

import static com.papademou.popularmovies.Constants.TMDB_IMAGE_BASE_URL;

public class MovieSummary extends MovieDetail {
    private String mTitle;

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public double getmVoteAvg() {
        return mVoteAvg;
    }

    public void setmVoteAvg(double mVoteAvg) {
        this.mVoteAvg = mVoteAvg;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public int getmMovieId() {
        return mMovieId;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }

    private int mMovieId;
    private String mPosterPath;
    private String mReleaseDate;
    private double mVoteAvg;
    private String mOverview;
    public MovieSummary() {
        this.setType(Type.SUMMARY);
    }
    public String constructImagePath() {
        return TMDB_IMAGE_BASE_URL + mPosterPath;
    }
}
