package com.papademou.popularmovies;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TMDbService {
    @GET("/3/discover/movie")
    Call<TMDbMovieResult<Movie>> getMovies(@Query("api_key") String apiKey,
                                                  @Query("sort_by") String sortBy);

    @GET("3/movie/{id}/reviews")
    Call<TMDbMovieResult<MovieReview>> getReviews(@Path("id") String id,
                                                  @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<TMDbMovieResult<MovieTrailer>> getTrailers(@Path("id") String id,
                                                    @Query("api_key") String apiKey);
}
