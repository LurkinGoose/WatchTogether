package com.example.watch_together.viewModels

import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: MovieRepository) : BaseViewModel() {

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    private val _hasViewedFavorites = MutableStateFlow(false)
    val hasViewedFavorites: StateFlow<Boolean> = _hasViewedFavorites

    init {
        checkHasViewedFavorites()
    }

    fun checkHasViewedFavorites() {
        val hasViewed = repository.getHasViewedFavorites()
        println("checkHasViewedFavorites: $hasViewed")
        _hasViewedFavorites.value = hasViewed
    }

    fun markFavoritesAsViewed() {
        repository.setHasViewedFavorites(true)
        _hasViewedFavorites.value = true
        println("markFavoritesAsViewed: true")
    }


    fun loadFavorites() {
        setLoading(true)
        setError(null)

        viewModelScope.launch {
            try {
                _favoriteMovies.value = repository.getFavoriteMovies()
            } catch (e: Exception) {
                setError("Ошибка загрузки избранного: ${e.localizedMessage}")
            } finally {
                setLoading(false)
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
