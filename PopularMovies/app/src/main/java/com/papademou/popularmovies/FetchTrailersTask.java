package com.papademou.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.papademou.popularmovies.activity.DetailActivity;
import com.papademou.popularmovies.activity.MainActivity;
import com.papademou.popularmovies.adapter.MovieDetailAdapter;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.papademou.popularmovies.util.Constants.TMDB_API_KEY;
import static com.papademou.popularmovies.util.Constants.TMDB_BASE_URL;

/**
 * AsyncTask to fetch info about movie trailers from The Movie Database (TMDb), given a movie id
 */
public class FetchTrailersTask extends AsyncTask<String, Void, MovieTrailers> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private MovieDetailAdapter mDetailAdapter;
    private Activity mActivity;
    public FetchTrailersTask(Activity activity, MovieDetailAdapter adapter) {
        super();
        mDetailAdapter = adapter;
        mActivity = activity;
    }

    @Override
    protected MovieTrailers doInBackground(String... params) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TMDbService service = retrofit.create(TMDbService.class);

        //params[0] contains the movie id
        Call<TMDbMovieResult<MovieTrailer>> call = service.getTrailers(params[0], TMDB_API_KEY);
        TMDbMovieResult result = null;
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching movie trailers with Retrofit", e);
            return null;
        }

        return new MovieTrailers(result.getResults());
    }

    @Override
    protected void onPostExecute(MovieTrailers results) {
        if (results != null) {
            //notify and pass collection of movie trailers to adapter
            mDetailAdapter.addTrailers(results);

            /*
            Display and build intent for share menu item if trailers are found
             */
            if (mActivity != null) {
                Menu menu = mActivity instanceof MainActivity ? ((MainActivity) mActivity).getMenu()
                        : ((DetailActivity) mActivity).getMenu();
                if (menu != null) {
                    MenuItem shareMenuItem = menu.findItem(R.id.action_share_trailer);
                    if (shareMenuItem != null) {
                        boolean visibility = false;
                        if (results != null && results.getMTrailers().size() > 0) {
                                ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
                                if (shareActionProvider != null) {
                                    //Build intent to send the url of the first trailer
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    intent.putExtra(Intent.EXTRA_TEXT, results.getMTrailers().get(0).getUri());
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
