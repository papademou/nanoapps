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

import static com.papademou.popularmovies.Constants.API_KEY;
import static com.papademou.popularmovies.Constants.MOVIEDB_DISCOVER_BASE_URL;
import static com.papademou.popularmovies.Constants.TMDB_IMAGE_BASE_URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviePostersFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        updateMovieGrid();
    }

    private ImageAdapter imageAdapter;
    private TMDBMovie[] movies = new TMDBMovie[] {};

    public class TMDBMovie {

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
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

        private int id;
        private String title;
        private Date releaseDate;
        private String posterPath;
        private double voteAverage;
        private String overview;
    }

    public MoviePostersFragment() {
    }



    public void updateMovieGrid() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by_option = prefs.getString(getString(R.string.pref_sort_by_key), "popularity.desc");

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(sort_by_option);

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

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        imageAdapter = new ImageAdapter(getContext());
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TMDBMovie movie = (TMDBMovie) imageAdapter.getItem(position);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(TITLE_EXTRA, movie.getTitle())
                        .putExtra(RELEASE_DATE_EXTRA, df.format(movie.getReleaseDate()))
                        .putExtra(POSTER_PATH_EXTRA, movie.constructImagePath())
                        .putExtra(OVERVIEW_EXTRA, movie.getOverview())
                        .putExtra(VOTE_AVG_EXTRA, movie.getVoteAverage());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();*/
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

            Picasso.with(getContext()).load(movies[position].constructImagePath()).into(imageView);

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
                final String SORT_BY_KEY = "sort_by";

                Uri builtUri = Uri.parse(MOVIEDB_DISCOVER_BASE_URL).buildUpon()
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .appendQueryParameter(SORT_BY_KEY, params[0])
                        .build();

                String b = builtUri.toString();
                Log.i("TAG_TAG", builtUri.toString());
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
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
            imageAdapter = new ImageAdapter(getActivity());
        }
    }
}
