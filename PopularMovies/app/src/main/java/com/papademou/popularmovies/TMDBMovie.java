package com.papademou.popularmovies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import static com.papademou.popularmovies.Constants.TMDB_IMAGE_BASE_URL;

/**
 * Created by Edem on 8/31/2015.
 */
public class TMDBMovie implements Parcelable {
    /* Parcel Keys */
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_VOTE_AVG = "vote_avg";
    private static final String KEY_OVERVIEW = "overview";

    private int id;
    private String title;
    private Date releaseDate;
    private String posterPath;
    private double voteAverage;
    private String overview;
    private boolean isFavorite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     * Construct the full poster path by prepending the base image URL
     */
    public String constructImagePath() {
        return TMDB_IMAGE_BASE_URL + posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putInt(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putSerializable(KEY_RELEASE_DATE, releaseDate);
        bundle.putString(KEY_POSTER_PATH, posterPath);
        bundle.putDouble(KEY_VOTE_AVG, voteAverage);
        bundle.putString(KEY_OVERVIEW, overview);

        dest.writeBundle(bundle);
    }

    public static final Parcelable.Creator<TMDBMovie> CREATOR = new Creator<TMDBMovie>() {
        @Override
        public TMDBMovie createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();

            TMDBMovie movie = new TMDBMovie();
            movie.setId(bundle.getInt(KEY_ID));
            movie.setTitle(bundle.getString(KEY_TITLE));
            movie.setVoteAverage(bundle.getDouble(KEY_VOTE_AVG));
            movie.setReleaseDate((Date) bundle.getSerializable(KEY_RELEASE_DATE));
            movie.setPosterPath(bundle.getString(KEY_POSTER_PATH));
            movie.setOverview(bundle.getString(KEY_OVERVIEW));

            return movie;
        }

        @Override
        public TMDBMovie[] newArray(int size) {
            return new TMDBMovie[size];
        }
    };
}

