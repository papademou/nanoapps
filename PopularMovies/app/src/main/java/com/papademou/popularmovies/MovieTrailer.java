package com.papademou.popularmovies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.papademou.popularmovies.util.Constants;

import lombok.Getter;
import lombok.Setter;

public class MovieTrailer extends MovieDetail {

    @Getter @Setter @Expose @SerializedName("key")
    private String mKey;
    @Getter @Setter @Expose @SerializedName("name")
    private String mName;

    public String getUri() { return Constants.YOUTUBE_VIDEO_BASE_URI + mKey; }

    public MovieTrailer() { this.setType(Type.TRAILER); }
}
