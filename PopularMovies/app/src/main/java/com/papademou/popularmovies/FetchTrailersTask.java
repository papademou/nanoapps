package com.papademou.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import java.util.ArrayList;

import static com.papademou.popularmovies.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.Constants.TMDB_TRAILER_URL;

public class FetchTrailersTask extends AsyncTask<String, Void, MovieTrailers> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private MovieDetailAdapter mDetailAdapter;
    private Activity mActivity;
    public FetchTrailersTask(Activity activity, MovieDetailAdapter adapter) {
        super();
        mDetailAdapter = adapter;
        mActivity = activity;
    }

    private MovieTrailers getTrailersFromJson(String responseJsonStr) throws JSONException, ParseException {
        final String KEY_VIDEO_KEY = "key";
        final String KEY_VIDEO_NAME = "name";

        JSONObject responseJson = new JSONObject(responseJsonStr);
        JSONArray results = responseJson.getJSONArray("results");

        ArrayList<MovieTrailer> trailers = new ArrayList<MovieTrailer>();

        for (int i=0; i<results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            MovieTrailer trailer = new MovieTrailer();
            trailer.setmKey(result.getString(KEY_VIDEO_KEY));
            trailer.setmName(result.getString(KEY_VIDEO_NAME));
            trailers.add(trailer);
        }
        MovieTrailers movieTrailers = new MovieTrailers();
        movieTrailers.setmTrailers(trailers);
        return movieTrailers;
    }

    @Override
    protected MovieTrailers doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String responseJsonStr = null;

        try{
            final String PARAM_API_KEY = "api_key";

            Uri builtUri = Uri.parse(TMDB_TRAILER_URL.replace("{id}", params[0])).buildUpon()
                    .appendQueryParameter(PARAM_API_KEY, TMDB_API_KEY)
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
            return getTrailersFromJson(responseJsonStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(MovieTrailers results) {
        if (results != null) {
            //notify and pass collection of movie trailers to adapter
            mDetailAdapter.addTrailers(results);

            /*
            Display and build share menu item functionality if trailers are found
             */
            if (mActivity != null) {
                Menu menu = mActivity instanceof MainActivity ? ((MainActivity) mActivity).getMenu()
                        : ((DetailActivity) mActivity).getMenu();
                if (menu != null) {
                    MenuItem shareMenuItem = menu.findItem(R.id.action_share_trailer);
                    if (shareMenuItem != null) {
                        boolean visibility = false;
                        if (results != null && results.getmTrailers().size() > 0) {
                                ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
                                if (shareActionProvider != null) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    intent.putExtra(Intent.EXTRA_TEXT, results.getmTrailers().get(0).getUri());
                                    shareActionProvider.setShareIntent(intent);
                                }
                            visibility = true;
                        }
                        shareMenuItem.setVisible(visibility);
                    }
                }
            }
        }
    }
}
