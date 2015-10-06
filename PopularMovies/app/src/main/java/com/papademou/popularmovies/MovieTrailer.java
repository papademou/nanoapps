package com.papademou.popularmovies;

public class MovieTrailer{
    private static final String YOUTUBE_BASE_URI = "http://www.youtube.com/watch?v=";
    private String mKey;
    private String mName;

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getmName() {
        return mName;
    }

    public String getmKey(){
        return mKey;
    }

    public String getUri() { return YOUTUBE_BASE_URI + mKey; }

}
