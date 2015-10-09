package com.papademou.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Date;

import static com.papademou.popularmovies.util.Constants.YOUTUBE_THUMBNAIL_URI;

public class Utility {
    /**
     * creates a Date object from a unix timestamp
     * @param timestamp
     * @return
     */
    public static Date convertUnixTime(long timestamp ) {
        return new Date((long) timestamp*1000);
    }

    public static String getYoutubeVideoThumnailUrl(String videoKey) {
        return YOUTUBE_THUMBNAIL_URI.replace("{VIDEO_ID}",videoKey);
    }

    /** Determines if app has permission to access internet & is connected to internet/ */
    public static boolean hasInternetConnection(Context context) {
        boolean isConnected;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch(SecurityException e) {
            isConnected = false;
        }

        return isConnected;
    }
}
