package com.example.watch_together.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    var movies by mutableStateOf<List<Movie>>(emptyList())
        private set

    var movieDetails by mutableStateOf<Movie?>(null)
        private set

    var favorites by mutableStateOf<List<Movie>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // состояние скролла поиска
    var searchListState: LazyListState = LazyListState()
        private set

    // состояние скролла избранного
    var favoritesListState: LazyListState = LazyListState()
        private set

    fun resetSearchScrollPosition() {
        viewModelScope.launch {
            searchListState.scrollToItem(0)
        }
    }

    fun resetFavoritesScrollPosition() {
        viewModelScope.launch {
            favoritesListState.scrollToItem(0)
        }
    }

    fun searchMovies(query: String) {
        resetSearchScrollPosition()
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                movies = repository.searchMovies(query)
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun loadTopRatedMovies() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                movies = repository.getTopRatedMovies()
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }


    fun getMovieDetails(id: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                movieDetails = repository.getMovieById(id)
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            isLoading = true
            try {
                repository.getAllFavorites().collectLatest {
                    favorites = it
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun addToFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.addToFavorites(movieId)
            val movie = repository.getMovieById(movieId)
            favorites = favorites + movie
        }
    }

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(movieId)
            favorites = favorites.filterNot { it.id == movieId }
        }
    }

    fun clearMovies() {
        movies = emptyList()
    }
}
