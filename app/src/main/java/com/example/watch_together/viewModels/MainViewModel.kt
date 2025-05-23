package com.example.watch_together.viewModels

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.CastMember
import com.example.watch_together.models.CrewMember
import com.example.watch_together.models.MediaItem
import com.example.watch_together.models.MediaType
import com.example.watch_together.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaItemState(
    val topRated: List<MediaItem> = emptyList(),
    val popular: List<MediaItem> = emptyList(),
    val nowPlayingOrOnAir: List<MediaItem> = emptyList(),
    val upcoming: List<MediaItem> = emptyList(),
    val trending: List<MediaItem> = emptyList(),
    val details: MediaItem? = null,
)

data class MainState(
    val movies: MediaItemState = MediaItemState(),
    val series: MediaItemState = MediaItemState(),
    val searchResults: List<MediaItem> = emptyList(),
    val favorites: List<MediaItem> = emptyList(),
    val hasNewFavorites: Boolean = false,
    val scrollToTop: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _mainState = MutableStateFlow(MainState())
    val mainState: StateFlow<MainState> = _mainState

    // LazyListState для фильмов
    val moviesTopRatedState = LazyListState()
    val moviesPopularState = LazyListState()
    val moviesUpcomingState = LazyListState()
    val moviesTrendingState = LazyListState()

    // LazyListState для сериалов
    val seriesTopRatedState = LazyListState()
    val seriesPopularState = LazyListState()
    val seriesOnTheAirState = LazyListState()
    val seriesTrendingState = LazyListState()

    val homeColumnState = LazyListState()
    val searchColumnState = LazyListState()
    val favoritesColumnState = LazyListState()

    init {
        loadMedia(MediaType.MOVIE)
        loadMedia(MediaType.TV)
        loadFavorites()
    }

    private fun filterUnique(items: List<MediaItem>, seen: MutableSet<Pair<Int, MediaType>> = mutableSetOf()): List<MediaItem> {
        return items.filter { seen.add(it.itemId to it.mediaType) }
    }

    // region Media Loading

    private fun loadMedia(type: MediaType) {
        val categories = when (type) {
            MediaType.MOVIE -> listOf("top_rated", "popular", "upcoming", "trending")
            MediaType.TV -> listOf("top_rated", "popular", "on_the_air", "trending")
        }
        categories.forEach { category ->
            loadMediaCategory(type, category)
        }
    }

    private fun loadMediaCategory(type: MediaType, category: String) {
        viewModelScope.launch {
            _mainState.update { it.copy(isLoading = true, error = null) }
            try {
                val items = filterUnique(repository.getMediaItems(type, category))
//                items.forEach { item ->
//                    Log.d("MainViewModel", " - ${item.titleText} (${item.itemId})")
//                }
                _mainState.update {
                    when (type) {
                        MediaType.MOVIE -> it.copy(movies = updateCategory(it.movies, category, items), isLoading = false)
                        MediaType.TV -> it.copy(series = updateCategory(it.series, category, items), isLoading = false)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading [$category] type [$type]: ${e.message}", e)
                _mainState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    fun searchMedia(query: String) {
        viewModelScope.launch {
            _mainState.update { it.copy(isLoading = true, error = null) }
            try {
                // Запускаем оба поиска параллельно, либо по очереди
                val movies = repository.searchMedia(query, MediaType.MOVIE)
                val series = repository.searchMedia(query, MediaType.TV)

                // Объединяем результаты и фильтруем дубликаты по mediaType + itemId
                val combined = (movies + series)
//                    .sortedByDescending { it.popularity } // если есть поле популярности

                _mainState.update {
                    it.copy(searchResults = combined, isLoading = false)
                }
            } catch (e: Exception) {
                _mainState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    fun getMediaDetails(id: Int, type: MediaType) {
        viewModelScope.launch {
            _mainState.update { it.copy(isLoading = true, error = null) }
            try {
                val details = repository.getMediaDetailsById(id, type)
                _mainState.update {
                    when (type) {
                        MediaType.MOVIE -> it.copy(movies = it.movies.copy(details = details), isLoading = false)
                        MediaType.TV -> it.copy(series = it.series.copy(details = details), isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _mainState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    private fun updateCategory(
        state: MediaItemState,
        category: String,
        items: List<MediaItem>
    ): MediaItemState {
        return when (category) {
            "top_rated" -> state.copy(topRated = items)
            "popular" -> state.copy(popular = items)
            "on_the_air" -> state.copy(nowPlayingOrOnAir = items)
            "upcoming" -> state.copy(upcoming = items)
            "trending" -> state.copy(trending = items)
            else -> state
        }
    }

    // endregion

    // region Favorites

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                repository.getAllFavorites()
                    .onStart {
                        val currentMarker = repository.getHasNewFavorites()
                        _mainState.update { it.copy(isLoading = true, error = null, hasNewFavorites = currentMarker) }
                    }
                    .collect { newFavorites ->
                        _mainState.update {
                            it.copy(
                                favorites = newFavorites,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _mainState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    private val recentlyAddedFavorites = mutableSetOf<Pair<Int, MediaType>>()

    fun addToFavorites(itemId: Int, type: MediaType) {
        viewModelScope.launch {
            try {
                repository.addToFavorites(itemId, type)
                recentlyAddedFavorites.add(itemId to type)
                repository.setHasNewFavorites(true)
                _mainState.update { it.copy(hasNewFavorites = true) }
            } catch (e: Exception) {
                _mainState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun removeFromFavorites(itemId: Int, type: MediaType) {
        viewModelScope.launch {
            try {
                repository.removeFromFavorites(itemId, type)
                val key = itemId to type
                if (recentlyAddedFavorites.contains(key)) {
                    recentlyAddedFavorites.remove(key)
                    Log.d("MainViewModel", "Removed from recently added: $key")
                    Log.d("MainViewModel", "Remaining recentlyAddedFavorites: $recentlyAddedFavorites")
                    if (recentlyAddedFavorites.isEmpty()) {
                        Log.d("MainViewModel", "recentlyAddedFavorites is empty -> clearing marker")
                        clearNewFavoritesMarker()
                    }
                } else {
                    Log.d("MainViewModel", "$key was not in recentlyAddedFavorites")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error removing from favorites: ${e.message}", e)
                _mainState.update { it.copy(error = e.localizedMessage) }
            }
        }
    }

    fun clearNewFavoritesMarker() {
        viewModelScope.launch {
            Log.d("MainViewModel", "Trying to clear marker. recentlyAddedFavorites = $recentlyAddedFavorites")
            if (recentlyAddedFavorites.isNotEmpty()) {
                recentlyAddedFavorites.clear()
            }
            repository.setHasNewFavorites(false)
            _mainState.update {
                Log.d("MainViewModel", "Before update: hasNewFavorites = ${it.hasNewFavorites}")
                val updated = it.copy(hasNewFavorites = false)
                Log.d("MainViewModel", "After update: hasNewFavorites = ${updated.hasNewFavorites}")
                updated
            }
            Log.d("MainViewModel", "Final state: ${_mainState.value.hasNewFavorites}")
        }
    }

    // endregion

    // region UI Utils

    fun clearError() {
        _mainState.update { it.copy(error = null) }
    }

    fun triggerScrollToTop() {
        _mainState.update { it.copy(scrollToTop = true) }
    }

    fun clearScrollToTop() {
        _mainState.update { it.copy(scrollToTop = false) }
    }

    // endregion
}
