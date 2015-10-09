package com.papademou.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MovieProvider extends ContentProvider{
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    public static final String CONTENT_AUTHORITY = "com.papademou.popularmovies.provider";
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_SINGLE_MOVIE = "movie";
    public static final String PATH_SINGLE_MOVIE_PATTERN = PATH_SINGLE_MOVIE + "/#";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
    public static final Uri CONTENT_ITEM_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_SINGLE_MOVIE).build();
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SINGLE_MOVIE;
    public static final int CODE_MOVIES = 100; //multiple movie rows
    public static final int CODE_SINGLE_MOVIE = 200; //single row
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //below will match multiple rows. e.g: content://com.papademou.popularmovies.provider/movies
        matcher.addURI(CONTENT_AUTHORITY, PATH_MOVIES, CODE_MOVIES);
        //below will match single row. e.g: content://com.papademou.popularmovies.provider/movie/1
        matcher.addURI(CONTENT_AUTHORITY, PATH_SINGLE_MOVIE_PATTERN, CODE_SINGLE_MOVIE);
        return matcher;
    }

    public static Uri buildMovieUri(long id) {
        return ContentUris.withAppendedId(CONTENT_ITEM_URI, id);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES: {
                break;
            }
            case CODE_SINGLE_MOVIE: {
                //retrieve row based on the movie id from "The Movie DB" API
                selection = (selection==null ? "" : selection) + MovieContract.MovieEntry.COLUMN_NAME_TMDB_MOVIE_ID + "="
                        + uri.getLastPathSegment();
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        try {
            retCursor = mDbHelper.getReadableDatabase().query(
                    MovieContract.MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        } catch(Exception e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
            return null;
        }
        //notify potential listeners
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case CODE_MOVIES: {
                return CONTENT_TYPE;
            }
            case CODE_SINGLE_MOVIE: {
                return CONTENT_ITEM_TYPE;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    /**
     * Passing null @param selection will delete all rows
     */
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        // passing 1 as whereClause (or selection here) will delete all rows
        if ( null == selection ) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            int rowsUpdated;

            switch (sUriMatcher.match(uri)) {
                case CODE_MOVIES:
                    rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
