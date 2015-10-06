package com.papademou.popularmovies;

public class MovieDetail {
    public enum Type {
        SUMMARY,
        REVIEW,
        TRAILERS
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private Type type;
}
