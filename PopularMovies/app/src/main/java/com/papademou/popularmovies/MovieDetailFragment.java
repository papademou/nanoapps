package com.papademou.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

import static com.papademou.popularmovies.Constants.KEY_MOVIE_ID;
import static com.papademou.popularmovies.Constants.KEY_OVERVIEW;
import static com.papademou.popularmovies.Constants.KEY_POSTER_PATH;
import static com.papademou.popularmovies.Constants.KEY_RELEASE_DATE;
import static com.papademou.popularmovies.Constants.KEY_TITLE;
import static com.papademou.popularmovies.Constants.KEY_VOTE_AVG;
import static com.papademou.popularmovies.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.Constants.TMDB_REVIEWS_URL;

/**
 * Detail Activity
 * TODO:
 *  create self-contained adapater classes in separate files
 */
public class MovieDetailFragment extends Fragment {

    private ArrayList<MovieDetail> mDetails = new ArrayList<MovieDetail>();
    private MovieDetailAdapter mDetailAdapter;
    private ArrayList<MovieTrailer> mTrailers = new ArrayList<MovieTrailer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final Bundle bundle = getArguments();
        if (bundle == null) return rootView;

        MovieSummary summaryDetails = new MovieSummary();
        summaryDetails.setmMovieId(bundle.getInt(KEY_MOVIE_ID));
        summaryDetails.setmTitle(bundle.getString(KEY_TITLE));
        summaryDetails.setmOverview(bundle.getString(KEY_OVERVIEW));
        summaryDetails.setmPosterPath(bundle.getString(KEY_POSTER_PATH));
        summaryDetails.setmReleaseDate(bundle.getString(KEY_RELEASE_DATE));
        summaryDetails.setmVoteAvg(bundle.getDouble(KEY_VOTE_AVG));
        mDetails.add(summaryDetails);

        //create adapter
        mDetailAdapter = new MovieDetailAdapter(getContext(), mDetails, bundle.getInt(KEY_MOVIE_ID));
        ((ListView) rootView.findViewById(R.id.listview_movie_detail)).setAdapter(mDetailAdapter);
        //Fetch Trailers
        FetchTrailersTask trailersTask = new FetchTrailersTask(this.getActivity(), mDetailAdapter);
        trailersTask.execute(Integer.toString(bundle.getInt(KEY_MOVIE_ID)));
        //Fetch reviews
        FetchReviewsTask reviewsTask = new FetchReviewsTask();
        reviewsTask.execute(Integer.toString(bundle.getInt(KEY_MOVIE_ID)));

        return rootView;
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<MovieReview>> {
        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private ArrayList<MovieReview> getReviewsFromJson(String responseJsonStr) throws JSONException, ParseException {
            final String KEY_REVIEW_CONTENT = "content";

            JSONObject responseJson = new JSONObject(responseJsonStr);
            JSONArray results = responseJson.getJSONArray("results");

            ArrayList<MovieReview> reviews = new ArrayList<MovieReview>();

            for (int i=0; i<results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                MovieReview review = new MovieReview();
                review.setmContent(result.getString(KEY_REVIEW_CONTENT));
                reviews.add(review);
            }
            return reviews;
        }

        @Override
        protected ArrayList<MovieReview> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;

            try{
                final String PARAM_API_KEY = "api_key";

                Uri builtUri = Uri.parse(TMDB_REVIEWS_URL.replace("{id}", params[0])).buildUpon()
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
                return getReviewsFromJson(responseJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieReview> results) {
            mDetailAdapter.addReviews(results);
        }
    }
}



