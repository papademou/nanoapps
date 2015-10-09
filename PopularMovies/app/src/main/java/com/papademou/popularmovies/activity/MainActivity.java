package com.papademou.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.papademou.popularmovies.Movie;
import com.papademou.popularmovies.fragment.MovieDetailsFragment;
import com.papademou.popularmovies.fragment.MoviePostersFragment;
import com.papademou.popularmovies.R;

public class MainActivity extends AppCompatActivity
        implements MoviePostersFragment.OnItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane = false;//assuming handheld phone device by default
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDFTAG";
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //If tablet, load detail fragment in right pane
        if (findViewById(R.id.movie_detail_container) != null) { //tablet
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailsFragment(),
                                MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //keep track of the activity menu in order to change menu options later on
        // (e.g: display/hide "Share" menu item)
        mMenu = menu;
        return true;
    }

    /**
     * Can be used to retrieve the main activity menu (in AsyncTask for instance),
     * which will help in hiding/displaying share menu item if a movie has trailers
     * Being used right now in FetchTrailersTask.onPostExecute
     * Only applies to two-pane mode where the action bar is the same for both master and detail layouts
     * @return the activity menu
     */
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

    /**
     * After cliking on a poster in master layout, if:
     * - Tablet:
     *      pass a bundle with movie info to detail fragment loaded in detail pane
     * - Handheld device:
     *      start detail activity with movie info passed as intent
     */
    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Movie.KEY_MOVIE, movie);

            MovieDetailsFragment detailFragment = new MovieDetailsFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Movie.KEY_MOVIE, movie);
            startActivity(intent);
        }

    }
}
