package com.example.watch_together.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.FavoriteMovieRepository
import com.example.watch_together.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FavoritesViewModel(private val repository: FavoriteMovieRepository) : ViewModel() {

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies.asStateFlow()

    private val _hasViewedFavorites = MutableStateFlow(repository.getHasViewedFavorites())
    val hasViewedFavorites: StateFlow<Boolean> = _hasViewedFavorites

    init {
        loadFavoriteMovies()
    }
    fun loadFavoriteMovies() {
        viewModelScope.launch {
            val movies = repository.getFavoriteMovies()
            Log.d("FavoritesViewModel", "Loaded favorite movies: ${movies.size}")
            _favoriteMovies.value = movies
        }
    }
    fun addToFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.addFavorite(movieId)
            _hasViewedFavorites.value = false // Сбрасываем состояние
            repository.setHasViewedFavorites(false) // Сохраняем в SharedPreferences
            loadFavoriteMovies()
        }
    }
    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.removeFavorite(movieId)
            loadFavoriteMovies()
        }
    }
    fun markFavoritesAsViewed() {
        Log.d("FavoritesViewModel", "Marking favorites as viewed")
        _hasViewedFavorites.value = true
        repository.setHasViewedFavorites(true)
        Log.d("FavoritesViewModel", "'hasViewedFavorites' marked as true")
    }
}

