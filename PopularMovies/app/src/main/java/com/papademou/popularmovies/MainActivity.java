package com.papademou.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import static com.papademou.popularmovies.Constants.KEY_IS_FAVORITE;
import static com.papademou.popularmovies.Constants.KEY_MOVIE_ID;
import static com.papademou.popularmovies.Constants.KEY_OVERVIEW;
import static com.papademou.popularmovies.Constants.KEY_POSTER_PATH;
import static com.papademou.popularmovies.Constants.KEY_RELEASE_DATE;
import static com.papademou.popularmovies.Constants.KEY_TITLE;
import static com.papademou.popularmovies.Constants.KEY_VOTE_AVG;

public class MainActivity extends AppCompatActivity implements MoviePostersFragment.OnItemSelectedListener{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane = false;//assuming phone device by default
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDFTAG";
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) { //tablet
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    public Menu getMenu() {
        return mMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(int movieId, String title, String releaseDate, String posterPath, String overview, boolean isFavorite, double voteAvg) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(KEY_MOVIE_ID, movieId);
            arguments.putString(KEY_TITLE, title);
            arguments.putString(KEY_RELEASE_DATE, releaseDate);
            arguments.putString(KEY_POSTER_PATH, posterPath);
            arguments.putString(KEY_OVERVIEW, overview);
            arguments.putBoolean(KEY_IS_FAVORITE, isFavorite);
            arguments.putDouble(KEY_VOTE_AVG, voteAvg);

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(KEY_MOVIE_ID, movieId)
                    .putExtra(KEY_TITLE, title)
                    .putExtra(KEY_RELEASE_DATE, releaseDate)
                    .putExtra(KEY_POSTER_PATH, posterPath)
                    .putExtra(KEY_OVERVIEW, overview)
                    .putExtra(KEY_IS_FAVORITE, isFavorite)
                    .putExtra(KEY_VOTE_AVG, voteAvg);
            startActivity(intent);
        }

    }
}
