package com.example.neizlesem.network

import com.example.neizlesem.models.MovieResponse
import com.example.neizlesem.models.GenreResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApi {
    @GET("movie/popular")
    fun getPopular(@Query("api_key") apiKey: String, @Query("language") lang: String = "tr-TR", @Query("page") page: Int = 1): Call<MovieResponse>

    @GET("discover/movie")
    fun discoverByGenre(@Query("api_key") apiKey: String, @Query("with_genres") genreId: Int, @Query("language") lang: String = "tr-TR", @Query("page") page: Int = 1): Call<MovieResponse>

    @GET("genre/movie/list")
    fun getGenres(@Query("api_key") apiKey: String, @Query("language") lang: String = "tr-TR"): Call<GenreResponse>
}
