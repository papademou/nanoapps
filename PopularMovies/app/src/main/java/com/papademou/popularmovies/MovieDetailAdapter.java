package com.papademou.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.papademou.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailAdapter extends BaseAdapter{

    private List<MovieDetail> mDetails;
    private Context mContext;
    private int mMovieId;
    private static final int VIEW_SUMMARY = 0;
    private static final int VIEW_REVIEW = 1;
    private static final int VIEW_TRAILER = 2;
    public MovieDetailAdapter(Context context, List<MovieDetail> movieDetails, int movie_id) {
        super();
        this.mDetails = movieDetails;
        this.mContext = context;
        this.mMovieId = movie_id;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDetails.get(position) instanceof MovieSummary) {
            return VIEW_SUMMARY;
        } else if (mDetails.get(position) instanceof MovieReview) {
            return VIEW_REVIEW;
        } else if (mDetails.get(position).getType().equals(MovieDetail.Type.TRAILERS)){ //MovieTrailer instance
            return VIEW_TRAILER;
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

        if (type == VIEW_SUMMARY) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.movie_detail_summary, parent, false);
                summaryViewHolder = new SummaryHolder();
                summaryViewHolder.mTitle = (TextView) convertView.findViewById(R.id.movie_title);
                summaryViewHolder.mPoster = (ImageView) convertView.findViewById(R.id.poster);
                summaryViewHolder.mReleaseDate = (TextView) convertView.findViewById(R.id.release_date);
                summaryViewHolder.mVoteAvg = (TextView) convertView.findViewById(R.id.vote_avg);
                summaryViewHolder.mOverview = (TextView) convertView.findViewById(R.id.plot_synopsis);
                summaryViewHolder.mFavorite = (Button) convertView.findViewById(R.id.btn_favorite);
                convertView.setTag(summaryViewHolder);
            } else {
                summaryViewHolder = (SummaryHolder) convertView.getTag();
            }

            final MovieSummary summaryDetails = (MovieSummary) mDetails.get(position);
            summaryViewHolder.mTitle.setText(summaryDetails.getmTitle());
            Picasso.with(mContext).load(summaryDetails.constructImagePath()).into(summaryViewHolder.mPoster);
            summaryViewHolder.mOverview.setText(summaryDetails.getmOverview());
            summaryViewHolder.mReleaseDate.setText(summaryDetails.getmReleaseDate());
            summaryViewHolder.mVoteAvg.setText(summaryDetails.getmVoteAvg()+"/10");
            //Favorite Button
            //TODO complete logic to remove from favorites and disable "mark as favorite" for movies already marked as favorites
            summaryViewHolder.mFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Insert into database
                    Uri mNewUri;
                    ContentValues mNewValues = new ContentValues();

                    try {

                        mNewValues.put(MovieContract.MovieEntry.COLUMN_ID, summaryDetails.getmMovieId());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, summaryDetails.getmOverview());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, summaryDetails.getmPosterPath());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, summaryDetails.getmReleaseDate());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_TITLE, summaryDetails.getmTitle());
                        mNewValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, summaryDetails.getmVoteAvg());

                        mNewUri = mContext.getContentResolver().insert(
                                MovieContract.MovieEntry.CONTENT_URI,
                                mNewValues
                        );
                        Toast.makeText(mContext, "Added to Favorites in database", Toast.LENGTH_SHORT).show();
                        summaryViewHolder.mFavorite.setText("FAVORITE");
                    } catch (Exception e) {
                        Log.e("MovieDetailFragment", "Exception ", e);
                    }
                }
            });
        } else if (type == VIEW_REVIEW) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.movie_detail_review, parent, false);
                reviewHolder = new ReviewHolder();
                reviewHolder.mContent = (TextView) convertView.findViewById(R.id.review_content);
                convertView.setTag(reviewHolder);
            } else {
                reviewHolder = (ReviewHolder) convertView.getTag();
            }
            MovieReview review = (MovieReview) mDetails.get(position);
            reviewHolder.mContent.setText(review.getmContent());
        } else if (type == VIEW_TRAILER) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.movie_detail_trailers, parent, false);
                trailersHolder = new TrailersHolder();
                trailersHolder.mTrailersRecyclerView = (android.support.v7.widget.RecyclerView) convertView.findViewById(R.id.trailers);
                convertView.setTag(trailersHolder);
            } else {
                trailersHolder = (TrailersHolder) convertView.getTag();
            }
            MovieTrailers trailers = (MovieTrailers) mDetails.get(position);
            MovieTrailerAdapter trailerAdapter = new MovieTrailerAdapter(mContext, trailers);
            android.support.v7.widget.RecyclerView trailersRecyclerView = trailersHolder.mTrailersRecyclerView;
            trailersRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            trailersRecyclerView.setAdapter(trailerAdapter);

        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addReviews(ArrayList<MovieReview> reviews) {
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

    private class SummaryHolder {
        private TextView mTitle;
        private ImageView mPoster;
        private TextView mReleaseDate;
        private TextView mVoteAvg;
        private TextView mOverview;
        private Button mFavorite;
    }
    private class ReviewHolder{
        private TextView mContent;
    }

    private class TrailersHolder {
        private android.support.v7.widget.RecyclerView mTrailersRecyclerView;
    }
}
