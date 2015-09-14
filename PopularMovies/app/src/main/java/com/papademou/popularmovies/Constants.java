package com.papademou.popularmovies;

/**
 * Key constants used in the application
 */
class Constants {
    public static final String TMDB_API_KEY = "Enter your key here";
    public static final String TMDB_DISCOVER_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
    public static final String TMDB_TRAILER_URL = "http://api.themoviedb.org/3/movie/{id}/videos";
    public static final String TMDB_REVIEWS_URL = "http://api.themoviedb.org/3/movie/{id}/reviews";
    public static final String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String YOUTUBE_THUMBNAIL_URI = "http://img.youtube.com/vi/{VIDEO_ID}/default.jpg";
    /* Keys for movie details extras */
    public static final String KEY_MOVIE_ID = "ID";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_RELEASE_DATE = "RELEASE_DATE";
    public static final String KEY_POSTER_PATH = "POSTER_PATH";
    public static final String KEY_OVERVIEW = "OVERVIEW";
    public static final String KEY_VOTE_AVG = "VOTE_AVG";
    public static final String KEY_IS_FAVORITE = "IS_FAVORITE";
}
