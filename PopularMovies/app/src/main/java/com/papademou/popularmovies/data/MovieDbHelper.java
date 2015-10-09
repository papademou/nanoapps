package com.papademou.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.papademou.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper{
    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "favorite_movies.db";
    private static final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY, " +
            MovieEntry.COLUMN_NAME_TMDB_MOVIE_ID + " INTEGER NOT NULL, " +
            MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS + " TEXT, " +
            MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT, " +
            MovieEntry.COLUMN_NAME_RELEASE_DATE + " INTEGER, " + //store dates as Integer UNIX Time
            MovieEntry.COLUMN_NAME_TITLE + " TEXT, " +
            MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
            MovieEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0" + //really a boolean
            ");";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Creating db " +DATABASE_NAME+ " with sql " + SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    /* Nothing fancy for this database upgrades/downgrades, drop and re-create */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Updating " +DATABASE_NAME+ " schema from version " +oldVersion+ " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
