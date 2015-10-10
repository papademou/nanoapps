package com.papademou.popularmovies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.papademou.popularmovies.Movie;
import com.papademou.popularmovies.fragment.DetailsFragment;
import com.papademou.popularmovies.fragment.PostersFragment;
import com.papademou.popularmovies.R;
import com.papademou.popularmovies.util.Utility;

public class MasterActivity extends AppCompatActivity
        implements PostersFragment.OnItemSelectedListener {

    private static final String LOG_TAG = MasterActivity.class.getSimpleName();
    private boolean mTwoPane = false;//assuming handheld phone device by default
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDFTAG";
    private String mSortByOption; //to store the sort by setting and track related changes
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        //If tablet, load detail fragment in right pane
        if (findViewById(R.id.movie_detail_container) != null) { //tablet
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailsFragment(),
                                MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePostersGrid();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mSortByOption = Utility.getPreferenceValue(this,
                getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value));
    }

    /**
     * We want this application to be as fast as possible. We don't want the movie posters grid to
     * be updated every time this activity resumes. Instead, a new list will be pulled from internet
     * only if the preference has changed, unless we are displaying favorites in one pane mode
     * in which case favorites will be fetched from the database each time we get back to the screen,
     * because each transition from the detail activity back to the main activity can update the favorites collection.
     */
    private void updatePostersGrid() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //if there is no connection, default to favorites mode (fetched from database)
        String sortByOption = Utility.hasInternetConnection(this)
                ? prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value))
                : getString(R.string.pref_sort_by_favorites_value);

        if (null != mSortByOption
                && !(!mTwoPane && mSortByOption.equals(getString(R.string.pref_sort_by_favorites_value))) //not favorites in single pane mode
                && sortByOption.equals(mSortByOption)) {
            return; //no need to update grid in these conditions
        }

        //when not connected, inform user that we are switching to "favorites" regardless of "sort by" user preference
        if(!Utility.hasInternetConnection(this)) {
            Toast.makeText(this,
                    getString(R.string.no_internet_favorites_only),
                    Toast.LENGTH_LONG).show();
        }

        PostersFragment postersFragment = (PostersFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_posters);
        if (null != postersFragment) {
            postersFragment.updateGrid(sortByOption);
        }

        //two pane mode - clean up detail screen if the poster grid is updated
        DetailsFragment detailsFragment = (DetailsFragment)
                getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
        if (null != detailsFragment) {
            detailsFragment.resetArguments();
            //fake re-create the details fragment (to force it to go back through its creation lifecycle)
            getSupportFragmentManager().beginTransaction()
                    .detach(detailsFragment)
                    .attach(detailsFragment)
                    .commit();
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

            DetailsFragment detailFragment = new DetailsFragment();
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
