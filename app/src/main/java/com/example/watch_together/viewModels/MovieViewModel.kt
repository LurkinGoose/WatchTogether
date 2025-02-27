package com.example.watch_together.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

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

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
            try {
                val movie = repository.getMovieDetails(movieId)
                _uiState.value = _uiState.value.copy(movieDetails = movie, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Ошибка загрузки фильма: ${e.localizedMessage}", loading = false)
            }
        }
    }

    fun clearMovies() {
        _uiState.value = _uiState.value.copy(movies = emptyList())
    }

    fun clearMovieDetails() {
        _uiState.value = _uiState.value.copy(movieDetails = null)
    }

}
