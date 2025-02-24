package com.example.watch_together.viewModels


import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.Movie
import com.example.watch_together.movieApiService.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MovieViewModel : ViewModel() {

    private val _movieList = MutableStateFlow<List<Movie>>(emptyList())
    val movieList: StateFlow<List<Movie>> get() = _movieList

    // Добавим состояния для ошибок и загрузки
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    // Добавляем состояния списка
    val saveListState = LazyListState()

    // Функция для поиска фильмов и вывода списка
    fun searchMovies(query: String) {
        _loading.value = true
        _errorMessage.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.searchMovies(query = query)
                withContext(Dispatchers.Main) {
                    _movieList.value = response.results
                    _loading.value = false
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Ошибка HTTP: ${e.message()}"
                    _loading.value = false
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Ошибка сети: ${e.message}"
                    _loading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Неизвестная ошибка: ${e.localizedMessage}"
                    _loading.value = false
                }
            }
        }
    }

    fun clearMovies() {
        _movieList.value = emptyList()
    }


    private val _movieDetails = MutableStateFlow<Movie?>(null)
    val movieDetails: StateFlow<Movie?> get() = _movieDetails

    fun getMovieDetails(movieId: Int) {
        _loading.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val movie = RetrofitInstance.api.getMovieById(movieId)
                withContext(Dispatchers.Main) {
                    _movieDetails.value = movie
                    _loading.value = false
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Ошибка HTTP: ${e.message()}"
                    _loading.value = false
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Ошибка сети: ${e.message}"
                    _loading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Неизвестная ошибка: ${e.localizedMessage}"
                    _loading.value = false
                }
            }
        }
    }

    fun clearMovieDetails() {
        _movieDetails.value = null
    }
}
