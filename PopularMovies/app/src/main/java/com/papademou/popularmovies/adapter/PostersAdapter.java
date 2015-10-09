package com.papademou.popularmovies.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.papademou.popularmovies.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostersAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> mMovies;

    public PostersAdapter(Context c, List<Movie> movies) {
        mContext = c;
        mMovies = movies;
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
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(mMovies.get(position).getFullImagePath()).into(imageView);
        return imageView;
    }

}

