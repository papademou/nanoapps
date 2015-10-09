package com.papademou.popularmovies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class MovieReview extends MovieDetail {

    @Getter @Setter @Expose @SerializedName("author")
    private String mAuthor;
    @Getter @Setter @Expose @SerializedName("content")
    private String mContent;
    @Getter @Setter @Expose @SerializedName("url")
    private String mUrl;

    public MovieReview() { this. setType(Type.REVIEW); }
}
