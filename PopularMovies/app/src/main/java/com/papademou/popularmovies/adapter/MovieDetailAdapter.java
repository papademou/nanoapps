package com.papademou.popularmovies.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.papademou.popularmovies.Movie;
import com.papademou.popularmovies.MovieDetail;
import com.papademou.popularmovies.MovieReview;
import com.papademou.popularmovies.MovieTrailers;
import com.papademou.popularmovies.R;
import com.papademou.popularmovies.data.MovieContract;
import com.papademou.popularmovies.data.MovieProvider;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Main Adapter associated with the detail screen layout where there is a listview to be populated with
 * 3 different view types. The adapter is linked to a collection of MovieDetail objects.
 * The view type is determined based on the class instance of the MovieDetail object. Each MovieDetail
 * object also has a type property(an Enum value) to identify them.
 * The table below summarizes the view mapping for this adapter.
 * Note: Only one object per types VIEW_SUMMARY and VIEW_TRAILERS is expected
 *
 *   _____________________________________________________________________________________________
 *  | VIEW TYPE   |  MOVIEDETAIL TYPE     | VIEW LAYOUT              |   LOOK AND FEEL            |
 *  |---------------------------------------------------------------------------------------------|
 *  |VIEW_SUMMARY | Class Instance: Movie | movie_detail_summary.xml |        Title               |
 *  |             | Enum Type: SUMMARY    |                          | I   Release Date           |
 *  |             |                       |                          | M  Vote Avg                |
 *  |             |                       |                          | A   Favorite Button        |
 *  |             |                       |                          | G                          |
 *  |             |                       |                          | E                          |
 *  |             |                       |                          | .....Plot Synopsis.......  |
 *  |---------------------------------------------------------------------------------------------|
 *  |VIEW_TRAILERS| Class Instance:       | movie_detail_trailers.xml| (Horizontal RecyclerView)  |
 *  |             |       MovieTrailers   |                          |(   Single Video Layout:  ) |
 *  |             | Enum Type: TRAILERS   |                          |( movie_detail_trailer.xml )|
 *  |             |                       |                          |                            |
 *  |             |                       |                          |ideo1 Video2 Video3 Video4 V|
 *  |             |                       |                          |                            |
 *  |---------------------------------------------------------------------------------------------|
 *  |VIEW_REVIEW  | Class Instance:       | movie_detail_review.xml  | This is a review about the |
 *  |             |       MovieReview     |                          | review in the review ....  |
 *  |             | Enum Type: REVIEW     |                          |                            |
 *  |_____________|_______________________|__________________________|____________________________|

 *
 */
public class MovieDetailAdapter extends BaseAdapter{
    private final String LOG_TAG = MovieDetailAdapter.class.getSimpleName();
    private List<MovieDetail> mDetails;
    private Context mContext;
    private int mMovieId;
    private static final int VIEW_SUMMARY = 0;
    private static final int VIEW_REVIEW = 1;
    private static final int VIEW_TRAILERS = 2;

