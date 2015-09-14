package com.papademou.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.papademou.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.papademou.popularmovies.Constants.KEY_IS_FAVORITE;
import static com.papademou.popularmovies.Constants.KEY_MOVIE_ID;
import static com.papademou.popularmovies.Constants.KEY_OVERVIEW;
import static com.papademou.popularmovies.Constants.KEY_POSTER_PATH;
import static com.papademou.popularmovies.Constants.KEY_RELEASE_DATE;
import static com.papademou.popularmovies.Constants.KEY_TITLE;
import static com.papademou.popularmovies.Constants.KEY_VOTE_AVG;
import static com.papademou.popularmovies.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.Constants.TMDB_DISCOVER_BASE_URL;

/**
 * TODO: implement loader, move AsyncTask to external class and save everything in database
 */
public class MoviePostersFragment extends Fragment {

    private static final String KEY_MOVIES_PARCEL = "movies";
    private ImageAdapter mAdapter;
    private TMDBMovie[] mMovies;
    private String mSortByOption; //to store the sort by setting and track related changes

    private void updateMovieGrid() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortByOption = prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value));

        if (null != mSortByOption && sortByOption.equals(mSortByOption)) {
            return; //we only want to fetch a new movie list if the sort preference has changed (after the activity has started)
        }

        mSortByOption = sortByOption;
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(sortByOption);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieGrid(); //attempt to refresh the movie grid each time the activity starts
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(KEY_MOVIES_PARCEL, mMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new ImageAdapter(getActivity());

        if (savedInstanceState != null) {
            mMovies = (TMDBMovie []) savedInstanceState.getParcelableArray(KEY_MOVIES_PARCEL);
        } else {
            mMovies = new TMDBMovie[] {};
        }

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TMDBMovie movie = (TMDBMovie) mAdapter.getItem(position);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                Date releaseDate = movie.getReleaseDate();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(KEY_MOVIE_ID, movie.getId())
                        .putExtra(KEY_TITLE, movie.getTitle())
                        .putExtra(KEY_RELEASE_DATE, releaseDate == null ? "" : df.format(releaseDate))
                        .putExtra(KEY_POSTER_PATH, movie.constructImagePath())
                        .putExtra(KEY_OVERVIEW, movie.getOverview())
                        .putExtra(KEY_IS_FAVORITE, movie.getIsFavorite())
                        .putExtra(KEY_VOTE_AVG, movie.getVoteAverage());
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mMovies.length;
        }

        public Object getItem(int position) {
            return mMovies[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setAdjustViewBounds(true);
            } else {
                imageView = (ImageView) convertView;
            }

            Picasso.with(getActivity()).load(mMovies[position].constructImagePath()).into(imageView);

            return imageView;
        }

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, TMDBMovie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private TMDBMovie[] getMoviesFromJson(String responseJsonStr) throws JSONException, ParseException {
            final String ID_KEY = "id";
            final String OVERVIEW_KEY = "overview";
            final String RELEASE_DATE_KEY = "release_date";
            final String POSTER_PATH_KEY = "poster_path";
            final String TITLE_KEY = "title";
            final String VOTE_AVG_KEY = "vote_average";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");

            JSONObject responseJson = new JSONObject(responseJsonStr);
            JSONArray results = responseJson.getJSONArray("results");

            TMDBMovie[] movies = new TMDBMovie[results.length()];

            for (int i=0; i<results.length(); i++) {
                JSONObject result = results.getJSONObject(i);

                TMDBMovie movie = new TMDBMovie();
                movie.setId(result.getInt(ID_KEY));
                movie.setTitle(result.getString(TITLE_KEY));
                String dateStr = result.getString(RELEASE_DATE_KEY);
                if (dateStr == null || dateStr.equals("null")) {
                    movie.setReleaseDate(null);
                } else {
                    movie.setReleaseDate(format.parse(dateStr));
                }
                movie.setPosterPath(result.getString(POSTER_PATH_KEY));
                movie.setVoteAverage(result.getDouble(VOTE_AVG_KEY));
                movie.setOverview(result.getString(OVERVIEW_KEY));

                movies[i] = movie;
            }
            return movies;
        }

        @Override
        protected TMDBMovie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String responseJsonStr = null;

            try{
                //If the user has selected favorites, read from database
                if (params[0].equals(getString(R.string.pref_sort_by_favorites_value))) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                    TMDBMovie[] mMovies = null;
                    String[] mProjection = {
                            MovieContract.MovieEntry.COLUMN_ID,
                            MovieContract.MovieEntry.COLUMN_OVERVIEW,
                            MovieContract.MovieEntry.COLUMN_TITLE,
                            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
                            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                            //MovieContract.MovieEntry.COLUMN_IS_FAVORITE
                    };

                    Cursor mCursor = getContext().getContentResolver().query(
                            MovieContract.MovieEntry.CONTENT_URI,
                            mProjection,
                            null,
                            null,
                            null);
                    if (null != mCursor && mCursor.getCount() >= 1) {
                        mMovies = new TMDBMovie[mCursor.getCount()];
                        while(mCursor.moveToNext()) {
                            TMDBMovie mMovie = new TMDBMovie();
                            mMovie.setId(mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_ID)));
                            mMovie.setTitle(mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE)));
                            mMovie.setReleaseDate(format.parse(mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RELEASE_DATE))));
                            mMovie.setPosterPath(mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
                            mMovie.setVoteAverage(mCursor.getDouble(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_VOTE_AVG)));
                            mMovie.setOverview(mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                            //mMovie.setIsFavorite(mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_IS_FAVORITE)) != 0);
                            mMovies[mCursor.getPosition()] = mMovie;
                        }
                    }
                    mCursor.close();

                    return mMovies;

                }

                final String PARAM_API_KEY = "api_key";

                Uri builtUri = Uri.parse(TMDB_DISCOVER_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_API_KEY, TMDB_API_KEY)
                        .appendQueryParameter(getString(R.string.pref_sort_by_key), params[0])
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder sb = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }

                if (sb.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                responseJsonStr = sb.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } catch (ParseException e) {
                Log.e(LOG_TAG, "ParseException ", e);
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesFromJson(responseJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(TMDBMovie[] results) {
            mMovies = results;
            mAdapter.notifyDataSetChanged();
        }
    }
}
