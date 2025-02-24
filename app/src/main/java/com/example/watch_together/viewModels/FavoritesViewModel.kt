package com.example.watch_together.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        checkHasViewedFavorites()
    }

    fun checkHasViewedFavorites() {
        val hasViewed = repository.getHasViewedFavorites()
        println("checkHasViewedFavorites: $hasViewed")
        _uiState.value = _uiState.value.copy(hasViewedFavorites = hasViewed)
    }

    fun markFavoritesAsViewed() {
        repository.setHasViewedFavorites(true)
        _uiState.value = _uiState.value.copy(hasViewedFavorites = true)
        println("markFavoritesAsViewed: true")
    }

    fun loadFavorites() {
        _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val favorites = repository.getFavoriteMovies()
                _uiState.value = _uiState.value.copy(favoriteMovies = favorites, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Ошибка загрузки избранного: ${e.localizedMessage}", loading = false)
            }
        }
    }

    fun addToFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.addFavorite(movieId)
            loadFavorites()
        }
    }

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.removeFavorite(movieId)
            loadFavorites()
        }
    }
}