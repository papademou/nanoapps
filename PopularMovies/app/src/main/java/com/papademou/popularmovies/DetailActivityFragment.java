package com.papademou.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String TITLE_EXTRA = "TITLE";
        final String RELEASE_DATE_EXTRA = "RELEASE_DATE";
        final String POSTER_PATH_EXTRA = "POSTER_PATH";
        final String OVERVIEW_EXTRA = "OVERVIEW";
        final String VOTE_AVG_EXTRA = "VOTE_AVG";

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (intent.hasExtra(TITLE_EXTRA)){
                ((TextView) rootView.findViewById(R.id.movie_title))
                        .setText(bundle.getString(TITLE_EXTRA));
            }
            if (intent.hasExtra(POSTER_PATH_EXTRA)){
                ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load(bundle.getString(POSTER_PATH_EXTRA)).into(posterImageView);
            }
            if (intent.hasExtra(RELEASE_DATE_EXTRA)){
                ((TextView) rootView.findViewById(R.id.release_date))
                        .setText(bundle.getString(RELEASE_DATE_EXTRA));
            }
            if (intent.hasExtra(VOTE_AVG_EXTRA)){
                ((TextView) rootView.findViewById(R.id.vote_avg))
                        .setText(Double.toString(bundle.getDouble(VOTE_AVG_EXTRA)) + "/10");
            }
            if (intent.hasExtra(OVERVIEW_EXTRA)){
                ((TextView) rootView.findViewById(R.id.plot_synopsis))
                        .setText(bundle.getString(OVERVIEW_EXTRA));
            }

        }

        return rootView;
    }
}
