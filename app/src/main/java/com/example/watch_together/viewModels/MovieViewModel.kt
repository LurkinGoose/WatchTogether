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
        if (_uiState.value.loading) return // ✅ Пропускаем, если уже идёт загрузка

        viewModelScope.launch {
            _uiState.updateIfChanged { it.copy(loading = true) } // ✅ Обновляем, только если изменилось

            try {
                val movies = repository.searchMovies(query)
                _uiState.updateIfChanged {
                    if (it.movies != movies) it.copy(movies = movies, loading = false, errorMessage = null)
                    else it.copy(loading = false) // ✅ Не обновляем, если фильмы такие же
                }
            } catch (e: Exception) {
                _uiState.updateIfChanged { it.copy(errorMessage = e.localizedMessage, loading = false) }
            }
        }
    }

    fun getMovieDetails(movieId: Int) {
        if (_uiState.value.loading) return // ✅ Избегаем повторного запроса

        viewModelScope.launch {
            _uiState.updateIfChanged { it.copy(loading = true, errorMessage = null) }

            try {
                val movie = repository.getMovieById(movieId)
                _uiState.updateIfChanged { it.copy(movieDetails = movie, loading = false) }
            } catch (e: Exception) {
                _uiState.updateIfChanged { it.copy(errorMessage = "Ошибка загрузки фильма: ${e.localizedMessage}", loading = false) }
            }
        }
    }

    fun clearMovies() {
        _uiState.updateIfChanged { it.copy(movies = emptyList()) }
    }

    fun clearMovieDetails() {
        _uiState.updateIfChanged { it.copy(movieDetails = null) }
    }

    private inline fun MutableStateFlow<MovieUiState>.updateIfChanged(update: (MovieUiState) -> MovieUiState) {
        val newState = update(value)
        if (newState != value) value = newState // ✅ Меняем только если есть изменения
    }
}
