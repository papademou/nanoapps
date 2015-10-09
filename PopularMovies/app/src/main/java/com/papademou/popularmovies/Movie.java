package com.papademou.popularmovies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import static com.papademou.popularmovies.util.Constants.TMDB_IMAGE_BASE_URL;

/**
 * Movie Summary class
 */
public class Movie extends MovieDetail implements Parcelable {
    /* Parcel Keys */
    public static final String KEY_MOVIE = "movie"; //when passing an entire movie parcel
    public static final String KEY_TMDB_ID = "the_movide_db_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_VOTE_AVG = "vote_avg";
    public static final String KEY_PLOT_SYNOPSIS = "plot_synopsis";

    @Getter @Setter @Expose @SerializedName("id")
    private int mTMDbMovieId; //movie id in "The Movie DB" database
    @Getter @Setter @Expose @SerializedName("title")
    private String mTitle;
    @Getter @Setter @Expose @SerializedName("release_date")
    private Date mReleaseDate;
    @Getter @Setter @Expose @SerializedName("poster_path")
    private String mPosterPath;
    @Getter @Setter @Expose @SerializedName("vote_average")
    private double mVoteAverage;
    @Getter @Setter @Expose @SerializedName("overview")
    private String mPlotSynopsis;
    @Getter @Setter
    private boolean mIsFavorite;

    public static final String RELEASE_DATE_FORMAT = "yyyy-MM-dd";//format in The Movie DB api json
    public static final String RELEASE_DATE_DISPLAY_FORMAT = "yyyy-MM-dd";//format displayed in detail fragment

    public Movie() {
        this.setType(Type.SUMMARY);
    }

    /* Formats the vote average for display on detail screen */
    public String getVoteAverageDisplay() {
        return mVoteAverage + "/10";
    }

    public String getFormattedReleaseDate() {
        if (mReleaseDate == null) {
            return "";
        }
        return new SimpleDateFormat(RELEASE_DATE_FORMAT).format(mReleaseDate);
    }

    /**
     * converts release date to unix timestamp
     * @return
     */
    public long getReleaseDateUnixTimestamp() {
        return mReleaseDate.getTime() / 1000;
    }


    /**
     * releaseDate setter from a String argument
     * @param strDate the String date
     */
    public void setStringReleaseDate(String strDate) {
        Date retDate;
        try {
            retDate = new SimpleDateFormat(RELEASE_DATE_FORMAT).parse(strDate);
        } catch (ParseException e) {
            retDate = null;
        }
        this.mReleaseDate = retDate;
    }

    /**
     * Construct the full poster path by prepending the base image URL
     */
    public String getFullImagePath() {
        return TMDB_IMAGE_BASE_URL + mPosterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putInt(KEY_TMDB_ID, mTMDbMovieId);
        bundle.putString(KEY_TITLE, mTitle);
        bundle.putSerializable(KEY_RELEASE_DATE, mReleaseDate);
        bundle.putString(KEY_POSTER_PATH, mPosterPath);
        bundle.putDouble(KEY_VOTE_AVG, mVoteAverage);
        bundle.putString(KEY_PLOT_SYNOPSIS, mPlotSynopsis);

        dest.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();

            Movie movie = new Movie();
            movie.setMTMDbMovieId(bundle.getInt(KEY_TMDB_ID));
            movie.setMTitle(bundle.getString(KEY_TITLE));
            movie.setMVoteAverage(bundle.getDouble(KEY_VOTE_AVG));
            movie.setMReleaseDate((Date) bundle.getSerializable(KEY_RELEASE_DATE));
            movie.setMPosterPath(bundle.getString(KEY_POSTER_PATH));
            movie.setMPlotSynopsis(bundle.getString(KEY_PLOT_SYNOPSIS));

            return movie;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

