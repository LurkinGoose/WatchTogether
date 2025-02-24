package com.example.watch_together.viewModels

import com.example.watch_together.models.Movie

data class FavoritesUiState(
    val favoriteMovies: List<Movie> = emptyList(),
    val hasViewedFavorites: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null
)