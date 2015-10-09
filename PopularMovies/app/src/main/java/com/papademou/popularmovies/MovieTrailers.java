package com.papademou.popularmovies;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MovieTrailers extends MovieDetail{

    @Getter @Setter
    private List<MovieTrailer> mTrailers;

    public MovieTrailers() {
        this.setType(Type.TRAILERS);
    }

    public MovieTrailers(List<MovieTrailer> trailers) {
        this();
        mTrailers = trailers;
    }

}
