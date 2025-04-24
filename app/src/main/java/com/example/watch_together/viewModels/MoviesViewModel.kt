package com.example.watch_together.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MoviesState(
    val movies: List<Movie> = emptyList(),
    val movieDetails: Movie? = null,
    val favorites: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchListState: LazyListState = LazyListState(),
    val favoritesListState: LazyListState = LazyListState()
)

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesState())
    val state: StateFlow<MoviesState> = _state

    fun resetSearchScrollPosition() {
        viewModelScope.launch {
            _state.value.searchListState.scrollToItem(0)
        }
    }

    fun resetFavoritesScrollPosition() {
        viewModelScope.launch {
            _state.value.favoritesListState.scrollToItem(0)
        }
    }

    fun searchMovies(query: String) {
        resetSearchScrollPosition()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.searchMovies(query)
                _state.update { it.copy(movies = result) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadTopRatedMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.getTopRatedMovies()
                _state.update { it.copy(movies = result) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val details = repository.getMovieById(id)
                _state.update { it.copy(movieDetails = details) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.getAllFavorites().collect { result ->
                    _state.update { it.copy(favorites = result) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.localizedMessage) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addToFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.addToFavorites(movieId)
            val movie = repository.getMovieById(movieId)
            _state.update { it.copy(favorites = it.favorites + movie) }
        }
    }

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(movieId)
            _state.update { it.copy(favorites = it.favorites.filterNot { movie -> movie.id == movieId }) }
        }
    }

    fun clearMovies() {
        _state.update { it.copy(movies = emptyList()) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
