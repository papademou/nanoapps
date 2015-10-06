package com.papademou.popularmovies;

import java.util.List;

/**
 * Created by p01ks on 10/4/2015.
 */
public class MovieTrailers extends MovieDetail{
    public List<MovieTrailer> getmTrailers() {
        return mTrailers;
    }

    public void setmTrailers(List<MovieTrailer> mTrailers) {
        this.mTrailers = mTrailers;
    }

    private List<MovieTrailer> mTrailers;
    public MovieTrailers() {
        this.setType(Type.TRAILERS);
    }

}
