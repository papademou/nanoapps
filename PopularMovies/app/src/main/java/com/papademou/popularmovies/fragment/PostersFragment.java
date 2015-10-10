package com.papademou.popularmovies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.papademou.popularmovies.FetchMoviesTask;
import com.papademou.popularmovies.Movie;
import com.papademou.popularmovies.R;
import com.papademou.popularmovies.adapter.PostersAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

//TODO Use Sync adapters
public class PostersFragment extends Fragment {
    @Bind(R.id.gridview_movies) GridView mPostersGridView;
    private static final String KEY_MOVIES_PARCEL = "movies";
    private static final String SELECTED_KEY = "selected_position";
    private PostersAdapter mAdapter;
    private ArrayList<Movie> mMovies = new ArrayList<Movie>();
    private Context mContext;
    private int mPosition = GridView.INVALID_POSITION;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mMovies = savedInstanceState.getParcelableArrayList(KEY_MOVIES_PARCEL);
        }

        mAdapter = new PostersAdapter(getActivity(), mMovies);

        mPostersGridView.setAdapter(mAdapter);
        mPostersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIES_PARCEL, mMovies);
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPosition != GridView.INVALID_POSITION) {
            mPostersGridView.smoothScrollToPosition(mPosition);
        }
    }

    public void updateGrid(String sortByOption) {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), mAdapter);
        moviesTask.execute(sortByOption);
    }

    public interface OnItemSelectedListener {
        public void onItemSelected(Movie movieDetailsSummary);
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
