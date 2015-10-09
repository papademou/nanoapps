package com.papademou.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.papademou.popularmovies.adapter.MovieDetailAdapter;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.papademou.popularmovies.util.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.util.Constants.TMDB_BASE_URL;

/**
 * AsyncTask to fetch movie reviews from The Movie Database (TMDb), given the movie id
 */
public class FetchReviewsTask extends AsyncTask<String, Void, List<MovieReview>> {
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private MovieDetailAdapter mAdapter;

    public FetchReviewsTask(MovieDetailAdapter adapter) {
        super();
        mAdapter = adapter;
    }

    @Override
    protected List<MovieReview> doInBackground(String... params) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TMDbService service = retrofit.create(TMDbService.class);

        //params[0] contains the movie id
        Call<TMDbMovieResult<MovieReview>> call = service.getReviews(params[0], TMDB_API_KEY);
        TMDbMovieResult result = null;
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching movie reviews with Retrofit", e);
            return null;
        }

        return result.getResults();
    }

    @Override
    protected void onPostExecute(List<MovieReview> results) {
        if (null != results) {
            mAdapter.addReviews(results);
        }
    }
}

