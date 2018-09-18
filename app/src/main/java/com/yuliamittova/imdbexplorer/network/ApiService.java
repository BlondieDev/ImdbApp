package com.yuliamittova.imdbexplorer.network;

import android.support.annotation.NonNull;

import com.yuliamittova.imdbexplorer.dataobject.MovieDetails;
import com.yuliamittova.imdbexplorer.dataobject.SearchResultList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("trending/movie/week")
    Observable<SearchResultList> getMovieList(final @Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Observable<MovieDetails> getMovieDetails(final @NonNull @Path("movie_id") Integer movieId, final @Query("api_key") String apiKey);
}
