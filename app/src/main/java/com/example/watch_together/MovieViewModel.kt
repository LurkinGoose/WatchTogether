package com.example.watch_together

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val searchListState = LazyListState()
    val favoritesListState = LazyListState()

    // Функция для поиска фильмов
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
}
