package com.papademou.popularmovies;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.papademou.popularmovies.adapter.PostersAdapter;
import com.papademou.popularmovies.data.MovieContract;
import com.papademou.popularmovies.data.MovieProvider;
import com.papademou.popularmovies.util.Utility;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.papademou.popularmovies.util.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.util.Constants.TMDB_BASE_URL;

/**
 * AsyncTask to fetch movies from The Movie Database (TMDb), sorted by user preference
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Context mContext;
    private PostersAdapter mAdapter;

    public FetchMoviesTask(Context context, PostersAdapter adapter) {
        super();
        mContext = context;
        mAdapter = adapter;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params[0].equals(mContext.getString(R.string.pref_sort_by_favorites_value))) {
            //If the user has selected favorites, fetch movies from database
            //Right now, only favorite movies are saved in the db
            return getFavoriteMovies();
        } else {
            //Fetch movies from "The Movie DB" API, sorting by the user preference
            Gson gson = new GsonBuilder()
                    //.setDateFormat(Movie.RELEASE_DATE_DISPLAY_FORMAT)
                    //register custom date deserializer to prevent exception when release date is empty
                    .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        DateFormat df = new SimpleDateFormat(Movie.RELEASE_DATE_FORMAT);
                        @Override
                        public Date deserialize(final JsonElement json, final Type typeOfT,
                                                final JsonDeserializationContext context)
                                throws JsonParseException {
                            try {
                                return df.parse(json.getAsString());
                            } catch (ParseException e) {
                                return null;
                            }
                        }
                    })
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            TMDbService service = retrofit.create(TMDbService.class);

            //params[0] contains the "sort by" user preference
            Call<TMDbMovieResult<Movie>> call = service.getMovies(TMDB_API_KEY, params[0]);
            TMDbMovieResult result = null;
            try {
                result = call.execute().body();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error fetching movies with Retrofit", e);
                return null;
            }
            return result.getResults();
        }
    }

    /**
     * Fetches favorite movies from databse
     * @return list of movies
     */
    private List<Movie> getFavoriteMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        String[] projection = {
                MovieContract.MovieEntry.COLUMN_NAME_TMDB_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS,
                MovieContract.MovieEntry.COLUMN_NAME_TITLE,
                MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_IS_FAVORITE
        };

        Cursor cursor = mContext.getContentResolver().query(
                MovieProvider.CONTENT_URI, projection,
                null, null, null);

        if (null != cursor && cursor.getCount() >= 1) {
            while(cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setMTMDbMovieId(cursor.getInt(
                        cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_TMDB_MOVIE_ID)));
                movie.setMTitle(cursor.getString(
                        cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_TITLE)));
                movie.setMReleaseDate(Utility.convertUnixTime(
                        cursor.getLong(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE))));
                movie.setMPosterPath(cursor.getString(
                        cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH)));
                movie.setMVoteAverage(cursor.getDouble
                        (cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                movie.setMPlotSynopsis(cursor.getString
                        (cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS)));
                movie.setMIsFavorite(cursor.getInt
                        (cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_IS_FAVORITE)) != 0);
                movies.add(movie);
            }
        }
        cursor.close();

        return movies;
    }

    @Override
    protected void onPostExecute(List<Movie> results) {
        if (null != results) {
            //add results and notify adapter
            List<Movie> movies = mAdapter.getItems();
            movies.clear();
            movies.addAll(results);
            mAdapter.notifyDataSetChanged();
        }
    }
}