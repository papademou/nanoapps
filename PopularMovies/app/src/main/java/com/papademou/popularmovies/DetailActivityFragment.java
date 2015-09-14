package com.papademou.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;

import static com.papademou.popularmovies.Constants.KEY_MOVIE_ID;
import static com.papademou.popularmovies.Constants.KEY_OVERVIEW;
import static com.papademou.popularmovies.Constants.KEY_POSTER_PATH;
import static com.papademou.popularmovies.Constants.KEY_RELEASE_DATE;
import static com.papademou.popularmovies.Constants.KEY_TITLE;
import static com.papademou.popularmovies.Constants.KEY_VOTE_AVG;
import static com.papademou.popularmovies.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.Constants.TMDB_REVIEWS_URL;
import static com.papademou.popularmovies.Constants.TMDB_TRAILER_URL;
import static com.papademou.popularmovies.Constants.YOUTUBE_THUMBNAIL_URI;

/**
 * Detail Activity
 * TODO:
 *  create self-contained adapater classes in separate files
 */
public class DetailActivityFragment extends Fragment {

    private ArrayList<Trailer> mTrailers = new ArrayList<Trailer>();
    private ArrayList<Review> mReviews = new ArrayList<Review>();
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final Intent intent = getActivity().getIntent();
        if (intent != null) {
            final Bundle bundle = intent.getExtras();

            if (intent.hasExtra(KEY_MOVIE_ID)) {
                FetchTrailersTask trailersTask = new FetchTrailersTask();
                mTrailerAdapter = new TrailerAdapter(getContext(), R.layout.trailer_row, mTrailers);
                ListView trailerListView = (ListView) rootView.findViewById(R.id.list_view_trailers);
                trailerListView.setAdapter(mTrailerAdapter);
                trailersTask.execute(Integer.toString(bundle.getInt(KEY_MOVIE_ID)));

                //play youtube trailers
                trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Trailer trailer = (Trailer) mTrailerAdapter.getItem(position);
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                            startActivity(intent);
                        }
                    }
                });

                FetchReviewsTask reviewsTask = new FetchReviewsTask();
                mReviewAdapter = new ReviewAdapter(getContext(), R.layout.reviews_row, mReviews);
                ListView reviewListView = (ListView) rootView.findViewById(R.id.list_view_reviews);
                reviewListView.setAdapter(mReviewAdapter);
                reviewsTask.execute(Integer.toString(bundle.getInt(KEY_MOVIE_ID)));

                final Button btn_favorite = (Button) rootView.findViewById(R.id.btn_favorite);
                /*final boolean isFavorite = intent.hasExtra(KEY_IS_FAVORITE) && bundle.getBoolean(KEY_IS_FAVORITE)==true;
                if (isFavorite) {
                    btn_favorite.setText(getString(R.string.btn_favorite_remove));
                }*/
                btn_favorite.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Insert into database
                        Uri mNewUri;
                        ContentValues mNewValues = new ContentValues();

                        try {
                            /* TODO: work on logic to change button to remove from faves if the movie
                            * was already marked as favorites
                             */
                            /*if (isFavorite) {
                                String mSelectionClause = MovieContract.MovieEntry.COLUMN_ID + " LIKE ?";
                                String[] mSelectionArgs = {""+bundle.getInt(KEY_MOVIE_ID)};

                                int mRowsDeleted = 0;
                                mRowsDeleted = getContext().getContentResolver().delete(
                                        MovieContract.MovieEntry.CONTENT_URI,
                                        mSelectionClause,
                                        mSelectionArgs
                                );

                                btn_favorite.setText(getString(R.string.btn_favorite_add));

                            } else {*/
                                mNewValues.put(MovieContract.MovieEntry.COLUMN_ID, bundle.getInt(KEY_MOVIE_ID));
                                mNewValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, bundle.getString(KEY_OVERVIEW));
                                mNewValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, bundle.getString(KEY_POSTER_PATH));
                                mNewValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, bundle.getString(KEY_RELEASE_DATE));
                                mNewValues.put(MovieContract.MovieEntry.COLUMN_TITLE, bundle.getString(KEY_TITLE));
                                mNewValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, bundle.getDouble(KEY_VOTE_AVG));

                                mNewUri = getContext().getContentResolver().insert(
                                        MovieContract.MovieEntry.CONTENT_URI,
                                        mNewValues
                                );
                            //}
                            //this has been added to favorites, now change button text
                            btn_favorite.setText(getString(R.string.btn_favorite_remove));
                        } catch(Exception e) {
                            Log.e("DetailActivityFragment", "Exception ", e);
                        }
                    }
                });

            }
            if (intent.hasExtra(KEY_TITLE)) {
                ((TextView) rootView.findViewById(R.id.movie_title))
                        .setText(bundle.getString(KEY_TITLE));
            }
            if (intent.hasExtra(KEY_POSTER_PATH)) {
                ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load(bundle.getString(KEY_POSTER_PATH)).into(posterImageView);
            }
            if (intent.hasExtra(KEY_RELEASE_DATE)){
                ((TextView) rootView.findViewById(R.id.release_date))
                        .setText(bundle.getString(KEY_RELEASE_DATE));
            }
            if (intent.hasExtra(KEY_VOTE_AVG)){
                ((TextView) rootView.findViewById(R.id.vote_avg))
                        .setText(Double.toString(bundle.getDouble(KEY_VOTE_AVG)) + "/10");
            }
            if (intent.hasExtra(KEY_OVERVIEW)){
                ((TextView) rootView.findViewById(R.id.plot_synopsis))
                        .setText(bundle.getString(KEY_OVERVIEW));
            }

        }

        return rootView;
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<Trailer>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private ArrayList<Trailer> getTrailersFromJson(String responseJsonStr) throws JSONException, ParseException {
            final String KEY_VIDEO_KEY = "key";
            final String KEY_VIDEO_NAME = "name";

            JSONObject responseJson = new JSONObject(responseJsonStr);
            JSONArray results = responseJson.getJSONArray("results");

            ArrayList<Trailer> trailers = new ArrayList<Trailer>();

            for (int i=0; i<results.length(); i++) {
                JSONObject result = results.getJSONObject(i);

                Trailer trailer = new Trailer(result.getString(KEY_VIDEO_KEY), result.getString(KEY_VIDEO_NAME));
                trailers.add(trailer);
            }
            return trailers;
        }

        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {
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
        protected void onPostExecute(ArrayList<Trailer> results) {
            mTrailers = results;
            mTrailerAdapter.clear();
            for(Trailer trailer: results) {
                mTrailerAdapter.add(trailer);
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {
        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private ArrayList<Review> getReviewsFromJson(String responseJsonStr) throws JSONException, ParseException {
            final String KEY_REVIEW_CONTENT = "content";

            JSONObject responseJson = new JSONObject(responseJsonStr);
            JSONArray results = responseJson.getJSONArray("results");

            ArrayList<Review> reviews = new ArrayList<Review>();

            for (int i=0; i<results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                Review review = new Review(result.getString(KEY_REVIEW_CONTENT));
                reviews.add(review);
            }
            return reviews;
        }

        @Override
        protected ArrayList<Review> doInBackground(String... params) {
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
        protected void onPostExecute(ArrayList<Review> results) {
            mReviews = results;
            mReviewAdapter.clear();
            for(Review review: results) {
                mReviewAdapter.add(review);
            }
        }
    }
}

class TrailerAdapter extends ArrayAdapter<Trailer> {
    Context context;
    int layoutResourceId;
    ArrayList<Trailer> data;

    public TrailerAdapter(Context context, int layoutResourceId, ArrayList<Trailer> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    public void setData(ArrayList<Trailer> data) {
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        TrailerHolder view;

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, parent, false);

            view = new TrailerHolder();
            view.trailer_thumb = (ImageView) rowView.findViewById(R.id.trailer_thumb);
            //view.trailer_title = (TextView) rowView.findViewById(R.id.trailer_title);
            //view.trailer_second_tv = (TextView) rowView.findViewById(R.id.trailer_second_tv);

            rowView.setTag(view);
        } else {
            view = (TrailerHolder) rowView.getTag();
        }

        //Set data to your Views.
        Trailer trailer = data.get(position);
        //view.trailer_title.setText(trailer.getName());
        //view.trailer_second_tv.setText("text");
        String thumbnailSrc = YOUTUBE_THUMBNAIL_URI.replace("{VIDEO_ID}",trailer.getKey());
        Picasso.with(context).load(thumbnailSrc).into(view.trailer_thumb);

        return rowView;
    }

    protected static class TrailerHolder{
        protected ImageView trailer_thumb;
        //protected TextView trailer_second_tv;
        protected TextView trailer_title;
    }
}



class ReviewAdapter extends ArrayAdapter<Review> {
    Context context;
    int layoutResourceId;
    ArrayList<Review> data;

    public ReviewAdapter(Context context, int layoutResourceId, ArrayList<Review> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    public void setData(ArrayList<Review> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data!=null ? data.size() : 0;
    }

    @Override
    public int getPosition(Review item) {
        return data.indexOf(item);
    }

    @Override
    public Review getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ReviewHolder view;

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, parent, false);

            view = new ReviewHolder();
            view.content = (TextView) rowView.findViewById(R.id.review_content);

            rowView.setTag(view);
        } else {
            view = (ReviewHolder) rowView.getTag();
        }

        Review review = data.get(position);
        view.content.setText(review.getContent());

        return rowView;
    }

    private class ReviewHolder{
        TextView content;
    }
}