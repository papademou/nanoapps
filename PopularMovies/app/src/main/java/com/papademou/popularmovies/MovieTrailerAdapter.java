package com.papademou.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.papademou.popularmovies.Constants.YOUTUBE_THUMBNAIL_URI;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder>{
    private List<MovieTrailer> mTrailers;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.trailer_thumb);
        }


    }

    public MovieTrailerAdapter(Context context, MovieTrailers trailers) {
        mTrailers = trailers.getmTrailers();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_detail_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MovieTrailer trailer = mTrailers.get(position);
        String thumbnailSrc = YOUTUBE_THUMBNAIL_URI.replace("{VIDEO_ID}",trailer.getmKey());
        Picasso.with(holder.mImageView.getContext()).load(thumbnailSrc).fit().centerCrop().into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* First, try opening explicitly from youtube app */
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getmKey()));
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
