package com.papademou.popularmovies;

import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class TMDbMovieResult<ResultType extends MovieDetail> {
    private Integer page;
    @Getter @Setter @Expose
    private List<ResultType> results;
    private Integer totalPages;
    private Integer totalResults;
}
