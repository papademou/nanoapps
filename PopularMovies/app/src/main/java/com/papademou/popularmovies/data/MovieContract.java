package com.papademou.popularmovies.data;

import android.provider.BaseColumns;

/* Movie Table Contract */
public final class MovieContract {
    /*Private constructor to prevent the contract class from being instantiated
      With this private constructor, a compilation error will be thrown if instantiated
     */
    private MovieContract() {}

    public static final class MovieEntry implements BaseColumns {
        //table & column names
        public static final String TABLE_NAME = "movie";
        //not using the following column as _ID because it's external to the application.
        //We can trust, but not expect it to be unique
        public static final String COLUMN_NAME_TMDB_MOVIE_ID = "tmdb_movie_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        public static final String COLUMN_NAME_PLOT_SYNOPSIS = "plot_synopsis";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_IS_FAVORITE = "is_fave";
    }
}
