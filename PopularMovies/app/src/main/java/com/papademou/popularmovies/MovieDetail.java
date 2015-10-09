package com.papademou.popularmovies;

import lombok.Getter;
import lombok.Setter;

public abstract class MovieDetail {
    public enum Type {
        SUMMARY,
        REVIEW,
        TRAILERS, //to identify collection of all trailers associated with a movie
                  //Specifically used to identify a list of movies passed at once to the movie detail adapter
                  //as opposed to processing them one at a time. A bit hacky, but this is so we can link
                  //a collection of movie trailers to a single common item type (horizontal recyclerview)
        TRAILER //to identify a single trailer associated with a movie
    }

    @Getter @Setter private Type type;
}
