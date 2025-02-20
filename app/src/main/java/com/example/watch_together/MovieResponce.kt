package com.example.watch_together

data class MovieResponse(val results: List<Movie>)

data class Movie(
    val title: String,
    val poster_path: String,
    val id: Int,
    val overview: String
) {
    val fullPosterPath: String
        get() = poster_path.let { "https://image.tmdb.org/t/p/w500$it" }

}

