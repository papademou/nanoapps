package com.papademou.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.papademou.popularmovies.Movie;
import com.papademou.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostersAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> mMovies;

    public PostersAdapter(Context c, List<Movie> movies) {
        mContext = c;
        mMovies = movies;
    }

    static class PosterViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_poster) ImageView mImageView;

        public PosterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public int getCount() {
        return mMovies.size();
    }

    public Object getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public List<Movie> getItems() {
        return mMovies;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final PosterViewHolder posterViewHolder;

        if (convertView == null) {
            convertView = ((Activity)mContext).getLayoutInflater()
                    .inflate(R.layout.poster, parent, false);
            posterViewHolder = new PosterViewHolder(convertView);
            convertView.setTag(posterViewHolder);
        } else {
            posterViewHolder = (PosterViewHolder) convertView.getTag();
        }

        Picasso.with(mContext).load(mMovies.get(position).getFullImagePath()).into(posterViewHolder.mImageView);
        return convertView;
    }

}

