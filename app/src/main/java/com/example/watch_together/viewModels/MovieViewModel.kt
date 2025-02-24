package com.example.watch_together.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState

    val saveListState = LazyListState()

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val movies = repository.searchMovies(query)
                _uiState.value = _uiState.value.copy(movies = movies, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage, loading = false)
            }
        }
    }

    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> = _movieDetails

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
            try {
                val movie = repository.getMovieDetails(movieId)
                _movieDetails.value = movie
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Ошибка загрузки фильма: ${e.localizedMessage}")
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    // Очистка списка фильмов
    fun clearMovies() {
        _uiState.value = _uiState.value.copy(movies = emptyList())
    }

    // Очистка деталей фильма
    fun clearMovieDetails() {
        _movieDetails.value = null
    }
}
