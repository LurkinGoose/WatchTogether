package com.example.watch_together.models

data class MovieList(val results: List<Movie>)

data class Movie(
    val title: String,
    val poster_path: String?,
    val id: Int,
    val overview: String,
    val release_date: String?,
    val vote_average: Double?,
    val runtime: Int?,
    val genres: List<Genre>?
) {
    val fullPosterPath: String
        get() = poster_path?.let { "https://image.tmdb.org/t/p/w185$it" } ?: ""
}

data class Genre(
    val id: Int,
    val name: String
)
