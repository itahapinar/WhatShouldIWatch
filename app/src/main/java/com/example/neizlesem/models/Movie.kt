package com.example.neizlesem.models

data class MovieResponse(
    val results: List<Movie>,
    val total_results: Int,
    val page: Int
)

data class Movie(
    val id: Int,
    val title: String?,
    val overview: String?,
    val vote_average: Double?,
    val genre_ids: List<Int>?,
    val poster_path: String?
)
