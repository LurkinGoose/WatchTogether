package com.example.watch_together.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        _uiState.value = _uiState.value.copy(loading = true)

        viewModelScope.launch {
            repository.getAllFavorites().collectLatest { favorites ->
                _uiState.value = _uiState.value.copy(favoriteMovies = favorites, loading = false)
            }
        }
    }

    fun addToFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.addToFavorites(movieId)
            _uiState.value = _uiState.value.copy(
                favoriteMovies = _uiState.value.favoriteMovies + repository.getMovieById(movieId)
            )
        }
    }

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(movieId)
            _uiState.value = _uiState.value.copy(
                favoriteMovies = _uiState.value.favoriteMovies.filterNot { it.id == movieId }
            )
        }
    }
}