    public MovieDetailAdapter(Context context, List<MovieDetail> movieDetails, int movieId) {
        super();
        mDetails = movieDetails;
        mContext = context;
        mMovieId = movieId;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDetails.get(position).getType().equals(MovieDetail.Type.SUMMARY)) {
            return VIEW_SUMMARY;
        } else if (mDetails.get(position).getType().equals(MovieDetail.Type.REVIEW)) {
            return VIEW_REVIEW;
        } else if (mDetails.get(position).getType().equals(MovieDetail.Type.TRAILERS)){ //MovieTrailer instance
            return VIEW_TRAILERS;
        }
        return -1;
    }

    @Override
    public int getCount() {
        return mDetails.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        final SummaryHolder summaryViewHolder;
        ReviewHolder reviewHolder;
        TrailersHolder trailersHolder;
        LayoutInflater inflater = null;

        if (convertView == null) {
            inflater = ((Activity)mContext).getLayoutInflater();
        }

        switch(type) {
            case VIEW_SUMMARY: {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.movie_detail_summary, parent, false);
                    summaryViewHolder = new SummaryHolder(convertView); //Thanks ButterKnife
                    convertView.setTag(summaryViewHolder);
                } else {
                    summaryViewHolder = (SummaryHolder) convertView.getTag();
                }

                final Movie movie = (Movie) mDetails.get(position);
                summaryViewHolder.mTitle.setText(movie.getMTitle());
                Picasso.with(mContext).load(movie.getFullImagePath()).into(summaryViewHolder.mPoster);
                summaryViewHolder.mPlotSynopsis.setText(movie.getMPlotSynopsis());
                summaryViewHolder.mReleaseDate.setText(movie.getFormattedReleaseDate());
                summaryViewHolder.mVoteAvg.setText(movie.getVoteAverageDisplay());
                initializeAndBindFavoriteButton(summaryViewHolder.mFavorite, movie);

                break;
            }
            case VIEW_TRAILERS: {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.movie_detail_trailers, parent, false);
                    trailersHolder = new TrailersHolder(convertView);
                    convertView.setTag(trailersHolder);
                } else {
                    trailersHolder = (TrailersHolder) convertView.getTag();
                }
                MovieTrailers trailers = (MovieTrailers) mDetails.get(position);
                //setup trailers RecyclerView to display the trailer videos horizontally, adapter and all...
                MovieTrailerAdapter trailerAdapter = new MovieTrailerAdapter(mContext, trailers);
                RecyclerView trailersRecyclerView = trailersHolder.mTrailersRecyclerView;
                trailersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                trailersRecyclerView.setAdapter(trailerAdapter);

                break;
            }
            case VIEW_REVIEW: {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.movie_detail_review, parent, false);
                    reviewHolder = new ReviewHolder(convertView);
                    convertView.setTag(reviewHolder);
                } else {
                    reviewHolder = (ReviewHolder) convertView.getTag();
                }
                MovieReview review = (MovieReview) mDetails.get(position);
                reviewHolder.mContent.setText(review.getMContent());

                break;
            }
        }

        return convertView;
    }

    private void initializeAndBindFavoriteButton(final Button btnFavorite, final Movie movie) {
        final String TAG_FAVE = "IS_FAVE";
        final String TAG_NO_FAVE = "NO_FAVE";
        final String TEXT_REMOVE_FAVORITE = mContext.getString(R.string.text_favorite_remove);
        final String TEXT_ADD_FAVORITE = mContext.getString(R.string.text_favorite_add);

        //Search if this movie is already in db (which means it's been marked as favorite) since
        //those are the only movies being saved in the db in this version of the application
        String[] projection = { MovieContract.MovieEntry.COLUMN_NAME_TMDB_MOVIE_ID };

        Cursor cursor = mContext.getContentResolver().query(
                MovieProvider.buildMovieUri(movie.getMTMDbMovieId()), projection,
                null, null, null);
        if (null != cursor && cursor.getCount() > 0) { //already saved in db
            btnFavorite.setTag(TAG_FAVE);
            btnFavorite.setText(TEXT_REMOVE_FAVORITE);
        } else {
            btnFavorite.setTag(TAG_NO_FAVE);
            btnFavorite.setText(TEXT_ADD_FAVORITE);
        }

        //bind click listener to button
        //If movie is already a favorite, delete it from database and change button text
        //Else add it to database and change button text
        //TODO change style of buttons with respect to the button status (fave or not)
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFavorite.getTag().equals(TAG_FAVE)) { //already a fave (meaning the button currently reads "Remove"
                    try {
                        //remove from db
                        String selection = MovieContract.MovieEntry.COLUMN_NAME_TMDB_MOVIE_ID + " = ?";
                        String[] selectionArgs = {String.valueOf(movie.getMTMDbMovieId())};
                        mContext.getContentResolver().delete(MovieProvider.CONTENT_URI, selection, selectionArgs);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Failure while deleting this movie from db", e);
                        return;
                    }
                    //update button
                    btnFavorite.setTag(TAG_NO_FAVE);
                    btnFavorite.setText(TEXT_ADD_FAVORITE);
                } else { // not yet a favorite, button action is to add as fave
                    //Insert into database
                    Uri newUri;
                    ContentValues mNewValues = new ContentValues();
                    try {
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_NAME_TMDB_MOVIE_ID, movie.getMTMDbMovieId());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS, movie.getMPlotSynopsis());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH, movie.getMPosterPath());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDateUnixTimestamp());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, movie.getMTitle());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getMVoteAverage());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);

                        newUri = mContext.getContentResolver().insert(
                                MovieProvider.CONTENT_URI,
                                mNewValues
                        );

                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Failure while inserting this movie in db as favorite ", e);
                        return;
                    }
                    //update button
                    btnFavorite.setTag(TAG_FAVE);
                    btnFavorite.setText(TEXT_REMOVE_FAVORITE);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addReviews(List<MovieReview> reviews) {
        for (MovieReview review : reviews) {
            mDetails.add(review);
        }
        notifyDataSetChanged();
    }

    public void addTrailers(MovieTrailers trailers) {
        mDetails.add(trailers);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mDetails.get(position);
    }

    static class SummaryHolder {
        @Bind(R.id.movie_title) TextView mTitle;
        @Bind(R.id.poster)  ImageView mPoster;
        @Bind(R.id.release_date) TextView mReleaseDate;
        @Bind(R.id.vote_avg) TextView mVoteAvg;
        @Bind(R.id.plot_synopsis) TextView mPlotSynopsis;
        @Bind(R.id.btn_favorite) Button mFavorite;

        public SummaryHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class TrailersHolder {
        @Bind(R.id.trailers) RecyclerView mTrailersRecyclerView;

        public TrailersHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ReviewHolder{
        @Bind(R.id.review_content) TextView mContent;

        public ReviewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
