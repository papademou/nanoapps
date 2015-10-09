package com.papademou.popularmovies.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.papademou.popularmovies.FetchMoviesTask;
import com.papademou.popularmovies.Movie;
import com.papademou.popularmovies.R;
import com.papademou.popularmovies.adapter.PostersAdapter;
import com.papademou.popularmovies.util.Utility;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

//TODO Use Sync adapters
public class MoviePostersFragment extends Fragment {
    @Bind(R.id.gridview_movies) GridView postersGridView;
    private static final String KEY_MOVIES_PARCEL = "movies";
    private PostersAdapter mAdapter;
    private ArrayList<Movie> mMovies = new ArrayList<Movie>();
    private String mSortByOption; //to store the sort by setting and track related changes
    private Context mContext;

    public interface OnItemSelectedListener {
        public void onItemSelected(Movie movieDetailsSummary);
    }

    private void updateMovieGrid() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //if there is no connection, default to favorites (from database instead of internet)
        String sortByOption = Utility.hasInternetConnection(mContext)
                ? prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value))
                : getString(R.string.pref_sort_by_favorites_value);

        if (null != mSortByOption
                && !mSortByOption.equals(getString(R.string.pref_sort_by_favorites_value))
                && sortByOption.equals(mSortByOption)) {
            return; //we only want to fetch a new movie list if the sort preference has changed, unless we are displaying favorites (fetched from db)
                    //because the favorite status can change anytime
        }

        if(!Utility.hasInternetConnection(mContext)) {
            Toast.makeText(mContext,
                    getString(R.string.no_internet_favorites_only),
                    Toast.LENGTH_LONG).show();
        }

        mSortByOption = sortByOption;
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), mAdapter);
        moviesTask.execute(sortByOption);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieGrid(); //attempt to refresh the movie grid each time the activity starts
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIES_PARCEL, mMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mMovies = savedInstanceState.getParcelableArrayList(KEY_MOVIES_PARCEL);
        }

        mAdapter = new PostersAdapter(getActivity(), mMovies);

        postersGridView.setAdapter(mAdapter);
        postersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnItemSelectedListener listener;
                Movie movie = (Movie) mAdapter.getItem(position);
                if (movie != null) {
                    FragmentActivity activity = getActivity();
                    try {
                        listener = (OnItemSelectedListener) activity;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(activity.toString()
                                + " needs to implement OnItemSelectedListener");
                    }

                    listener.onItemSelected(movie);
                }
            }
        });

        return rootView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
