package com.papademou.popularmovies.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.papademou.popularmovies.FetchReviewsTask;
import com.papademou.popularmovies.FetchTrailersTask;
import com.papademou.popularmovies.Movie;
import com.papademou.popularmovies.MovieDetail;
import com.papademou.popularmovies.MovieTrailer;
import com.papademou.popularmovies.R;
import com.papademou.popularmovies.adapter.DetailsAdapter;
import com.papademou.popularmovies.util.Utility;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment {
    @Bind(R.id.listview_movie_detail) ListView detailsListView;

    private ArrayList<MovieDetail> mDetails = new ArrayList<MovieDetail>();
    private DetailsAdapter mDetailAdapter;
    private ArrayList<MovieTrailer> mTrailers = new ArrayList<MovieTrailer>();
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        final Bundle bundle = getArguments();
        if (bundle == null || bundle.isEmpty()) {
            mDetails.clear();
            mDetailAdapter = new DetailsAdapter(mContext, mDetails);
            detailsListView.setAdapter(mDetailAdapter);
        } else {
            Movie movie = bundle.getParcelable(Movie.KEY_MOVIE);
            mDetails.add(movie); //add the summary details
            int movieId = movie.getMTMDbMovieId();

            mDetailAdapter = new DetailsAdapter(mContext, mDetails);
            detailsListView.setAdapter(mDetailAdapter);

            if (Utility.hasInternetConnection(mContext)) {
                //Fetch Trailers
                FetchTrailersTask trailersTask = new FetchTrailersTask(getActivity(), mDetailAdapter);
                trailersTask.execute(Integer.toString(movieId));
                //Fetch reviews
                FetchReviewsTask reviewsTask = new FetchReviewsTask(mDetailAdapter);
                reviewsTask.execute(Integer.toString(movieId));
            } else {
                Toast.makeText(mContext,
                        getString(R.string.no_internet_movie_details),
                        Toast.LENGTH_LONG)
                        .show();
            }
        }

        return rootView;
    }

    /* Clears all fragment arguments */
    public void resetArguments() {
        Bundle args = getArguments();
        if (null != args) {
            args.clear();
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}



