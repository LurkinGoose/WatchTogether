package com.example.watch_together.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : BaseViewModel() {

    private val _movieList = MutableStateFlow<List<Movie>>(emptyList())
    val movieList: StateFlow<List<Movie>> = _movieList

    val saveListState = LazyListState()

    fun searchMovies(query: String) {
        setLoading(true)
        setError(null)

        viewModelScope.launch {
            try {
                val movies = repository.searchMovies(query)
                _movieList.value = movies
            } catch (e: Exception) {
                setError("Ошибка загрузки: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }

    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> = _movieDetails

    fun getMovieDetails(movieId: Int) {
        setLoading(true)
        setError(null)

        viewModelScope.launch {
            try {
                val movie = repository.getMovieDetails(movieId)
                _movieDetails.value = movie
            } catch (e: Exception) {
                setError("Ошибка загрузки фильма: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun clearMovies() {
        _movieList.value = emptyList()
    }

    fun clearMovieDetails() {
        _movieDetails.value = null
    }
}
