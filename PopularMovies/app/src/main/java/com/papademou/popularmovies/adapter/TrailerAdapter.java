package com.papademou.popularmovies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.papademou.popularmovies.MovieTrailer;
import com.papademou.popularmovies.MovieTrailers;
import com.papademou.popularmovies.R;
import com.papademou.popularmovies.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * RecyclerView Adapter to display movie trailers horizontally
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{
    private List<MovieTrailer> mTrailers;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.trailer_thumb) ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TrailerAdapter(Context context, MovieTrailers trailers) {
        mTrailers = trailers.getMTrailers();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieTrailer trailer = mTrailers.get(position);
        String thumbnailUrl = Utility.getYoutubeVideoThumnailUrl(trailer.getMKey());
        Picasso.with(holder.mImageView.getContext()).load(thumbnailUrl).fit().centerCrop().into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* First, try opening explicitly from Youtube app. If fails, send implicit intent */
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getMKey()));
                    v.getContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(trailer.getUri()));
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers == null ? 0 : mTrailers.size();
    }

}
