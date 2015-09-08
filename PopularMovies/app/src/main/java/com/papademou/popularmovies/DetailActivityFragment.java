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

import static com.papademou.popularmovies.Constants.KEY_TITLE;
import static com.papademou.popularmovies.Constants.KEY_POSTER_PATH;
import static com.papademou.popularmovies.Constants.KEY_RELEASE_DATE;
import static com.papademou.popularmovies.Constants.KEY_VOTE_AVG;
import static com.papademou.popularmovies.Constants.KEY_OVERVIEW;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (intent.hasExtra(KEY_TITLE)){
                ((TextView) rootView.findViewById(R.id.movie_title))
                        .setText(bundle.getString(KEY_TITLE));
            }
            if (intent.hasExtra(KEY_POSTER_PATH)){
                ImageView posterImageView = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load(bundle.getString(KEY_POSTER_PATH)).into(posterImageView);
            }
            if (intent.hasExtra(KEY_RELEASE_DATE)){
                ((TextView) rootView.findViewById(R.id.release_date))
                        .setText(bundle.getString(KEY_RELEASE_DATE));
            }
            if (intent.hasExtra(KEY_VOTE_AVG)){
                ((TextView) rootView.findViewById(R.id.vote_avg))
                        .setText(Double.toString(bundle.getDouble(KEY_VOTE_AVG)) + "/10");
            }
            if (intent.hasExtra(KEY_OVERVIEW)){
                ((TextView) rootView.findViewById(R.id.plot_synopsis))
                        .setText(bundle.getString(KEY_OVERVIEW));
            }

        }

        return rootView;
    }
}
