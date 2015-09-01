package com.papademou.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static com.papademou.popularmovies.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.Constants.TMDB_DISCOVER_BASE_URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviePostersFragment extends Fragment {

    private static final String KEY_MOVIES_PARCEL = "movies";
    private ImageAdapter imageAdapter;
    private TMDBMovie[] movies;
    //use field below to store the sort by setting and track related changes
    private String mSortByOption;

    public void updateMovieGrid() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortByOption = prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value));

        if (null != mSortByOption && sortByOption == mSortByOption) {
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
        outState.putParcelableArray(KEY_MOVIES_PARCEL, movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String TITLE_EXTRA = "TITLE";
        final String RELEASE_DATE_EXTRA = "RELEASE_DATE";
        final String POSTER_PATH_EXTRA = "POSTER_PATH";
        final String OVERVIEW_EXTRA = "OVERVIEW";
        final String VOTE_AVG_EXTRA = "VOTE_AVG";

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        imageAdapter = new ImageAdapter(getActivity());

        if (savedInstanceState != null) {
            movies = (TMDBMovie []) savedInstanceState.getParcelableArray(KEY_MOVIES_PARCEL);
        } else {
            movies = new TMDBMovie[] {};
            //updateMovieGrid();
        }

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TMDBMovie movie = (TMDBMovie) imageAdapter.getItem(position);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                Date releaseDate = movie.getReleaseDate();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(TITLE_EXTRA, movie.getTitle())
                        .putExtra(RELEASE_DATE_EXTRA, releaseDate == null ? "" : df.format(releaseDate))
                        .putExtra(POSTER_PATH_EXTRA, movie.constructImagePath())
                        .putExtra(OVERVIEW_EXTRA, movie.getOverview())
                        .putExtra(VOTE_AVG_EXTRA, movie.getVoteAverage());
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
            return movies.length;
        }

        public Object getItem(int position) {
            return movies[position];
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

            Picasso.with(getActivity()).load(movies[position].constructImagePath()).into(imageView);

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
                if (dateStr == null || dateStr == "null") {
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
                final String PARAM_API_KEY = "api_key";

                Uri builtUri = Uri.parse(TMDB_DISCOVER_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_API_KEY, TMDB_API_KEY)
                        .appendQueryParameter(getString(R.string.pref_sort_by_key), params[0])
                        .build();

                String b = builtUri.toString();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                responseJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
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
            movies = results;
            imageAdapter.notifyDataSetChanged();
        }
    }
}
