package com.papademou.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.papademou.popularmovies.fragment.DetailsFragment;
import com.papademou.popularmovies.R;

/**
 * Only used on handheld devices (separate activities/single pane)
 */
public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            //load a DetailFragment instance in the detail screen, populated with movie info
            //passed as intent from MasterActivity
            Bundle arguments = new Bundle();
            arguments.putAll(getIntent().getExtras());
            DetailsFragment detailFragment = new DetailsFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, detailFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        //keep track of the activity menu in order to be able to change menu options later on
        // (e.g: display/hide "Share" menu item depending on whether movie trailers are found or not)
        mMenu = menu;
        return true;
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
     * Can be used to retrieve the detail activity menu (in AsyncTask for instance),
     * which will help in hiding/displaying share menu item if a movie has trailers
     * Being used right now in FetchTrailersTask.onPostExecute
     * @return the activity menu
     */
    public Menu getMenu() {
        return mMenu;
    }
}
