package com.example.watch_together.viewModels

import com.example.watch_together.models.Movie

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val movieDetails: Movie? = null, // ✅ Теперь в uiState есть `movieDetails`
    val loading: Boolean = false,
    val errorMessage: String? = null
)
