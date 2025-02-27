package com.example.watch_together.viewModels

import com.example.watch_together.models.Movie

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val movieDetails: Movie? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null
)
