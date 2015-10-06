package com.papademou.popularmovies;

/**
 * Created by p01ks on 10/4/2015.
 */
public class MovieReview extends MovieDetail {
    private String mContent;

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmContent() {
        return mContent;
    }

    public MovieReview() { this. setType(Type.REVIEW); }
}
